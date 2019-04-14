import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class TeaCup implements BotAPI {
	public static final boolean DEBUG = false;
	public static final boolean VERBOSE = false;
	
    private PlayerAPI me, opponent;
    private BoardAPI board;
    private CubeAPI cube;
    private MatchAPI match;
    private InfoPanelAPI info;
    
	private ArrayList<Double> weights = new ArrayList<>();
	private double pipCountWeight, blockBlotWeight, homeBlockWeight, primingWeight, anchorWeight, escapedCheckersWeight, checkersInHomeWeight, checkersTakenOffWeight, pipsCoveredWeight;
    
    public TeaCup(PlayerAPI me, PlayerAPI opponent, BoardAPI board, CubeAPI cube, MatchAPI match, InfoPanelAPI info) {
        this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.cube = cube;
        this.match = match;
        this.info = info;
        initWeights();
    }
    private void initWeights() {
    	weights = new ArrayList<>();
    	//for (int i = 0; i < 9; i++) weights.add(0.5);
    	readWeightsFromFile();
    	pipCountWeight = weights.get(0);
    	blockBlotWeight = weights.get(1);
    	homeBlockWeight = weights.get(2);
    	primingWeight = weights.get(3);
    	anchorWeight = weights.get(4);
    	escapedCheckersWeight = weights.get(5);
    	checkersInHomeWeight = weights.get(6);
    	checkersTakenOffWeight = weights.get(7);
    	pipsCoveredWeight = weights.get(8);
    	System.out.println("Weights: " + Arrays.toString(weights.toArray()));
    }
    private void readWeightsFromFile() {
		try {
			String classPath = System.getProperty("java.class.path");
			File txt = new File(classPath + "/weights.txt");
			if (DEBUG) System.out.println(txt.getCanonicalPath());
			Scanner scan = new Scanner(txt);
			
			// go to last line.
			// last line has the most updated weights.
			String lastLine = "";
			while (scan.hasNextLine()) {
				lastLine = scan.nextLine();
			}
			
			// store the weights.
			String[] weightsInString = lastLine.split(",");
			for (int i = 0; i < weightsInString.length; i++)
				weights.add(Double.parseDouble(weightsInString[i]));
			//buffer.close();
			scan.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public ArrayList<Double> getWeights() {
    	return weights;
    }
    public void setWeights(ArrayList<Double> newWeights) {
    	System.out.println("\nOld Weight: " + Arrays.toString(weights.toArray()));
    	weights = newWeights;
    	System.out.println("New Weight: " + Arrays.toString(weights.toArray()));
    }
    
	public String getName() {
		return "TeaCup";
	}
	
	/**
	 * » Generate a list of all possible (i.e. legal) plays
	   » Generate all possible resulting board positions, i.e. position AFTER each play has been made
	   » Go through the board positions and score to them
	   » The score should reflect the probability that the bot will win from that position.
	   » In reality, that is very hard to calculate, so the score should be an estimate of how good the position is.
	   » The bot should select the play with the highest score (i.e. the highest probability of the bot winning).
	 */
	public String getCommand(Plays possiblePlays) {
		// generate resulting board positions.
		// go through board positions and score them.
		return String.valueOf(1+getBestMove(getBoardPositionsProbabilities(getResultingBoardPositions(possiblePlays))));
	}
	
	// generates resulting board positions.
	private ArrayList<int[][]> getResultingBoardPositions(Plays plays) {
		ArrayList<int[][]> boardPositions = new ArrayList<>();
		int currID = me.getId();
		int oppoID = opponent.getId();
		int[][] checkers = board.get();
		if (DEBUG) {
			System.out.println("Initial position:");
			printCheckers(checkers);
		}
		for (Play aPlay : plays) {
			int[][] temp = getCopy(checkers);
			for (Move aMove : aPlay) {
				int fro = aMove.getFromPip();
				int to = aMove.getToPip();
				
				temp[currID][fro]--;
				temp[currID][to]++;
				if (aMove.isHit())
					temp[oppoID][to]--;
				
				if (VERBOSE) System.out.println("Next move: " + aMove.toString());
			}
			boardPositions.add(temp);
			if (VERBOSE) printCheckers(temp);
		}
		return boardPositions;
	}
	
	// scores the board positions.
	private double[] getBoardPositionsProbabilities(ArrayList<int[][]> boardPositions) {
		double[] scores = new double[boardPositions.size()];
		int currID = me.getId();
		int oppoID = opponent.getId();
		
		int i = 0;
		for (int[][] checkers : boardPositions) {
			if (DEBUG) System.out.println(i + ": Next Move:");
			scores[i] = getScore(checkers[currID], checkers[oppoID]);
			if (DEBUG) printCheckers(checkers);
			i++;
		}
		return getProbabilities(scores);
	}
	
	// Normalize the scores to probabilities.
	// https://stackoverflow.com/questions/26916150/normalize-small-probabilities-in-python
	private double[] getProbabilities(double[] scores) {
		double[] probs = new double[scores.length];
		double prob_factor = 1.0 / getSum(scores);
		for (int i = 0; i < scores.length; i++) probs[i] = scores[i] * prob_factor;
		if (DEBUG) System.out.println("Probabilities: " + Arrays.toString(probs));
		return probs;
	}
	private double getSum(double[] scores) {
		double sum = 0;
		for (int i = 0; i < scores.length; i++) sum += scores[i];
		return sum;
	}
	
	private double getScore(int[] c, int[] o) {
		double score = 0;
		
		if (isOpposedBearOff(c, o)) {
			// and even checkers on last pip.
			score = numCheckersTakenOff(c, o) + blockBlotDiff(c, o);
		} else if (isUnopposedPreBearOff(c, o)) {
			score = numCheckersInHomeBoard(c, o);
		} else if (isUnopposedBearOff(c, o)) {
			score = numCheckersTakenOff(c, o) + numPipsCovered(c, o);
		} else {
			// if pip count difference is good then escape is good.
			// if pip count difference is bad then escape is bad
			score = pipCountDiff(c, o) + blockBlotDiff(c, o) + numHomeBoardBlocks(c, o) + lengthPrimeCapturedChecker(c, o) + anchor(c, o) + numEscapedCheckers(c, o) + numCheckersInHomeBoard(c, o) + numCheckersTakenOff(c, o) + numPipsCovered(c, o);
		}
		if (DEBUG) System.out.println("Score: " + score);
		return score;
	}
	private boolean isOpposedBearOff(int[] c, int[] o) {
		// opposed if it is not unopposed.
		return !isUnopposedBearOff(c, o);
	}
	private boolean isUnopposedPreBearOff(int[] c, int[] o) {
		// unopposed pre if c has not bear-off yet and is unopposed bear off.
		return (!board.hasCheckerOff((Player) me) && isUnopposedBearOff(c, o));
	}
	private boolean isUnopposedBearOff(int[] c, int[] o) {
		// unopposed bear off if the only thing remaining is trying to bear-off,
		// i.e. no opponent's checkers in the way.
		
		// find last checker of opponent.
		int lastOpponentCheckerIndex = findLastCheckerIndex(o);
		
		// convert it into bot's perspective.
		lastOpponentCheckerIndex = 25 - lastOpponentCheckerIndex;
		
		// find last checker of bot.
		int lastBotCheckerIndex = findLastCheckerIndex(c);
		
		// if difference between opponent's index and bot's index is
		// positive, then it is unopposed. 
		// else it is opposed.
		return (lastOpponentCheckerIndex - lastBotCheckerIndex) > 0;
	}
	private int findLastCheckerIndex(int[] checkers) {
		int lastCheckerIndex = -1;
		for (int i = checkers.length-1; i >= 1; i--) {
			if (checkers[i] > 0) {
				lastCheckerIndex = i;
				break;
			}
		}
		return lastCheckerIndex;
	}
	
	// difference between the total number of dice results needed to bear-off.
	private double pipCountDiff(int[] c, int[] o) {
		int cTotal = 0, oTotal = 0;
		for (int i = 1; i < c.length; i++) {
			// num checkers * pip num.
			cTotal += i*c[i];
			oTotal += i*o[i];
		}
		/*
		System.out.println("cTotal: " + cTotal);
		System.out.println("oTotal: " + oTotal);
		*/
		// more negative (cTotal - oTotal) is, more better.
		return (cTotal - oTotal) * (-1) * pipCountWeight;
	}

	private double blockBlotDiff(int[] c, int[] o) {
		// number of blocks by c, pips with more than 1 checkers.
		// number of blots by o, pips with only 1 checker.
		int cBlocks = 0, oBlots = 0;
		for (int i = 1; i < c.length; i++) {
			if (c[i] > 1) cBlocks++;
			if (o[i] == 1) oBlots++;
		}
		/*
		System.out.println("cBlocks: " + cBlocks);
		System.out.println("oBlots: " + oBlots);
		*/
		// more positive (cBlocks - oBlots) is, more better.
		return (cBlocks - oBlots) * blockBlotWeight;
	}
	
	// TODO add a check for double blocks,
	// i.e. able to move two checkers to the same empty pip to block it.
	
	// if move played has more home board blocks, then that's a good thing.
	// however, this is not always a good thing.
	//
	// TODO could take into account home board blocks to opponents.
	private double numHomeBoardBlocks(int[] c, int[] o) {
		int num = 0;
		int MAX = 6;
		for (int i = 1; i <= MAX; i++) {
			if (c[i] > 1) num += i;	// take into consideration importance.
		}
		/*
		System.out.println("Num home board blocks: " + num);
		*/
		// more positive, more better.
		return num * homeBlockWeight;
	}
	
	// TODO add priming defenses.
	// http://www.nacr.net/backgammon-basics-priming-game.html

	private double lengthPrimeCapturedChecker(int[] c, int[] o) {
		// need check if there are checkers captured.
		// else, this is useless.
		//
		// length of prime with a threshold of 4 (put 3 below, but will register after 3, so 4).
		int maxLen = 3, len = 0, indexOfLastPrime = -1;
		for (int i = c.length-1; i >= 1; i--) {
			if (c[i] > 1) {
				len++;
				if (len > maxLen) {
					maxLen = len;
					indexOfLastPrime = i;
				}
			} else len = 0;
		}
		
		// calculate captured checkers.
		int captured = 0;
		if (indexOfLastPrime != -1) {
			// was indexOfLastPrime+1
			// Formulae 24-indexOfLastPrime+2,
			// i.e. bottom pov is 5, top pov is 20,
			// indexOfLastPrime = 20 for other player,
			// so search from 21.
			// 24-20+2 = 6
			for (int i = 26-indexOfLastPrime; i < o.length; i++) {
				if (DEBUG) System.out.println("i: " + i);
				captured += o[i];
			}
		}
		/*
		System.out.println("Prime length: " + (maxLen > 3 ? maxLen : 0));
		System.out.println("Index of last Prime: " + (maxLen > 3 ? indexOfLastPrime : -1));
		System.out.println("Captured: " + captured);
		*/
		// TODO can consider the number of captured checkers.
		// NOTE: Added, but could consider better calculation.
		return (maxLen*captured) * primingWeight;
	}
	
	// anchors = checkers in opponent's home boards.
	// Dont break anchors prematurely.
	// READ MORE FROM HERE: http://www.bkgm.com/articles/GOL/Sep02/anchor.htm
	// simple implementation atm first.
	private double anchor(int[] c, int[] o) {
		int num = 0;
		for (int i = 19; i <= 24; i++) {
			if (c[i] > 1) num += c[i];	// take into consideration importance.
		}
		/*
		System.out.println("Anchors: " + num);
		*/
		return num * anchorWeight;
	}
	
	// escaped = checkers not able to get hit.
	private double numEscapedCheckers(int[] c, int[] o) {
		// find pip number of last opponent checker.
		int lastCheckerIndex = -1;
		for (int i = o.length-1; i >= 1; i--) {
			if (o[i] > 0) {
				lastCheckerIndex = i;
				break;
			}
		}
		
		// calculate escaped checkers,
		// all checkers before the last opponent's checker.
		int num = 0;
		if (lastCheckerIndex != -1) {
			for (int i = 24-lastCheckerIndex; i >= 1; i--) {
				num += c[i];
			}
		}
		/*
		System.out.println("Last checker index: " + (lastCheckerIndex == -1 ? 0 : lastCheckerIndex));
		System.out.println("Num checkers escaped: " + num);
		*/
		return num * escapedCheckersWeight;
	}
	
	// weight should be low,
	// since if it leads to a blot, then its useless.
	private double numCheckersInHomeBoard(int[] c, int[] o) {
		int num = 0;
		for (int i = 1; i <= 6; i++) num += c[i];
		/*
		System.out.println("Num checkers in home: " + num);
		*/
		return num * checkersInHomeWeight;
	}
	
	private double numCheckersTakenOff(int[] c, int[] o) {
		/*
		System.out.println("Num checkers taken off: " + c[0]);
		*/
		return c[0] * checkersTakenOffWeight;
	}
	
	private double numPipsCovered(int[] c, int[] o) {
		int num = 0;
		// covered if it is not a blot.
		for (int i = 1; i <= 24; i++) {
			if (c[i] > 1) num++; 
		}
		/*
		System.out.println("Num pips covered: " + num);
		*/
		return num * pipsCoveredWeight;
	}
	
	// picks the best move.
	// returns the index of the move that results to the best board positions.
	private int getBestMove(double[] probs) {
		int maxAt = 0;
		for (int i = 0; i < probs.length; i++)
			maxAt = probs[i] > probs[maxAt] ? i : maxAt;
		if (DEBUG) System.out.println("Chosen index: " + maxAt);
		return maxAt;
	}
	
	// return a carbon copy of arr.
	private int[][] getCopy(int[][] arr) {
		int[][] newArr = new int[arr.length][arr[0].length];
		for (int i = 0; i < arr.length; i++)
			for (int j = 0; j < arr[i].length; j++)
				newArr[i][j] = arr[i][j];
		return newArr;
	}
	
	private void printCheckers(int[][] checkers) {
		String s = "";
		for (int i = 0; i < checkers.length; i++) {
			for (int j = 0; j < checkers[i].length; j++) {
				s += checkers[i][j] + " ";
			}
			s += "\n";
		}
		System.out.println(s);
	}
	
	public String getDoubleDecision() {
		return "n";
	}
}
