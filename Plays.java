import java.util.ArrayList;
import java.util.Iterator;

public class Plays implements Iterable<Play>, Iterator<Play> {
    // Plays is a list of legal plays that a player can make in the current turn

    ArrayList<Play> plays;
    private Iterator<Play> iterator;

    Plays() {
        plays = new ArrayList<Play>();
    }

    Plays(Plays plays) {
        this.plays = new ArrayList<Play>();
        for (Play play : plays) {
            this.plays.add(play);
        }
    }

    public void add(Play play) {
        plays.add(play);
    }

    public void add(Plays plays) {
        for (Play play : plays) {
            this.plays.add(play);
        }
    }

    public int number() {
        return plays.size();
    }

    public Play get(int index) {
        return plays.get(index);
    }

    public void prependAll(Move move) {
        for (Play play : plays) {
            play.prepend(move);
        }
    }

    public boolean contains(Play query) {
        for (Play play : plays) {
            if (play.matches(query)) {
                return true;
            }
        }
        return false;
    }

    public void removeDuplicatePlays() {
        // Remove plays that give the same board position
        Plays duplicatePlays = new Plays(this);
        plays.clear();
        for (Play play : duplicatePlays) {
            if (!this.contains(play)) {
                this.add(play);
            }
        }
    }

    public void removeIncompletePlays() {
        // Remove plays with too few moves
        int maxNumberOfMoves = 0;
        for (Play play : plays) {
            if (play.numberOfMoves() > maxNumberOfMoves) {
                maxNumberOfMoves = play.numberOfMoves();
            }
        }
        Plays duplicatePlays = new Plays(this);
        plays.clear();
        for (Play play : duplicatePlays) {
            if (play.numberOfMoves() == maxNumberOfMoves) {
                plays.add(play);
            }
        }
        // Remove single die plays that don't play the largest die
        if (maxNumberOfMoves==1) {
            int maxMove = 0;
            for (Play play : plays) {
                if (play.getMove(0).getPipDifference() > maxMove) {
                    maxMove = play.getMove(0).getPipDifference();
                }
            }
            duplicatePlays = new Plays(this);
            plays.clear();
            for (Play play : duplicatePlays) {
                if (play.getMove(0).getPipDifference() == maxMove) {
                    plays.add(play);
                }
            }
        }
    }


    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Play next() {
        return iterator.next();
    }

    public Iterator<Play> iterator() {
        iterator = plays.iterator();
        return iterator;
    }

}
