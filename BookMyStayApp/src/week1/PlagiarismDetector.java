package week1;

import java.util.*;

class PlagiarismDetector {

    // n-gram size
    private int N = 5;

    // ngram -> documents containing it
    private HashMap<String, Set<String>> ngramIndex;

    // documentId -> list of ngrams
    private HashMap<String, List<String>> documentNgrams;

    public PlagiarismDetector() {
        ngramIndex = new HashMap<>();
        documentNgrams = new HashMap<>();
    }

    // Add document to database
    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNGrams(text);

        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {

            ngramIndex.putIfAbsent(gram, new HashSet<>());
            ngramIndex.get(gram).add(docId);
        }
    }

    // Generate n-grams from text
    private List<String> generateNGrams(String text) {

        List<String> grams = new ArrayList<>();

        String[] words = text.toLowerCase().split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            grams.add(gram.toString().trim());
        }

        return grams;
    }

    // Analyze document for plagiarism
    public void analyzeDocument(String docId) {

        List<String> ngrams = documentNgrams.get(docId);

        if (ngrams == null) {
            System.out.println("Document not found.");
            return;
        }

        System.out.println("\nAnalyzing " + docId);
        System.out.println("Extracted " + ngrams.size() + " n-grams");

        HashMap<String, Integer> similarityCount = new HashMap<>();

        for (String gram : ngrams) {

            Set<String> docs = ngramIndex.get(gram);

            if (docs != null) {

                for (String otherDoc : docs) {

                    if (!otherDoc.equals(docId)) {

                        similarityCount.put(otherDoc,
                                similarityCount.getOrDefault(otherDoc, 0) + 1);
                    }
                }
            }
        }

        for (String otherDoc : similarityCount.keySet()) {

            int matches = similarityCount.get(otherDoc);

            double similarity =
                    (matches * 100.0) / ngrams.size();

            System.out.println("Found " + matches +
                    " matching n-grams with \"" + otherDoc + "\"");

            System.out.printf("Similarity: %.2f%%", similarity);

            if (similarity > 60) {
                System.out.println(" (PLAGIARISM DETECTED)");
            } else if (similarity > 15) {
                System.out.println(" (suspicious)");
            } else {
                System.out.println();
            }
        }
    }
}

public class PlagiarismSystem {

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String essay1 =
                "Artificial intelligence is transforming modern technology. "
                        + "Machine learning allows systems to learn from data "
                        + "and improve their performance automatically.";

        String essay2 =
                "Artificial intelligence is transforming modern technology. "
                        + "Machine learning helps computers learn from data "
                        + "and improve performance automatically.";

        String essay3 =
                "Climate change affects ecosystems worldwide. "
                        + "Rising temperatures impact wildlife and agriculture.";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);
        detector.addDocument("essay_123.txt", essay1);

        detector.analyzeDocument("essay_123.txt");
    }
}