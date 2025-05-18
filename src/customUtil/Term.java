package customUtil;

public class Term implements Comparable<Term> {
    private String term;
    private int df;
    private double idf;

    public Term(String term) {
        this.term = term;
        this.df = 0;
        this.idf = 0;
    }

    public Term(String term, int df) {
        this.term = term;
        this.df = df;
        this.idf = 0;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getDf() {
        return df;
    }

    public void setDf(int df) {
        this.df = df;
    }

    public void setIdf(double idf) {
        this.idf = idf;
    }

    public double getIdf() {
        return idf;
    }

    @Override
    public int compareTo(Term o) {
        return 0;
    }
}
