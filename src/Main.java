import main.InvertedIndexcc;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        InvertedIndexcc index = new InvertedIndexcc();


        String folderPath = "Koleksi";
        File folder = new File(folderPath);


        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                String content = Files.readString(file.toPath()).toLowerCase();
                index.addDocument(file.getName(), content);
            }
        }

        index.printIndex();

        Scanner scanner = new Scanner(System.in);
        System.out.println("\nMasukkan kata kunci pencarian:");
        String query = scanner.nextLine();

        Map<String, List<String>> results = index.searchWithSnippets(query);

        System.out.println("\nHasil pencarian untuk \"" + query + "\":");
        if (results.isEmpty()) {
            System.out.println("Tidak ada dokumen ditemukan.");
        } else {
            for (Map.Entry<String, List<String>> entry : results.entrySet()) {
                System.out.println("Kata Kunci = \"" + query + "\"");
                System.out.println("Ditemukan dalam: " + entry.getKey());
                for (String snippet : entry.getValue()) {
                    System.out.println(snippet);
                }
                System.out.println();
            }
        }
    }
}