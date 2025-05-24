package customUtil;
import java.util.*;

public class LinkedListOrdered<T extends Comparable<T>> extends LinkedList<T> {

    public boolean addSort(T val) {

        ListIterator<T> iter = this.listIterator();

        while(iter.hasNext()) {
            T curr = iter.next();
            int comp = (curr).compareTo(val);

            if (comp == 0) {
                return false;
            }

            if (comp > 0) {
                iter.previous();
                iter.add(val);

                return true;
            }
        }
        iter.add(val);
        return true;
    }

    public ArrayList<T> getAll() {
        ArrayList<T> list = new ArrayList<>(this);

        return list;
    }

    public Optional<T> get(T val) {
        ListIterator<T> iter = this.listIterator();

        while(iter.hasNext()) {
            T curr = iter.next();
            int comp = curr.compareTo(val);

            if (comp > 0) {
                iter.previous();
                return Optional.of(iter.previous());
            }
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        ListIterator<T> iter = this.listIterator();

        while (iter.hasNext()) {
            T curr = iter.next();
            sb.append(curr.toString());

            if (iter.hasNext()) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        LinkedListOrdered<Document> docs = new LinkedListOrdered<>();
        docs.addSort(new Document("file1", 1));
        docs.addSort(new Document("doc12", 1));
        docs.addSort(new Document("doc11", 1));
        docs.addSort(new Document("file100", 1));
        docs.addSort(new Document("doc21", 1));
        docs.addSort(new Document("doc10", 1));
        System.out.println(docs);
    }
}
