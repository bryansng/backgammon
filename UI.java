import javax.swing.*;
import java.awt.*;

public class UI {
    // UI is the top level interface to the user interface

    private static final int FRAME_WIDTH = 1100;
    private static final int FRAME_HEIGHT = 600;

    private final BoardPanel boardPanel;
    private final InfoPanel infoPanel;
    private final CommandPanel commandPanel;
    private final BotAPI[] bots;

    UI (Board board, Players players, Cube cube, Match match, BotAPI[] bots) {
        infoPanel = new InfoPanel();
        commandPanel = new CommandPanel();
        JFrame frame = new JFrame();
        boardPanel = new BoardPanel(board,players,cube,match);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setTitle("Backgammon");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(boardPanel, BorderLayout.LINE_START);
        frame.add(infoPanel, BorderLayout.LINE_END);
        frame.add(commandPanel, BorderLayout.PAGE_END);
        frame.setResizable(false);
        frame.setVisible(true);
        this.bots = bots;
    }

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    public void display() {
        boardPanel.refresh();
    }

    public void clearText() {
        infoPanel.clear();
    }

    public void displayString(String string) {
        infoPanel.addText(string);
    }

    public void displayStartOfGame() {
        infoPanel.clear();
        boardPanel.refresh();
        displayString("Welcome to Backgammon");
    }

    public void promptPlayerName() {
        displayString("Enter a player name:");
    }

    public void displayPlayerColor(Player player) {
        displayString(player + " uses " + player.getColorName() + " checkers.");
    }

    public void displayDieRoll(Player player) {
        displayString(player + " (" + player.getColorName() + ") rolls " + player.getDice().getDieAsString(0));
    }

    public void displayDiceEqual() {
        displayString("Equal. Roll again");
    }

    public void displayDiceWinner(Player player) {
        displayString(player + " (" + player.getColorName() + ") wins the roll and goes first.");
    }

    public void displayDiceRoll(Player player) {
        displayString(player + " (" + player.getColorName() + ") rolls " + player.getDice());
    }

    public void displayGameWinner(Player player) {
        displayString(player + " (" + player.getColorName() + ") WINS THE GAME!!!");
    }

    public String getName(Player player) {
        String textEntered = bots[player.getId()].getName();
        displayString("> " + textEntered);
        return textEntered;
    }

    public Command getCommand(Player player, Plays possiblePlays) {
        Command command;
        displayString(player + " (" + player.getColorName() + ") enter double, your move or quit:");
        do {
            String commandString = bots[player.getId()].getCommand(possiblePlays);
            displayString("> " + commandString);
            command = new Command(commandString,possiblePlays);
            if (!command.isValid()) {
                displayString("Error: Command not valid.");
            }
        } while (!command.isValid());
        return command;
    }

    public void displayPlays(Player player, Plays plays) {
        displayString(player + " (" + player.getColorName() + ") available moves...");
        int index = 1;
        for (Play play : plays) {
             displayString(index + ". " + play);
            index++;
        }
    }

    public void displayNoMove(Player player) throws InterruptedException {
        displayString(player + " has no valid moves.");
    }

    public void displayForcedMove(Player player) throws InterruptedException {
        displayString(player + " (" + player.getColorName() + ") has a forced move.");
    }


    public boolean getDoubleDecision(Player player) {
        displayString(player + " (" + player.getColorName() + ") do you wish to accept the double (y/n)?");
        boolean isYes = true, valid = false;
        do {
            String doubleDecision = bots[player.getId()].getDoubleDecision();
            displayString("> " + doubleDecision);
            doubleDecision = doubleDecision.toLowerCase().trim();
            if (doubleDecision.matches("y|n")) {
                if (doubleDecision.equals("y")) {
                    isYes = true;
                } else {
                    isYes = false;
                }
                valid = true;
            } else {
                displayString("Error: Response must be y or n.");
            }
        } while (!valid);
        return isYes;
    }

    public void displayCannotDouble(Player player) {
        displayString(player + " (" + player.getColorName() + ") cannot double at present." );
    }

    public void displayHasDoubled(Player player) {
        displayString(player + " (" + player.getColorName() + ") has already doubled this turn." );
    }

    public void displayPointsWon(Player player, int points) {
        if (points==1) {
            displayString(player + " (" + player.getColorName() + ") wins 1 point.");
        } else {
            displayString(player + " (" + player.getColorName() + ") wins " + points + " points.");
        }
    }

    public void displayScore(Players players, Match match) {
        for (int i=0; i<Backgammon.NUM_PLAYERS; i++) {
            if (players.get(i).getScore()==1) {
                displayString(players.get(i) + " (" + players.get(i).getColorName() + ") has 1 point.");
            } else {
                displayString(players.get(i) + " (" + players.get(i).getColorName() + ") has " + players.get(i).getScore() + " points.");
            }
        }
        displayString("The match length is " + match.getLength() + " points.");
    }

    public void displayMatchWinner(Player player) {
        displayString(player + " WINS THE MATCH!!!!!!");
    }

}