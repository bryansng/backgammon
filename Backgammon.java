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
import java.util.Scanner;

public class Backgammon {
    // This is the main class for the Backgammon game. It orchestrates the running of the game.
	private static final boolean DEBUG = false;
	private static final boolean VERBOSE = false;
	
	private static boolean REINFORCE_LEARNING = true;
	private static final boolean ENTER_TO_MOVE_ON = false;
	private static final boolean PLAY_WITH_BOT = false;
	private static int NUM_PLAYERS_VS_BOTS = 2;	// if play with bot, this = 1, else this = 2.
    
    public static int MATCH_LENGTH = 11;
    
    // MATCH_LENGTH, but for tournament rounds.
    private static final int MATCH_LENGTH_FOR_CHAMPIONSHIP = 7;
    
    // number of weights to produce in champion.txt
	private static final int NUM_TOURNAMENT_BOTS = 2;
	private static final int NUM_TOURNAMENT_TRIES = 10;
	
    // will produce weights in champion.txt,
    // it will then produce the best weights and add to final.txt
    private static final boolean CHAMPIONSHIP = true;
    
    // does not create weights in champion.txt
    // only uses the weights in champion.txt to produce the final.txt.
    private static final boolean ONLY_TOURNAMENT = true;
    
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
            if (VERBOSE) System.out.println("Dice: " + currentDice.toString());
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
                    if (VERBOSE) System.out.println("Command: " + command);		// added by us.
                    if (command.isMove()) {
                        board.move(currentPlayer, command.getPlay());
                        turnOver = true;
                    } else if (command.isDouble()) {
                        Player opposingPlayer = players.getOpposingPlayer(currentPlayer);
                        if (!hasDoubled) {
                            if (match.canDouble(currentPlayer) && (!cube.isOwned() || cube.getOwner().equals(currentPlayer))) {
                                if (ui.getDoubleDecision(opposingPlayer)) {
                                	if (VERBOSE) System.out.println("Double: y");		// added by us.
                                    cube.accept(opposingPlayer);
                                    ui.display();
                                    hasDoubled = true;
                                } else {
                                	if (VERBOSE) System.out.println("Double: n");		// added by us.
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
        if (VERBOSE) System.out.println("Game over");		// added by us.
        pause();
        pause();
    }
    
    private int[] lossesInARow = new int[NUM_PLAYERS_VS_BOTS];
    private void updateWeightsEachGame() {
    	// update file with the current weights used to win game.
    	if (!CHAMPIONSHIP) {
        	ArrayList<ArrayList<Double>> botsPreviousWeights = new ArrayList<ArrayList<Double>>(bots.length);
        	for (int i = 0; i < bots.length; i++) botsPreviousWeights.add(bots[i].getWeights());
    		updateWeightsFile(getGameStatsAndBotWeightsInString(botsPreviousWeights));
    	}
    	
    	// update weights.
    	if (REINFORCE_LEARNING) {
        	ArrayList<ArrayList<Double>> botsNewWeights = new ArrayList<ArrayList<Double>>(NUM_PLAYERS_VS_BOTS);
        	for (int i = 0; i < NUM_PLAYERS_VS_BOTS; i++) botsNewWeights.add(new ArrayList<Double>());
        	System.out.println("\n");
	    	Random rand = new Random();
	    	// if bot wins, add a small random amount (could be slightly
	    	// negative) to the positive weights and subtract a small amount
	    	// from the negative weights (could be slightly negative).
	    	for (int botID = 0; botID < NUM_PLAYERS_VS_BOTS; botID++) {
		    	ArrayList<Double> oldWeights = bots[botID].getWeights();
		    	if (game.getWinner().equals(players.get(botID))) {
		        	for (int i = 0; i < bots[botID].getWeights().size(); i++) {
		        		double oldWeight = oldWeights.get(i);
		        		// positive weights.
		        		if (oldWeight > 0)
		        			botsNewWeights.get(botID).add(oldWeight + getMinuteRandomValue(rand));
		        		// negative weights.
		        		else
		        			botsNewWeights.get(botID).add(oldWeight - getMinuteRandomValue(rand));
		        	}
		        	//bots[1].setWeights(getProbabilities(newWeights));
		        	bots[botID].setWeights(botsNewWeights.get(botID));
		        	if (DEBUG) System.out.println("\nReset losses due to win, Losses before: " + Arrays.toString(lossesInARow));
					lossesInARow[botID] = 0;
					//lossesInARow[otherBotID] = 0;
					if (DEBUG) System.out.println("Losses after: " + Arrays.toString(lossesInARow));
		    	// if bot losses, do opposite.
		    	} else {
		    		if (DEBUG) System.out.println("\nBot " + botID + " loss, Losses before: " + Arrays.toString(lossesInARow));
		    		lossesInARow[botID]++;
		    		if (DEBUG) System.out.println("Losses after: " + Arrays.toString(lossesInARow));
		        	// if bot losses three times in a row, exchange the weights between the bots.
		    		if (lossesInARow[botID] >= 3) {
		    			if (DEBUG) System.out.println("\n\nNOTE: Bot " + botID + " losses more than three times, exchanging weights with bot 1.");
		    			if (DEBUG) System.out.println("Bot " + botID + " old weights: " + Arrays.toString(bots[1].getWeights().toArray()));
		    			int otherBotID = botID == 0 ? 1 : 0;
		    			ArrayList<Double> temp = bots[botID].getWeights();
		    			bots[botID].setWeights(bots[otherBotID].getWeights());
		    			bots[otherBotID].setWeights(temp);
		    			if (DEBUG) System.out.println("Bot " + botID + " new weights: " + Arrays.toString(bots[1].getWeights().toArray()) + "\n\n");
		    			if (DEBUG) System.out.println("Swapping, Losses before: " + Arrays.toString(lossesInARow));
		    			lossesInARow[botID] = 0;
						//lossesInARow[otherBotID] = 0;
		    			if (DEBUG) System.out.println("Losses after: " + Arrays.toString(lossesInARow));
		    			botsNewWeights.set(botID, bots[botID].getWeights());
		    		} else {
			        	for (int i = 0; i < bots[botID].getWeights().size(); i++) {
			        		double oldWeight = oldWeights.get(i); 
			        		// positive weights.
			        		if (oldWeight < 0)
			        			botsNewWeights.get(botID).add(oldWeight + getMinuteRandomValue(rand));
			        		// negative weights.
			        		else
			        			botsNewWeights.get(botID).add(oldWeight - getMinuteRandomValue(rand));
			        	}
			        	//bots[1].setWeights(getProbabilities(newWeights));
			        	bots[botID].setWeights(botsNewWeights.get(botID));
		    		}
		    	}
	    	}
    	}
    }
    private String getGameStatsAndBotWeightsInString(ArrayList<ArrayList<Double>> botsNewWeights) {
    	String s = "";
    	int winnerBotID = game.getWinner().getId();
    	for (int i = 0; i < botsNewWeights.size(); i++) {
    		// add bot's game result, i.e. win/lose.
    		if (i == winnerBotID) s += 1 + ",";	// win
    		else s += 0 + ",";					// lose
    		
    		// add bot id.
    		s += i + ",";
    		
    		// add bot weights.
    		for (int j = 0; j < botsNewWeights.get(i).size(); j++) {
    			s += botsNewWeights.get(i).get(j);
				if (j != botsNewWeights.get(i).size()-1) s += ",";
    		}
			if (i != botsNewWeights.size()-1) s += "\n";
    	}
    	return s;
    }
    private static void updateWeightsFile(String gameStatsAndBotWeights) {
    	StringBuilder sb = new StringBuilder();
    	/*
    	// date,
    	sb.append(getCurrentTime() + "\n");
    	// stats,
    	sb.append(match.getStats() + "\n");
    	*/
    	// gameStatsAndBotWeights in csv format:
    	// win/lose,botID,weights
    	// i.e. 1,0,weights...
    	// 1 denotes a win by bot 0.
    	sb.append(gameStatsAndBotWeights + "\n");
    	toFile(sb);
    }
    private double minuteRange = 0.05;
    private double getMinuteRandomValue(Random rand) {
    	//return (rand.nextDouble()-0.5) * minuteRange;
    	return (rand.nextDouble()-0.1) * minuteRange;	// 0 to 0.05
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
    	/*
    	if (match.getWinner().equals(players.get(1)))
    		updateWeightsFile(getNewWeightsInString(bots[1].getWeights()));
    	else
    		updateWeightsFile(getNewWeightsInString(bots[0].getWeights()));
    	*/
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
    
	private static void toFile(StringBuilder sb, String filename) {
		try {
			String classPath = System.getProperty("java.class.path");
			File txt = new File(classPath + "/" + filename);	// NOTE: This resides in /bin, NOT /src.
			// File txt = new File("/Users/YeohB/Desktop/UCD/Stage 2/Semester 2/COMP20050 - Software Engineering Project 2/Software Engineering/backgammon/src/weights.txt");
			BufferedWriter buffer = new BufferedWriter(new FileWriter(txt, true));
			buffer.append(sb);
			buffer.flush();
			buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void toFile(StringBuilder sb) {
		toFile(sb, "weights.txt");
	}
    @SuppressWarnings("unused")
	private String getCurrentTime() {
    	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss");
    	Date date = new Date();
    	return dateFormat.format(date);
    }
    
    // aka handleChampionship()
    private static void getBestWeights(String[] args) throws InterruptedException {
    	// generate as many weights.
    	if (!ONLY_TOURNAMENT) {
	    	for (int i = 0; i < NUM_TOURNAMENT_BOTS; i++) {
		    	// runs 101 games.
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
		        game.ui.terminanteFrame();
	    		
		    	// gets the winner's weights of the game.
	    		// print to file "champions.txt"
		        game.printWinnerWeightsToFile("champion.txt");
	    	}
    	}
    	
    	REINFORCE_LEARNING = false;
    	System.out.println("\n\nTOURNAMENT ROUNDS:");
    	
    	for (int i = 0; i < NUM_TOURNAMENT_TRIES; i++) {
        	// from those winner's weights.
        	ArrayList<ArrayList<Double>> winnersWeights = getWinnersWeights();
        	
	    	// get the final winner's weights.
	    	ArrayList<Double> bestWeights = botMatch(winnersWeights);
	    	
			// print to file "final.txt"
	    	printWeightsToFile(bestWeights, "final.txt");
    	}
    }
    private static void printWeightsToFile(ArrayList<Double> theWeights, String filename) {
    	StringBuilder s = new StringBuilder();
		// add bot weights.
		for (int i = 0; i < theWeights.size(); i++) {
			s.append(theWeights.get(i));
			if (i != theWeights.size()-1) s.append(",");
		}
		s.append("\n");
        toFile(s, filename);
    }
    // Returns the best weights.
    // Handles the tournament.
    /*
    private static ArrayList<Double> botMatches(ArrayList<ArrayList<Double>> botsWeights) throws InterruptedException {
    	ArrayList<ArrayList<Double>> bestWeights = new ArrayList<>();
    	do {
    		bestWeights = botMatch(botsWeights);
    	} while (bestWeights.size() > 1);
    	return bestWeights.get(0);
    }
	*/
    // Tree recursive pattern that handles the tournament.
    private static ArrayList<Double> botMatch(ArrayList<ArrayList<Double>> botsWeights) throws InterruptedException {
    	if (botsWeights.size() == 0) {
    		System.out.println("[Error] Got botsWeights with size 0. What do I return?");
    		return null;
    	// if length 1, return the weights.
    	} else if (botsWeights.size() == 1) {
    		return botsWeights.get(botsWeights.size()-1);
    	// if length 2, bot versus bot, return winner's weights.
    	} else if (botsWeights.size() == 2) {
    		return botVsBot(botsWeights.get(0), botsWeights.get(1));
    	// else, continue calling botsWeights with lesser weights.
    	} else {
    		//ArrayList<ArrayList<Double>> remaining = (ArrayList<ArrayList<Double>>) botsWeights.subList(2, botsWeights.size());
    		ArrayList<Double> first, second;
    		first = botsWeights.remove(0);
    		second = botsWeights.remove(1);
    		return botVsBot(botVsBot(first, second), botMatch(botsWeights));
    	}
    }
    // plays a match using the given weights.
    // returns the winner's weights.
    private static ArrayList<Double> botVsBot(ArrayList<Double> bot0Weights, ArrayList<Double> bot1Weights) throws InterruptedException {
    	MATCH_LENGTH = MATCH_LENGTH_FOR_CHAMPIONSHIP;
        Backgammon game = new Backgammon();
        if (PLAY_WITH_BOT) {
            String[] myArgs = new String[2];
            myArgs[0] = "TeaCup";
            myArgs[1] = "3";
            game.setupBots(myArgs);
        } else {
            game.setupBots(new String[0]);
        }
        game.updateBotsWeights(bot0Weights, bot1Weights);
        game.playAMatch();
        game.ui.terminanteFrame();
        return game.getWinnerWeights();
    }
    // returns the winner's weights based on the conclusion of the match.
    private ArrayList<Double> getWinnerWeights() {
    	if (match.getWinner().getId() == 0) return bots[0].getWeights();
    	else return bots[1].getWeights();
    }
    
    // updates the two bot's weights with the given weights.
    private void updateBotsWeights(ArrayList<Double> bot0Weights, ArrayList<Double> bot1Weights) {
    	bots[0].setWeights(bot0Weights);
    	bots[1].setWeights(bot1Weights);
    }
    
    // prints the match winner's weights to the given filename (file resides in /bin).
    private void printWinnerWeightsToFile(String filename) {
    	// get winner weights.
    	// then print winner weights to file.
    	System.out.println("\n");
    	if (match.getWinner().equals(players.get(1))) {
    		printWeightsToFile(bots[1].getWeights(), filename);
    		System.out.println("Winner weights: " + bots[1].getWeights());
    	} else {
    		printWeightsToFile(bots[0].getWeights(), filename);
    		System.out.println("Winner weights: " + bots[0].getWeights());
    	}
    }
    
    // Returns the weights of the bots that will be entering the tournament.
    // The weights are extracted from the champion.txt file.
    // That file should include only the winners.
    private static ArrayList<ArrayList<Double>> getWinnersWeights() {
    	ArrayList<ArrayList<Double>> winnersWeights = new ArrayList<ArrayList<Double>>();
		try {
			String classPath = System.getProperty("java.class.path");
			File txt = new File(classPath + "/champion.txt");	// NOTE: This resides in /bin, NOT /src.
			Scanner scan = new Scanner(txt);
			
			// get all weights from file.
			ArrayList<String> lines = new ArrayList<String>();
			while (scan.hasNextLine()) {
				lines.add(scan.nextLine());
			}
			scan.close();
			
			// store the weights in winnersWeights.
			for (int numLine = 0; numLine < lines.size(); numLine++) {
				winnersWeights.add(new ArrayList<Double>());
				String[] csv = lines.get(numLine).split(",");
				for (int j = 0; j < csv.length; j++)
					winnersWeights.get(numLine).add(Double.parseDouble(csv[j]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return winnersWeights;
    }
    
    public static void main(String[] args) throws InterruptedException {
    	if (CHAMPIONSHIP) {
    		getBestWeights(args);
	        System.exit(0);
    	} else {
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
	        // System.exit(0);
    	}
    }
}
