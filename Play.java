import java.util.ArrayList;
import java.util.Iterator;

public class Play implements Iterable<Move>, Iterator<Move>  {
    // A Play consists of a series of Moves

    ArrayList<Move> moves;
    private Iterator<Move> iterator;

    Play() {
        moves = new ArrayList<Move>();
    }

    Play(Move move) {
        moves = new ArrayList<Move>();
        moves.add(move);
    }

    Play(Play play) {
        this.moves = new ArrayList<Move>();
        for (Move move : play) {
            this.moves.add(move);
        }
    }

    public void prepend(Move move) {
        moves.add(0,move);
    }

    public String toString() {
        String text = "";
        boolean firstMove = true;
        for (Move move : moves) {
            if (firstMove) {
                text = "" + move;
                firstMove = false;
            } else {
                text = text + "  " + move;
            }
        }
        return text;
    }

    public int numberOfMoves() {
        return moves.size();
    }

    public boolean contains(Move query) {
        for (Move move : moves) {
            if (move.equals(query)) {
                return true;
            }
        }
        return false;
    }

    private void remove(Move query) {
        for (int i=0; i<moves.size(); i++) {
            if (moves.get(i).equals(query)) {
                moves.remove(i);
                return;
            }
        }
    }

    public Move getMove(int index) {
        return moves.get(index);
    }

    public boolean matches(Play query) {
        // checks if two plays are equivalent
        boolean matches = true;
        if (this.numberOfMoves()==0 && query.numberOfMoves()==0) {
            matches = true;
        } else {
            if (this.numberOfMoves() == query.numberOfMoves()) {
                // check for same moves in different order
                Play duplicatePlay = new Play(this);
                for (Move move : query) {
                    if (duplicatePlay.contains(move)) {
                        duplicatePlay.remove(move);
                    } else {
                        matches = false;
                    }
                }
                // check for single checker moving twice and no hits
                if (!matches) {
                    if (this.numberOfMoves()==2
                            && moves.get(0).getFromPip()==query.getMove(0).getFromPip()
                            && moves.get(1).getToPip()==query.getMove(1).getToPip()
                            && !moves.get(0).isHit() && !query.getMove(0).isHit() ) {
                        matches = true;
                    }
                }
            } else {
                matches = false;
            }
        }
        return matches;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Move next() {
        return iterator.next();
    }

    public Iterator<Move> iterator() {
        iterator = moves.iterator();
        return iterator;
    }

}
