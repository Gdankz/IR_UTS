package customUtil;

import java.util.ArrayList;
import java.util.List;

public class Term implements Comparable<Term> {
    private String term;
    private int df;
    private double idf;
    private LinkedListOrdered<Document> docs;

    public Term(String term) {
        this.term = term;
        this.df = -1;
        this.idf = 0;
        this.docs = new LinkedListOrdered<>();
    }

    public Term(String term, int df) {
        this.term = term;
        this.df = df;
        this.idf = 0;
        this.docs = new LinkedListOrdered<>();
    }

    public String getTerm() {
        return this.term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getDF() {
        return this.df;
    }

    public void setDF(int df) {
        this.df = df;
    }

    public void setIDF(double idf) {
        this.idf = idf;
    }

    public double getIDF() {
        return this.idf;
    }

    public LinkedListOrdered<Document> getDocs() {
        return this.docs;
    }

    public void setDocs(Document value) {
        this.docs.addSort(value);
    }

    private double countIDF(String term) {
        return 0.0;
    }

    @Override
    public int compareTo(Term o) {
        char[] alp1 = this.term.toCharArray();
        char[] alp2 = o.term.toCharArray();
        int length = Math.min(alp1.length, alp2.length);

        for (int i = 0; i < length; i++) {
            char c1 = alp1[i];
            char c2 = alp2[i];

            int comp = Character.compare(c1, c2);

            if (comp != 0) {
                return comp;
            }
        }

        return Integer.compare(alp1.length, alp2.length);
    }

    @Override
    public String toString() {
        return this.term + " (" + this.df + ")";
    }
}
