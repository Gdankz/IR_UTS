package main;

import customUtil.Stemmer;

import java.util.*;

public class InvertedIndex {
    private final Map<String, Set<String>> invertedIndex = new HashMap<>();
    private final Set<String> stopWords = new HashSet<>(Arrays.asList(
            "dan", "di", "ke", "dari", "yang", "ini", "adalah", "itu", "untuk", "pada"
    ));

    private final Map<String, String> documentsContents = new HashMap<>();

    public void addDocument(String docId, String text) {
        documentsContents.put(docId, text);

        String[] words = text.split("\\W+");
        for (String word : words) {
            String stemmed = Stemmer.stem(word);
            if (!stemmed.isEmpty() && !stopWords.contains(stemmed)) {
                invertedIndex.computeIfAbsent(stemmed, k -> new HashSet<>()).add(docId);
            }
        }
    }

    public Map<String, List<String>> searchWithSnippets(String query) {
        String[] terms = query.toLowerCase().split("\\W+");
        Set<String> matchedDocs = new HashSet<>();

        // Cari dokumen yang cocok
        for (int i = 0; i < terms.length; i++) {
            String term = Stemmer.stem(terms[i]);
            if (term.isEmpty() || !invertedIndex.containsKey(term)) {
                return new HashMap<>();
            }

            if (i == 0) {
                matchedDocs.addAll(invertedIndex.get(term));
            } else {
                matchedDocs.retainAll(invertedIndex.get(term));
            }
        }

        // Ambil snippet dari dokumen-dokumen tersebut
        Map<String, List<String>> results = new HashMap<>();
        for (String docId : matchedDocs) {
            results.put(docId, getSnippets(documentsContents.get(docId), query));
        }

        return results;
    }

    private List<String> getSnippets(String content, String query) {
        List<String> snippets = new ArrayList<>();
        String[] terms = query.toLowerCase().split("\\W+");
        List<String> keywords = Arrays.stream(terms)
                .map(Stemmer::stem)
                .filter(s -> !s.isEmpty())
                .toList();

        String[] words = content.split("\\s+");
        for(int i = 0; i < words.length; i++) {
            String word = words[i].replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            String stemmed = Stemmer.stem(word);

            if (keywords.contains(stemmed)) {
                int start = Math.max(0, i - 5);
                int end = Math.min(words.length, i + 6);

                StringBuilder snippet = new StringBuilder("...");
                for (int j = start; j < end; j++) {
                    if (j == i) {
                        snippet.append("[[").append(words[j]).append("]] ");
                    } else {
                        snippet.append(words[j]).append(" ");
                    }
                }
                snippet.append("...");

                snippets.add(snippet.toString().trim());
            }
        }

        return snippets;
    }

    private boolean containsKeyword(String sentence, String query) {
        String[] terms = query.toLowerCase().split("\\W+");
        for (String term : terms) {
            String stemmed = Stemmer.stem(term);
            if (sentence.toLowerCase().contains(stemmed)) {
                return true;
            }
        }
        return false;
    }



    public void printIndex() {
        System.out.println("Inverted Index:");
        for (Map.Entry<String, Set<String>> entry : invertedIndex.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
