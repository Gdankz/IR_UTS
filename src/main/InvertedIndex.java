package main;
import customUtil.Stemmer;
import customUtil.Term;
import customUtil.LinkedListOrdered;
import customUtil.Document;
import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.util.*;

public class InvertedIndex {
    private static final String PATH = "src/resources";
    private static final String willIgnore = "[.,!?:;'\"]";
    private static LinkedListOrdered<File> masterDocs = new LinkedListOrdered<>();
    private LinkedListOrdered<Term> dictionary = new LinkedListOrdered<>();

    // bikin proses dictionary dan postinglist
    private void makeInvertedIndex() {

        File folder = new File(PATH);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Folder tidak ada");
            return;
        }

        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().startsWith("doc") && file.getName().endsWith(".txt")) {
                int docId = extractDocId(file.getName());

                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {

                        String[] words = line.toLowerCase().split("\\W+");

                        for (String word : words) {
                            if (word.isEmpty()) continue;

                            word = word.replaceAll(willIgnore, "");
                            String stemmed = Stemmer.stem(word);

                            if (stemmed.isEmpty()) continue;

                            Term existingTerm = findTermInDictionary(stemmed);

                            if (existingTerm == null){
                                Term newTerm = new Term(stemmed,1);
                                String docName = "doc" + docId;
                                Document newDoc = new Document(docName, 1);
                                newTerm.setDocs(newDoc);
                                dictionary.addSort(newTerm);
                            } else {
                                if (!containsDoc(existingTerm, docId)){
                                    existingTerm.setDF(existingTerm.getDF() + 1);
                                }

                                addDocumentToTerm(existingTerm, docId);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Dictionary berhasil dibuat dengan " + dictionary.size() + " term.");
    }

    private int extractDocId(String fileName){
        String numberOnly = fileName.replaceAll("[^0-9]", "");
        return Integer.parseInt(numberOnly);
    }

    private boolean containsDoc(Term term, int docId){
        String docName = "doc" + docId;
        LinkedListOrdered<Document> docs = term.getDocs();

        for (Document doc : docs){
            if (doc.getName().equals(docName)){
                return true;
            }
        }
        return false;
    }

    private Term findTermInDictionary(String targetTerm){
        for (Term term : dictionary){
            if (term.getTerm().equals(targetTerm)){
                return term;
            }
        }
        return null;
    }

    private void showDics() {
        ListIterator<Term> iter = dictionary.listIterator();

        while (iter.hasNext()) {
            Term term = iter.next();
            System.out.print(term + ": " + term.getDocs() + "\n");
        }
    }

    private void addDocumentToTerm(Term term, int docId){
        String docName = "doc" + docId;
        LinkedListOrdered<Document> docs = term.getDocs();
        
        for (Document doc : docs){
            if (doc.getName().equals(docName)){
                doc.setTF(doc.getTF() + 1);
                return;
            }
        }
        Document newDoc = new Document(docName,1);
        docs.addSort(newDoc);
    }

    private void search(String query) {
        LinkedListOrdered<Term> selectedTerms = getSelected(query);

        if (selectedTerms.size() == 1) {
            System.out.println("Term ditemukan: " + selectedTerms.get(0).getTerm() + "-> " + selectedTerms.get(0).getDocs());
            return;
        } else if (selectedTerms.isEmpty()) {
            System.out.println("Term tidak ditemukan");
            return;
        }

        LinkedListOrdered<Document> result = selectedTerms.get(0).getDocs();

        for (int i = 1; i < selectedTerms.size(); i++) {
            LinkedListOrdered<Document> docs = selectedTerms.get(i).getDocs();

            result = intersect(result, docs);
        }

        System.out.print("intersect: ");

        ListIterator<Document> it = result.listIterator();

        while (it.hasNext()) {
            Document doc = it.next();
            System.out.print(doc.getName());

            if (it.hasNext()) {
                System.out.print(", ");
            }
        }

        System.out.println();
    }


    // bikin fungsi buat ngebaca query terus output postinglist dari query itu
    private LinkedListOrdered<Term> getSelected(String query) {
        String[] terms = query.toLowerCase().split("\\W+");

        LinkedListOrdered<Term> f = new LinkedListOrdered<>();

        for (String term : terms) {
            ListIterator<Term> it = dictionary.listIterator();
            term = term.replaceAll(willIgnore, "");

            while (it.hasNext()) {
                Term curr = it.next();

                if (curr.getTerm().equals(term)) {
                    f.addSort(curr);
                }
            }
        }

        return f;
    }

    private static LinkedListOrdered<Document> intersect(LinkedListOrdered<Document> docs1, LinkedListOrdered<Document> docs2) {

        if (docs1 == null || docs2 == null || docs1.isEmpty() || docs2.isEmpty()) {
            return null;
        }

         ListIterator<Document> it1 = docs1.listIterator();
         ListIterator<Document> it2 = docs2.listIterator();
         LinkedListOrdered<Document> docs = new LinkedListOrdered<>();
         Document d1 = it1.hasNext() ? it1.next() : null;
         Document d2 = it2.hasNext() ? it2.next() : null;

         while (d1 != null && d2 != null) {

             int comp = d1.compareTo(d2);

             if (comp == 0) {
                 docs.addSort(d1);
                 d1 = it1.hasNext() ? it1.next() : null;
                 d2 = it2.hasNext() ? it2.next() : null;
             } else if (comp < 0) {
                d1 = it1.hasNext() ? it1.next() : null;
             } else if (comp > 0) {
                d2 = it2.hasNext() ? it2.next() : null;
             }
         }

        return docs;
    }

    private void selectedFile(File filename) {

        File folder = new File(PATH);
        LinkedListOrdered<Term> selectedTerms = new LinkedListOrdered<>();

        boolean found = false;

        if (folder.listFiles() == null || folder.listFiles().length == 0) {
            System.out.println("Folder tidak ada atau kosong");
            return;
        }

        for (Term term : dictionary) {
            LinkedListOrdered<Document> docs = term.getDocs();

            for (Document doc : docs) {
                if (doc.getName().equals(filename.getName())) {
                    selectedTerms.addSort(term);
                }
            }
        }

        if (!found) {
            System.out.println("File tidak ditemukan");
            return;
        }

        if (selectedTerms.isEmpty()) {
            System.out.println("File ditemukan tetapi tidak memiliki term yang valid");
            return;
        }

        ListIterator<Term> it = selectedTerms.listIterator();

        while (it.hasNext()) {
            Term curr = it.next();
            System.out.println(curr.getTerm() + ": " + curr.getDocs());
        }
    }

    private static void makeMaster() {
        File folder = new File(PATH);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Folder tidak ada");
            return;
        }

        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().startsWith("doc") && file.getName().endsWith(".txt")) {
                masterDocs.addSort(file);
            }
        }
        System.out.println("masterDocs:" + masterDocs);
    }


//    private void makeInvertedIndex() {
//        File folder = new File(PATH);
//        makeMaster();
//
//        if (!folder.exists() || !folder.isDirectory()) {
//            System.out.println("Folder tidak ada");
//            return;
//        }
//
//        for (File file : folder.listFiles()) {
//            if (file.isFile() && file.getName().startsWith("doc") && file.getName().endsWith(".txt")) {
//                dictionary = partInvertedIndex(dictionary, file);
//            }
//        }
//    }

//    public LinkedListOrdered<Term> partInvertedIndex(LinkedListOrdered<Term> dictionary, File file) {
//        int docId = extractDocId(file.getName());
//        String docName = "doc" + docId;
//        LinkedListOrdered<Term> selectedTerms = new LinkedListOrdered<>(); // Ini barusan ditambah
//
//        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//            String line;
//            while((line = br.readLine()) != null) {
//                String[] words = line.toLowerCase().split("\\W+");
//                for (String word : words) {
//                    if (word.isEmpty()) continue;
//
//                    word = word.replaceAll(willIgnore, "");
//                    String stemmed = Stemmer.stem(word);
//
//                    Term existingTerm = findTermInDictionary(stemmed);
//
//                    if (stemmed.equals("semarang")) {
//                        System.out.println(stemmed);
//                    }
//
//                    if(existingTerm == null) {
//                        Term newTerm = new Term(stemmed);
//                        Document newDoc = new Document(docName, 1);
//                        newTerm.setDocs(newDoc);
//                        newTerm.setDF(1);
//                        dictionary.addSort(newTerm);
//                        selectedTerms.addSort(newTerm);
//
//                        if (stemmed.equals("semarang")) {
//                            if (dictionary.contains(newTerm)) {
//                                System.out.println("kata semarang ada");
//                            }
//                        }
//
//                    } else {
//
//                        if (existingTerm.getTerm().equals("semarang")) {
//                            System.out.println(existingTerm.getTerm() + " sudah ada");
//                        }
//
//                        LinkedListOrdered<Document> docs = existingTerm.getDocs();
//                        boolean docExists = false;
//
//                        ListIterator<Document> docIt = docs.listIterator();
//                        while (docIt.hasNext()) {
//                            Document doc = docIt.next();
//                            if (doc.getName().equals(docName)) {
//                                doc.setTF(doc.getTF() + 1);
//                                docExists = true;
//                                break;
//                            }
//                        }
//
//                        if (!docExists) {
//                            Document newDoc = new Document(docName, 1);
//                            docs.addSort(newDoc);
//                            existingTerm.setDF(existingTerm.getDF() + 1);
//
//                        }
//
//                        boolean alreadyAdded = false;
//                        ListIterator<Term> termIt = selectedTerms.listIterator();
//                        while (termIt.hasNext()) {
//                            if (termIt.next().getTerm().equals(stemmed)) {
//                                alreadyAdded = true;
//                                break;
//                            }
//                        }
//
//                        if (!alreadyAdded) {
//                            selectedTerms.addSort(existingTerm);
//                        }
//                    }
//                }
//            }
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return selectedTerms;
//    }


    public static void main(String[] args) {
        InvertedIndex index = new InvertedIndex();
        Scanner sc = new Scanner(System.in);

        Runtime runtime = Runtime.getRuntime();

        runtime.gc();

        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memori terpakai SEBELUM membuat index: " + toReadableFormat(memoryBefore));

        long startTime = System.nanoTime();
        index.makeInvertedIndex();

        long endTime = System.nanoTime();

        runtime.gc();

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memori terpakai SETELAH membuat index: " + toReadableFormat(memoryAfter));

        long memoryUsed = memoryAfter - memoryBefore;
        long timeTaken = endTime - startTime;

        System.out.println("\n================ HASIL ANALISIS ===============");
        System.out.println("Memori yang digunakan untuk proses indexing: " + toReadableFormat(memoryUsed));
        System.out.println("Waktu yang dibutuhkan untuk proses indexing: " + (timeTaken / 1_000_000) + " ms");
        System.out.println("============================================\n");

        boolean loop = true;
        while (loop) {
            show();
            System.out.print("Pilih: ");
            int choice = sc.nextInt();
            sc.nextLine(); // Membersihkan sisa newline

            if (choice == 1) {
                System.out.print("Masukkan term: ");
                String term = sc.nextLine();

                runtime.gc();
                long searchMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
                long searchTimeStart = System.nanoTime();

                // Jalankan proses searching
                index.search(term);

                long searchTimeEnd = System.nanoTime();
                runtime.gc();
                long searchMemoryAfter = runtime.totalMemory() - runtime.freeMemory();

                System.out.println("\n--- Analisis Proses Pencarian ---");
                System.out.println("Memori yang digunakan untuk search: " + toReadableFormat(searchMemoryAfter - searchMemoryBefore));
                System.out.println("Waktu yang dibutuhkan untuk search: " + ((searchTimeEnd - searchTimeStart) / 1_000_000.0) + " ms");
                System.out.println("---------------------------------");

            } else if (choice == 2) {
                index.showDics();
            } else if (choice == 3) {
                loop = false;
            }
            System.out.println("");
        }
        sc.close();
    }

    /**
     * Helper method untuk mengubah byte menjadi format yang mudah dibaca (KB/MB).
     */
    public static String toReadableFormat(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }


    static void showList() {
        int i = 0;
        for (File doc : masterDocs) {
            System.out.println((i+1) + ". " + doc.getName());
            i++;
        }
        System.out.println("99. Tampilkan semua file");
        System.out.println("0. Kembali");
    }

    static void show() {
        System.out.println("1. Cari kata\n" +
                            "2. Tampilkan dictionary\n" +
                            "3. keluar\n");
    }

}
