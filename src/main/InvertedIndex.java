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
//    private void makeInvertedIndex() {
//
//        File folder = new File(PATH);
//        if (!folder.exists() || !folder.isDirectory()) {
//            System.out.println("Folder tidak ada");
//            return;
//        }
//
//
//        for (File file : folder.listFiles()) {
//            if (file.isFile() && file.getName().startsWith("doc") && file.getName().endsWith(".txt")) {
//                int docId = extractDocId(file.getName());
//
//                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//                    String line;
//                    while ((line = br.readLine()) != null) {
//
//                        String[] words = line.toLowerCase().split("\\W+");
//
//                        for (String word : words) {
//                            if (word.isEmpty()) continue;
//
//                            word = word.replaceAll(willIgnore, "");
//                            String stemmed = Stemmer.stem(word);
//
//                            if (stemmed.isEmpty()) continue;
//
//                            Term existingTerm = findTermInDictionary(stemmed);
//
//                            if (existingTerm == null){
//                                Term newTerm = new Term(stemmed,1);
//                                String docName = "doc" + docId;
//                                Document newDoc = new Document(docName, 1);
//                                newTerm.setDocs(newDoc);
//                                dictionary.addSort(newTerm);
//                            } else {
//                                if (!containsDoc(existingTerm, docId)){
//                                    existingTerm.setDF(existingTerm.getDF() + 1);
//                                }
//
//                                addDocumentToTerm(existingTerm, docId);
//                            }
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        System.out.println("Dictionary berhasil dibuat dengan " + dictionary.size() + " term.");
//    }

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

        System.out.println("intersect: " + result);

    }

    // bikin fungsi buat ngebaca query terus output postinglist dari query itu
    private LinkedListOrdered<Term> getSelected(String query) {
        String[] terms = query.toLowerCase().split("\\W+");

        for (String term : terms){
            System.out.println(term);
        }

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

        // masih error: ga mau deteksi file yang dipilih
        for (File file : folder.listFiles()) {
            if (file.equals(filename)) {
                selectedTerms = partInvertedIndex(selectedTerms, file);
                break;
            }
        }

        if (selectedTerms.isEmpty()) {
            System.out.println("File tidak ditemukan");
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
    }

    private void makeInvertedIndex() {
        File folder = new File(PATH);
        makeMaster();

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Folder tidak ada");
            return;
        }

        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().startsWith("doc") && file.getName().endsWith(".txt")) {
                dictionary = partInvertedIndex(dictionary, file);
            }
        }
    }

    public LinkedListOrdered<Term> partInvertedIndex(LinkedListOrdered<Term> dictionary, File file) {
        int docId = extractDocId(file.getName());

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
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
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return dictionary;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Scanner sc2 = new Scanner(System.in);
        InvertedIndex index = new InvertedIndex();
        index.makeInvertedIndex();
        boolean loop = true;

        while (loop) {
            show();
            System.out.print("Pilih: ");
            int choice = sc.nextInt();

            if (choice == 1) {
                System.out.print("Masukkan term: ");
                String term = sc2.nextLine();
                index.search(term);

            } else if (choice == 2) {
                boolean thiswillLoop = true;
                showList();
                System.out.print("Pilih: ");
                while(thiswillLoop) {


                    int pilihan = sc.nextInt();

                    if (pilihan <= masterDocs.size()) {
                        index.selectedFile(masterDocs.get(pilihan-1));
                        thiswillLoop = false;
                    } else if (pilihan == 0) {
                        thiswillLoop = false;
                    } else if (pilihan == 99) {
                        index.showDics();
                    } else {
                        System.out.println("Pilihan tidak valid");
                        System.out.print("Pilih: ");
                    }
                }

            } else if (choice == 3) {
                loop = false;
            }

            System.out.println("");
        }
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
