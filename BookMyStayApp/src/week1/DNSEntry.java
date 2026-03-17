package week1;

import java.util.*;

class DNSEntry {

    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, long ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class DNSCache {

    private int capacity;

    // LRU cache using LinkedHashMap
    private LinkedHashMap<String, DNSEntry> cache;

    private long cacheHits = 0;
    private long cacheMisses = 0;

    public DNSCache(int capacity) {

        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {

            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };

        startCleanupThread();
    }

    // Resolve domain
    public synchronized String resolve(String domain) {

        long startTime = System.nanoTime();

        if (cache.containsKey(domain)) {

            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {

                cacheHits++;

                long time = System.nanoTime() - startTime;

                System.out.println("Cache HIT → " + entry.ipAddress +
                        " (lookup " + time / 1000000.0 + " ms)");

                return entry.ipAddress;
            }

            // Expired
            cache.remove(domain);
            System.out.println("Cache EXPIRED for " + domain);
        }

        // Cache miss
        cacheMisses++;

        String ip = queryUpstreamDNS(domain);

        cache.put(domain, new DNSEntry(domain, ip, 5));

        long time = System.nanoTime() - startTime;

        System.out.println("Cache MISS → Upstream query → " + ip +
                " (lookup " + time / 1000000.0 + " ms)");

        return ip;
    }

    // Simulate upstream DNS lookup
    private String queryUpstreamDNS(String domain) {

        try {
            Thread.sleep(100); // simulate network latency
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Random rand = new Random();

        return "172.217." + rand.nextInt(255) + "." + rand.nextInt(255);
    }

    // Cleanup expired entries
    private void startCleanupThread() {

        Thread cleaner = new Thread(() -> {

            while (true) {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (this) {

                    Iterator<Map.Entry<String, DNSEntry>> iterator = cache.entrySet().iterator();

                    while (iterator.hasNext()) {

                        Map.Entry<String, DNSEntry> entry = iterator.next();

                        if (entry.getValue().isExpired()) {
                            iterator.remove();
                        }
                    }
                }
            }
        });

        cleaner.setDaemon(true);
        cleaner.start();
    }

    // Cache statistics
    public void getCacheStats() {

        long total = cacheHits + cacheMisses;

        double hitRate = total == 0 ? 0 : ((double) cacheHits / total) * 100;

        System.out.println("\nCache Stats:");
        System.out.println("Hits: " + cacheHits);
        System.out.println("Misses: " + cacheMisses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }
}

public class DNSCacheSystem {

    public static void main(String[] args) throws InterruptedException {

        DNSCache cache = new DNSCache(5);

        cache.resolve("google.com");
        cache.resolve("google.com");

        cache.resolve("openai.com");
        cache.resolve("amazon.com");
        cache.resolve("facebook.com");
        cache.resolve("youtube.com");

        Thread.sleep(6000);

        cache.resolve("google.com");

        cache.getCacheStats();
    }
}