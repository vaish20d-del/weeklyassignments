import java.util.concurrent.ConcurrentHashMap;

class TokenBucket {
    long tokens;
    long maxTokens;
    double refillRate; // tokens per second
    long lastRefillTime;

    public TokenBucket(long maxTokens, double refillRate) {
        this.tokens = maxTokens;
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.lastRefillTime = System.currentTimeMillis();
    }

    synchronized boolean allowRequest() {
        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        double tokensToAdd = (now - lastRefillTime) / 1000.0 * refillRate;

        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + (long) tokensToAdd);
            lastRefillTime = now;
        }
    }

    synchronized long getRemainingTokens() {
        refill();
        return tokens;
    }

    synchronized long retryAfterSeconds() {
        return (long) (1 / refillRate);
    }
}

class RateLimiter {

    private ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    private final long MAX_REQUESTS = 1000;
    private final double REFILL_RATE = 1000.0 / 3600.0;

    public String checkRateLimit(String clientId) {

        TokenBucket bucket = buckets.computeIfAbsent(
                clientId,
                id -> new TokenBucket(MAX_REQUESTS, REFILL_RATE)
        );

        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getRemainingTokens() + " remaining)";
        } else {
            return "Denied (retry after " + bucket.retryAfterSeconds() + "s)";
        }
    }

    public String getRateLimitStatus(String clientId) {

        TokenBucket bucket = buckets.get(clientId);

        if (bucket == null) {
            return "Client not found";
        }

        long remaining = bucket.getRemainingTokens();
        return "{used:" + (MAX_REQUESTS - remaining) +
                ", limit:" + MAX_REQUESTS +
                ", reset:" + bucket.retryAfterSeconds() + "}";
    }
}

public class Main {
    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.getRateLimitStatus("abc123"));
    }
}