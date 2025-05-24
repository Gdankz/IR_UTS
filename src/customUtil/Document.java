package customUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Document implements Comparable<Document> {
    private String name;
    private int tf;

    public Document() {
        this.name = "";
        this.tf = 0;
    }

    public Document(String document) {
        this.name = document;
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

    public int getTF() {
        return tf;
    }

    public void setTF(int tf) {
        this.tf = tf;
    }

    @Override
    public int compareTo(Document o) {
        List<Object> alp1 = getAlphabetic(this.name);
        List<Object> alp2 = getAlphabetic(o.name);

        int length = Math.min(alp1.size(), alp2.size());

        for (int i = 0; i < length; i++) {
            Object obj1 = alp1.get(i);
            Object obj2 = alp2.get(i);

            int comp;

            if (obj1 instanceof String && obj2 instanceof String) {
                comp = ((String) obj1).compareTo((String) obj2);
            } else if (obj1 instanceof Integer && obj2 instanceof Integer) {
                comp = ((Integer) obj1).compareTo((Integer) obj2);
            } else {
                comp = (obj1 instanceof String) ? -1 : 1;
            }

            if (comp != 0) {
                return comp;
            }
        }

        return Integer.compare(alp1.size(), alp2.size());
    }

    private static List<Object> getAlphabetic(String str) {
        List<Object> alp = new ArrayList<>();

        Pattern pattern = Pattern.compile("([a-zA-Z]+)|([0-9]+)");
        Matcher matcher = pattern.matcher(str.toLowerCase());

        while(matcher.find()) {
            if (matcher.group(1) != null) {
                alp.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                try {
                    alp.add(Integer.parseInt(matcher.group(2)));
                } catch (NumberFormatException e) {
                    alp.add(matcher.group(2));
                }
            }
        }

        return alp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return name.equals(document.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return this.name + ": " + this.tf;
    }

    public static void main(String[] args) {
        LinkedListOrdered<Document> docs = new LinkedListOrdered<>();
        docs.addSort(new Document("9"));
        docs.addSort(new Document("123"));
        docs.addSort(new Document("docs"));
        docs.addSort(new Document("docs1"));
        docs.addSort(new Document("3"));
        docs.addSort(new Document("2"));
        docs.addSort(new Document("8"));
        docs.addSort(new Document("23"));
        docs.addSort(new Document("456"));
        docs.addSort(new Document("789"));
        docs.addSort(new Document("1"));
        System.out.println(docs);
    }
}
