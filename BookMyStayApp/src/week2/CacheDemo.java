import java.util.*;

class VideoData {
    String videoId;
    String content;

    public VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

class LRUCache extends LinkedHashMap<String, VideoData> {

    private int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
        return size() > capacity;
    }
}

class MultiLevelCache {

    private LRUCache L1 = new LRUCache(10000);
    private Map<String, VideoData> L2 = new HashMap<>();
    private Map<String, VideoData> L3 = new HashMap<>();

    public MultiLevelCache() {

        // simulate DB
        L3.put("video_999", new VideoData("video_999", "Movie data"));
    }

    public VideoData getVideo(String id) {

        if (L1.containsKey(id)) {
            System.out.println("L1 HIT");
            return L1.get(id);
        }

        if (L2.containsKey(id)) {
            System.out.println("L2 HIT");
            VideoData data = L2.get(id);
            L1.put(id, data);
            return data;
        }

        if (L3.containsKey(id)) {
            System.out.println("L3 HIT");
            VideoData data = L3.get(id);
            L2.put(id, data);
            return data;
        }

        return null;
    }
}

public class CacheDemo {

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        cache.getVideo("video_999");
        cache.getVideo("video_999");
    }
}