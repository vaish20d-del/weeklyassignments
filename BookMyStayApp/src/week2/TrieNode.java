import java.util.*;

class TrieNode {

    Map<Character, TrieNode> children = new HashMap<>();
    boolean isWord = false;
    int frequency = 0;
}

class AutocompleteSystem {

    TrieNode root = new TrieNode();
    Map<String, Integer> freqMap = new HashMap<>();

    public void insert(String query) {

        freqMap.put(query, freqMap.getOrDefault(query, 0) + 1);

        TrieNode node = root;

        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }

        node.isWord = true;
        node.frequency = freqMap.get(query);
    }

    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c))
                return new ArrayList<>();
            node = node.children.get(c);
        }

        PriorityQueue<String> pq =
                new PriorityQueue<>((a, b) -> freqMap.get(a) - freqMap.get(b));

        dfs(node, prefix, pq);

        List<String> result = new ArrayList<>();

        while (!pq.isEmpty())
            result.add(pq.poll());

        Collections.reverse(result);
        return result;
    }

    private void dfs(TrieNode node, String path, PriorityQueue<String> pq) {

        if (node.isWord) {
            pq.offer(path);

            if (pq.size() > 10)
                pq.poll();
        }

        for (char c : node.children.keySet()) {
            dfs(node.children.get(c), path + c, pq);
        }
    }
}