package customUtil;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Optional;

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
}
