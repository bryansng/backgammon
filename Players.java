import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Players implements Iterable<Player>, Iterator<Player> {
    // Players creates and groups two Players

    public static int NUM_PLAYERS = 2;

    private ArrayList<Player> players;
    private int currentPlayer;
    private Iterator<Player> iterator;

    Players() {
        players = new ArrayList<Player>();
        players.add(new Player(0,"RED", new Color(255,51,51)));
        players.add(new Player(1,"GREEN",Color.GREEN));
        currentPlayer = 0;
    }

    public void setCurrentAccordingToDieRoll() {
        if (players.get(0).getDice().getDie(0) > players.get(1).getDice().getDie(0)) {
            currentPlayer = 0;
        } else {
            currentPlayer = 1;
        }
    }

    public Player getCurrent() {
        return players.get(currentPlayer);
    }

    public void advanceCurrentPlayer() {
        currentPlayer++;
        if (currentPlayer == NUM_PLAYERS) {
            currentPlayer = 0;
        }
    }

    public Player get(int id) {
        return players.get(id);
    }

    public boolean isEqualDice() {
        return players.get(0).getDice().getDie(0) == players.get(1).getDice().getDie(0);
    }

    public Dice getOneDieFromEachPlayer() {
        return new Dice(get(0).getDice().getDie(0),get(1).getDice().getDie(0));
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Player next() {
        return iterator.next();
    }

    public Player getOpposingPlayer(Player player) {
        if (player.getId()==0) {
            return players.get(1);
        } else {
            return players.get(0);
        }
    }

    public void reset() {
        for (int i=0; i<Backgammon.NUM_PLAYERS; i++) {
            players.get(i).reset();
        }
    }

    public Iterator<Player> iterator() {
        iterator = players.iterator();
        return iterator;
    }
}
