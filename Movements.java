import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Movements implements Iterable<Integer>, Iterator<Integer> {
    // Movements stores the list of checker movements that a player can perform as a result of a dice roll.
    // E.g. a [4,4] dice roll would allow the movements 4,4,4,4
    // E.g. a [2,1] dice roll would allow the movements 2,1 or 1,2

    ArrayList<Integer> movements;
    private Iterator<Integer> iterator;

    Movements(Dice dice) {
        movements = new ArrayList<Integer>();
        if (dice.isDouble()) {
            for (int i=0; i<4; i++) {
                movements.add(dice.getDie(0));
            }
        } else {
            movements.add(dice.getDie(0));
            movements.add(dice.getDie(1));
        }
    }

    Movements(Movements movements) {
        this.movements = new ArrayList<Integer>();
        for (Integer movement : movements) {
            this.movements.add(movement);
        }
    }

    public void reverse() {
        Collections.reverse(movements);
    }

    public int getFirst() {
        return movements.get(0);
    }

    public void removeFirst() {
        movements.remove(0);
    }

    public int number() {
        return movements.size();
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Integer next() {
        return iterator.next();
    }

    public Iterator<Integer> iterator() {
        iterator = movements.iterator();
        return iterator;
    }
}
