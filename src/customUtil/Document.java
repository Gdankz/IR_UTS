package customUtil;

public class Document implements Comparable<Document> {
    private String name;
    private int tf;

    public Document() {
        this.name = "";
        this.tf = 0;
    }

    public Document(String document, int tf) {
        this.name = document;
        this.tf = tf;
    }

    public String getName() {
        return name;
    }

    public void setName(String document) {
        this.name = document;
    }

    public int getTf() {
        return tf;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    @Override
    public int compareTo(Document o) {
        return this.getName().compareTo(o.getName());
    }
}
