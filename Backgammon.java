import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Backgammon {
    // This is the main class for the Backgammon game. It orchestrates the running of the game.

    public static final int NUM_PLAYERS = 2;
    public static final boolean CHEAT_ALLOWED = false;
    private static final int DELAY = 3000;  // in milliseconds
    private static final String[] ALL_BOT_NAMES = {"Bot0","Bot1"};

    private final Cube cube = new Cube();
    private final Players players = new Players();
    private final Board board = new Board(players);
    private final Game game = new Game(board, cube, players);
    private final Match match = new Match(game, cube, players);
    private BotAPI[] bots = new BotAPI[NUM_PLAYERS];
    private final UI ui = new UI(board,players,cube,match,bots);
    private String[] botNames = new String[NUM_PLAYERS];
    private boolean quitGame = false;

    private void setupBots (String[] args) {
        if (args.length < NUM_PLAYERS) {
            botNames[0] = "Bot0";
            botNames[1] = "Bot1";
        } else {
            for (int i = 0; i < NUM_PLAYERS; i++) {
                boolean found = false;
                for (int j = 0; (j < ALL_BOT_NAMES.length) && !found; j++) {
                    if (args[i].equals(ALL_BOT_NAMES[j])) {
                        found = true;
                        botNames[i] = args[i];
                    }
                }
                if (!found) {
                    System.out.println("Error: Bot name not found");
                    System.exit(-1);
                }
            }
        }
        if (args.length < NUM_PLAYERS + 1) {
            match.setLength(3);
        } else {
            match.setLength(Integer.parseInt(args[2]));
        }
        for (int i=0; i<NUM_PLAYERS; i++) {
            try {
                Class<?> botClass = Class.forName(botNames[i]);
                Constructor<?> botCons = botClass.getDeclaredConstructor(PlayerAPI.class, PlayerAPI.class, BoardAPI.class, CubeAPI.class, MatchAPI.class, InfoPanelAPI.class);
                if (i==0) {
                    bots[i] = (BotAPI) botCons.newInstance(players.get(0), players.get(1), board, cube, match, ui.getInfoPanel());
                } else {
                    bots[i] = (BotAPI) botCons.newInstance(players.get(1), players.get(0), board, cube, match, ui.getInfoPanel());
                }
            } catch (IllegalAccessException ex) {
                System.out.println("Error: Bot instantiation fail (IAE)");
                Thread.currentThread().interrupt();
            } catch (InstantiationException ex) {
                System.out.println("Error: Bot instantiation fail (IE)");
                Thread.currentThread().interrupt();
            } catch (ClassNotFoundException ex) {
                System.out.println("Error: Bot instantiation fail (CNFE)");
                Thread.currentThread().interrupt();
            } catch (InvocationTargetException ex) {
                System.out.println("Error: Bot instantiation fail (ITE)");
                Thread.currentThread().interrupt();
            } catch (NoSuchMethodException ex) {
                System.out.println("Error: Bot instantiation fail (NSME)");
                Thread.currentThread().interrupt();
            }
        }
    }

    private void pause() throws InterruptedException {
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getPlayerNames() {
        for (Player player : players) {
            ui.promptPlayerName();
            String name = ui.getName(player);
            player.setName(name);
            ui.displayPlayerColor(player);
        }
    }

    private void rollToStart() throws InterruptedException {
        ui.display();
        do {
            for (Player player : players) {
                player.getDice().rollDice();
                ui.displayDieRoll(player);
            }
            if (players.isEqualDice()) {
                ui.displayDiceEqual();
            }
        } while (players.isEqualDice());
        players.setCurrentAccordingToDieRoll();
        ui.displayDiceWinner(players.getCurrent());
        ui.display();
        pause();
    }

    private void playAGame() throws InterruptedException {
        Command command = new Command();
        boolean firstMove = true;
        game.reset();
        rollToStart();
        do {
            Player currentPlayer = players.getCurrent();
            Dice currentDice;
            if (firstMove) {
                currentDice = players.getOneDieFromEachPlayer();
                firstMove = false;
            } else {
                currentPlayer.getDice().rollDice();
                ui.displayDiceRoll(currentPlayer);
                currentDice = currentPlayer.getDice();
            }
            Plays possiblePlays;
            possiblePlays = board.getPossiblePlays(currentPlayer,currentDice);
            if (possiblePlays.number()==0) {
                ui.displayNoMove(currentPlayer);
            } else if (possiblePlays.number()==1) {
                ui.displayForcedMove(currentPlayer);
                board.move(currentPlayer, possiblePlays.get(0));
            } else {
                ui.displayPlays(currentPlayer, possiblePlays);
                boolean turnOver = false, hasDoubled=false;
                do {
                    command = ui.getCommand(currentPlayer, possiblePlays);
                    if (command.isMove()) {
                        board.move(currentPlayer, command.getPlay());
                        turnOver = true;
                    } else if (command.isDouble()) {
                        Player opposingPlayer = players.getOpposingPlayer(currentPlayer);
                        if (!hasDoubled) {
                            if (match.canDouble(currentPlayer) && (!cube.isOwned() || cube.getOwner().equals(currentPlayer))) {
                                if (ui.getDoubleDecision(opposingPlayer)) {
                                    cube.accept(opposingPlayer);
                                    ui.display();
                                    hasDoubled = true;
                                } else {
                                    game.resign(opposingPlayer);
                                    turnOver = true;
                                }
                            } else {
                                ui.displayCannotDouble(currentPlayer);
                            }
                        } else {
                            ui.displayHasDoubled(currentPlayer);
                        }
                    } else if (command.isCheat()) {
                        board.cheat();
                        turnOver = true;
                    } else if (command.isQuit()) {
                        quitGame = true;
                        turnOver = true;
                    }
                    pause();
                } while (!turnOver);
            }
            ui.display();
            players.advanceCurrentPlayer();
        } while (!quitGame && !game.isOver());
        if (game.isOver()) {
            ui.displayGameWinner(game.getWinner());
        }
    }

    private void playAMatch() throws InterruptedException {
        ui.displayStartOfGame();
        getPlayerNames();
        ui.displayString("Match length is " + match.getLength());
        do {
            playAGame();
            if (!quitGame) {
                int points = match.getPoints();
                match.updateScores(points);
                ui.displayPointsWon(match.getWinner(),points);
                ui.displayScore(players,match);
            }
            pause();
        } while (!quitGame && !match.isOver());
        if (match.isOver()) {
            ui.displayMatchWinner(match.getWinner());
        }
        pause();
        pause();
    }

    public static void main(String[] args) throws InterruptedException {
        Backgammon game = new Backgammon();
        game.setupBots(args);
        game.playAMatch();
//        System.exit(0);
    }
}
