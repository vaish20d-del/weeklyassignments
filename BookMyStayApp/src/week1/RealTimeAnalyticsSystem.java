package week1;

import java.util.*;
import java.util.concurrent.*;

class PageViewEvent {

    String url;
    String userId;
    String source;

    public PageViewEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

class AnalyticsEngine {

    // page -> visit count
    private ConcurrentHashMap<String, Integer> pageViews = new ConcurrentHashMap<>();

    // page -> unique visitors
    private ConcurrentHashMap<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();

    // traffic source -> count
    private ConcurrentHashMap<String, Integer> sourceCounts = new ConcurrentHashMap<>();

    // Process incoming event
    public void processEvent(PageViewEvent event) {

        // Update page views
        pageViews.put(event.url,
                pageViews.getOrDefault(event.url, 0) + 1);

        // Update unique visitors
        uniqueVisitors.putIfAbsent(event.url, ConcurrentHashMap.newKeySet());
        uniqueVisitors.get(event.url).add(event.userId);

        // Update traffic source
        sourceCounts.put(event.source,
                sourceCounts.getOrDefault(event.source, 0) + 1);
    }

    // Get Top N pages
    public List<Map.Entry<String, Integer>> getTopPages(int n) {

        PriorityQueue<Map.Entry<String, Integer>> heap =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {

            heap.offer(entry);

            if (heap.size() > n) {
                heap.poll();
            }
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<>();

        while (!heap.isEmpty()) {
            result.add(heap.poll());
        }

        Collections.reverse(result);

        return result;
    }

    // Display dashboard
    public void getDashboard() {

        System.out.println("\n===== REAL-TIME ANALYTICS DASHBOARD =====");

        List<Map.Entry<String, Integer>> topPages = getTopPages(10);

        System.out.println("\nTop Pages:");

        int rank = 1;

        for (Map.Entry<String, Integer> page : topPages) {

            int unique = uniqueVisitors.get(page.getKey()).size();

            System.out.println(rank + ". " + page.getKey()
                    + " - " + page.getValue()
                    + " views (" + unique + " unique)");

            rank++;
        }

        int totalSources = sourceCounts.values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();

        System.out.println("\nTraffic Sources:");

        for (Map.Entry<String, Integer> entry : sourceCounts.entrySet()) {

            double percent =
                    (entry.getValue() * 100.0) / totalSources;

            System.out.printf("%s: %.2f%%\n",
                    entry.getKey(), percent);
        }
    }
}

public class RealTimeAnalyticsSystem {

    public static void main(String[] args) {

        AnalyticsEngine engine = new AnalyticsEngine();

        ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        // Update dashboard every 5 seconds
        scheduler.scheduleAtFixedRate(
                engine::getDashboard,
                5,
                5,
                TimeUnit.SECONDS
        );

        // Simulate incoming traffic
        String[] pages = {
                "/article/breaking-news",
                "/sports/championship",
                "/tech/ai-update",
                "/world/politics",
                "/entertainment/movie"
        };

        String[] sources = {
                "google",
                "facebook",
                "direct",
                "twitter"
        };

        Random random = new Random();

        int userCounter = 1;

        while (true) {

            String page = pages[random.nextInt(pages.length)];
            String source = sources[random.nextInt(sources.length)];

            String userId = "user_" + userCounter++;

            PageViewEvent event =
                    new PageViewEvent(page, userId, source);

            engine.processEvent(event);

            try {
                Thread.sleep(50); // simulate traffic
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}