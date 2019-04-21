import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class Backgammon {
    // This is the main class for the Backgammon game. It orchestrates the running of the game.
	private static final boolean DEBUG = false;
	private static final boolean ENTER_TO_MOVE_ON = false;
	private static final boolean PLAY_WITH_BOT = false;
    public static int NUM_PLAYERS_VS_BOTS = 2;	// if play with bot, this = 1, else this = 2.
    
    public static final int MATCH_LENGTH = 51;
    public static final int NUM_PLAYERS = 2;
    public static final boolean CHEAT_ALLOWED = false;
    //private static final int DELAY = 3000;  // in milliseconds
    private static final int DELAY = 0;  // in milliseconds
    private static final String[] ALL_BOT_NAMES = {"Bot0","Bot1","TeaCup"};
    private final Cube cube = new Cube();
    private final Players players = new Players();
    private final Board board = new Board(players);
    private final Game game = new Game(board, cube, players);
    private final Match match = new Match(game, cube, players);
    private BotAPI[] bots = new BotAPI[NUM_PLAYERS_VS_BOTS];
    private final UI ui = new UI(board,players,cube,match,bots);
    private String[] botNames = new String[NUM_PLAYERS_VS_BOTS];
    private boolean quitGame = false;

    private void setupBots (String[] args) {
        if (args.length < NUM_PLAYERS_VS_BOTS) {
            botNames[0] = "TeaCup";
            botNames[1] = "TeaCup";
            /*
            botNames[0] = "Bot0";
            botNames[1] = "Bot1";
            */
        } else {
            for (int i = 0; i < NUM_PLAYERS_VS_BOTS; i++) {
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
        if (args.length < NUM_PLAYERS_VS_BOTS + 1) {
            match.setLength(MATCH_LENGTH);
        } else {
            match.setLength(Integer.parseInt(args[1]));
        }
        for (int i=0; i<NUM_PLAYERS_VS_BOTS; i++) {
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
        	if (ENTER_TO_MOVE_ON) {
        		ui.displayString("Enter an empty string to proceed.");
        		while (!ui.getString().equals("")) {}
        	} else Thread.sleep(DELAY);
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
            if (DEBUG) System.out.println("Dice: " + currentDice.toString());
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
                    System.out.println(command);		// added by us.
                    if (command.isMove()) {
                        board.move(currentPlayer, command.getPlay());
                        turnOver = true;
                    } else if (command.isDouble()) {
                        Player opposingPlayer = players.getOpposingPlayer(currentPlayer);
                        if (!hasDoubled) {
                            if (match.canDouble(currentPlayer) && (!cube.isOwned() || cube.getOwner().equals(currentPlayer))) {
                                if (ui.getDoubleDecision(opposingPlayer)) {
                                	if (DEBUG) System.out.println("Double: y");		// added by us.
                                    cube.accept(opposingPlayer);
                                    ui.display();
                                    hasDoubled = true;
                                } else {
                                	if (DEBUG) System.out.println("Double: n");		// added by us.
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
                updateWeightsEachGame();	// added by us.
                ui.getInfoPanel().clear();	// added by us.
            }
            pause();
        } while (!quitGame && !match.isOver());
        if (match.isOver()) {
            ui.displayMatchWinner(match.getWinner());
            updateWeightsEachMatch();		// added by us.
        }
        if (DEBUG) System.out.println("Game over");		// added by us.
        pause();
        pause();
    }
    
    private int lossesInARow = 0;
    private void updateWeightsEachGame() {
    	// if bot wins, add a small random amount (could be slightly
    	// negative) to the positive weights and subtract a small amount
    	// from the negative weights (could be slightly negative).
    	ArrayList<Double> oldWeights = bots[1].getWeights();
    	ArrayList<Double> newWeights = new ArrayList<>();
    	Random rand = new Random();
    	if (game.getWinner().equals(players.get(1))) {
        	for (int i = 0; i < bots[1].getWeights().size(); i++) {
        		double oldWeight = oldWeights.get(i);
        		// positive weights.
        		if (oldWeight > 0)
        			newWeights.add(oldWeight + getMinuteRandomValue(rand));	// 0 to 0.23
        		// negative weights.
        		else
        			newWeights.add(oldWeight - getMinuteRandomValue(rand));	// -0.23 to 0
        	}
        	//bots[1].setWeights(getProbabilities(newWeights));
        	bots[1].setWeights(newWeights);
        	if (DEBUG) System.out.println("\nReset losses due to win, Losses before: " + lossesInARow);
			lossesInARow = 0;
			if (DEBUG) System.out.println("Losses after: " + lossesInARow);
    	// if bot losses, do opposite.
    	} else {
    		if (DEBUG) System.out.println("\nBot loss, Losses before: " + lossesInARow);
    		lossesInARow++;
    		if (DEBUG) System.out.println("Losses after: " + lossesInARow);
        	// if bot losses three times in a row, exchange the weights between the bots.
    		if (lossesInARow >= 3) {
    			if (DEBUG) System.out.println("\n\nNOTE: Bot 1 losses more than three times, exchanging weights with bot 1.");
    			if (DEBUG) System.out.println("Bot 1 old weights: " + Arrays.toString(bots[1].getWeights().toArray()));
    			ArrayList<Double> temp = bots[1].getWeights();
    			bots[1].setWeights(bots[0].getWeights());
    			bots[0].setWeights(temp);
    			if (DEBUG) System.out.println("Bot 1 new weights: " + Arrays.toString(bots[1].getWeights().toArray()) + "\n\n");
    			if (DEBUG) System.out.println("Swapping, Losses before: " + lossesInARow);
    			lossesInARow = 0;
    			if (DEBUG) System.out.println("Losses after: " + lossesInARow);
    		} else {
	        	for (int i = 0; i < bots[1].getWeights().size(); i++) {
	        		double oldWeight = oldWeights.get(i); 
	        		// positive weights.
	        		if (oldWeight < 0)
	        			newWeights.add(oldWeight + getMinuteRandomValue(rand));
	        		// negative weights.
	        		else
	        			newWeights.add(oldWeight - getMinuteRandomValue(rand));
	        	}
	        	//bots[1].setWeights(getProbabilities(newWeights));
	        	bots[1].setWeights(newWeights);
    		}
    	}
    	updateWeightsFile(getNewWeightsInString(bots[1].getWeights()));
    }
    private String getNewWeightsInString(ArrayList<Double> newWeights) {
    	String weightsInStr = "";
    	for (int i = 0; i < newWeights.size(); i++) {
    		weightsInStr += newWeights.get(i);
			if (i != bots[1].getWeights().size()-1) weightsInStr += ",";
    	}
    	return weightsInStr;
    }
    private void updateWeightsFile(String newWeights) {
    	StringBuilder sb = new StringBuilder();
    	// date,
    	sb.append(getCurrentTime() + "\n");
    	// stats,
    	sb.append(match.getStats() + "\n");
    	// new weights to test.
    	sb.append(newWeights + "\n");
    	toFile(sb);
    }
    private double minuteRange = 0.05;
    private double getMinuteRandomValue(Random rand) {
    	//return (rand.nextDouble()-0.5) * minuteRange;
    	return rand.nextDouble() * minuteRange;
    }
    
	// Normalize the scores to probabilities.
	// https://stackoverflow.com/questions/16514443/how-to-normalize-a-list-of-positive-and-negative-decimal-number-to-a-specific-ra
	@SuppressWarnings("unused")
	private ArrayList<Double> getProbabilities(ArrayList<Double> weights) {
		ArrayList<Double> probs = new ArrayList<>();
		double[] minMaxSum = getMinMaxSum(weights);
		double old_min, old_range, new_min = -1, new_range = 1;// + 0.9999999999 - new_min;
		old_min = minMaxSum[0];
		old_range = minMaxSum[1] - old_min;
		for (int i = 0; i < weights.size(); i++) {
			probs.add((weights.get(i) - old_min) / old_range * new_range + new_min);
		}
		return probs;
	}
	private double[] getMinMaxSum(ArrayList<Double> weights) {
		double min, max, sum = 0;
		min = weights.get(0);
		max = weights.get(0);
		for (int i = 0; i < weights.size(); i++) {
			double val = weights.get(i);
			sum += val;
			if (val > max) max = val;
			if (val < min) min = val;
		}
		double[] minMaxSum = new double[3];
		minMaxSum[0] = min;
		minMaxSum[1] = max;
		minMaxSum[2] = sum;
		return minMaxSum;
	}
    
    private void updateWeightsEachMatch() {
    	if (match.getWinner().equals(players.get(1)))
    		updateWeightsFile(getNewWeightsInString(bots[1].getWeights()));
    	else
    		updateWeightsFile(getNewWeightsInString(bots[0].getWeights()));
    }
    
    @SuppressWarnings("unused")
	private String getNewRandomWeights() {
    	// weights in the range of -0.5 to 0.5.
    	// NOTE: ATM, just use random weights.
    	// random weights.
    	String newWeights = "";
    	Random rand = new Random();
    	for (int i = 0; i < bots[1].getWeights().size(); i++) {
    		newWeights += rand.nextDouble()-0.5;
    		if (i != bots[1].getWeights().size()-1) newWeights += ",";
    	}
    	return newWeights;
    }
    
	private void toFile(StringBuilder sb) {
		try {
			String classPath = System.getProperty("java.class.path");
			File txt = new File(classPath + "/weights.txt");	// NOTE: This resides in /bin, NOT /src.
			// File txt = new File("/Users/YeohB/Desktop/UCD/Stage 2/Semester 2/COMP20050 - Software Engineering Project 2/Software Engineering/backgammon/src/weights.txt");
			BufferedWriter buffer = new BufferedWriter(new FileWriter(txt, true));
			buffer.append(sb);
			buffer.flush();
			buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    private String getCurrentTime() {
    	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss");
    	Date date = new Date();
    	return dateFormat.format(date);
    }

    public static void main(String[] args) throws InterruptedException {
        Backgammon game = new Backgammon();
        if (PLAY_WITH_BOT) {
            String[] myArgs = new String[2];
            myArgs[0] = "TeaCup";
            myArgs[1] = "3";
            game.setupBots(myArgs);
        } else {
            game.setupBots(args);
        }
        game.playAMatch();
//        System.exit(0);
    }
}
