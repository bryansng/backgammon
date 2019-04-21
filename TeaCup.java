import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class TeaCup implements BotAPI {
	public static final boolean DEBUG = false;
	public static final boolean VERBOSE = false;
	public static final boolean TEST = false;
	
    private PlayerAPI me, opponent;
    private BoardAPI board;
    @SuppressWarnings("unused")
	private CubeAPI cube;
    @SuppressWarnings("unused")
	private MatchAPI match;
    @SuppressWarnings("unused")
	private InfoPanelAPI info;
    
	private ArrayList<Double> weights = new ArrayList<>();
	private double pipCountWeight, blockBlotWeight, blotWithoutContestWeight, homeBlockWeight, primingDefenseWeight, primingWeight, anchorWeight, escapedCheckersWeight, checkersInHomeWeight, checkersTakenOffWeight, pipsCoveredWeight, blotFurtherFromHomeWeight;
    
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
    	/*
    	blotWithoutContestWeight = weights.get(9);
    	primingDefenseWeight = weights.get(10);
    	blotFurtherFromHomeWeight = weights.get(11);
    	*/
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
    	System.out.println("\nBot " + me.getId() + ":");
    	System.out.println("Old Weight: " + Arrays.toString(weights.toArray()));
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
				if (aMove.isHit()) {
					temp[oppoID][25-to]--;
					temp[oppoID][temp[oppoID].length-1]++;
				}
				
				if (VERBOSE) {
					System.out.println("Next move: " + aMove.toString());
					if (temp[currID][fro] < 0 || temp[currID][to] < 0 || temp[oppoID][to] < 0 || temp[oppoID][temp[oppoID].length-1] < 0)
						System.out.println("ERROR: Play caused board positions to be wrong.");
				}
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
		double[] minMaxSum = getMinMaxSum(scores);
		double old_min, old_sum, new_sum;
		old_min = minMaxSum[0];
		old_sum = minMaxSum[2];
		double adjustValue = getAdjustmentValue(old_min);
		// get new sum, which is current_sum +  adjustValue * size of list.
		new_sum = old_sum + adjustValue * scores.length;
		for (int i = 0; i < scores.length; i++) {
			probs[i] = (scores[i] + adjustValue) / new_sum;
		}
		if (VERBOSE) {
			//if (getSum(probs) != 1.0) {
			System.out.println("\n\nAdjust value: " + adjustValue);
			System.out.println("Sum of probs: " + getSum(probs));
			System.out.println("Scores:\n" + Arrays.toString(scores));
			System.out.println("Probabilities:\n" + Arrays.toString(probs));
		}
		return probs;
	}
	private double[] getMinMaxSum(double[] scores) {
		double min, max, sum = 0;
		min = scores[0];
		max = scores[0];
		for (int i = 0; i < scores.length; i++) {
			double val = scores[i];
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
	// hard to normalize list with negative values,
	// Idea: so we add all the values by a positive number
	// making all the values in the list positive.
	//
	// this method returns the value needed to make
	// all values in the list positive.
	// NOTE: get min, then add until that min is positive.
	private double getAdjustmentValue(double min) {
		if (min > 0) {
			return 0;
		}
		double adjustValue = 1.0;
		return adjustValue + getAdjustmentValue(min + adjustValue);
	}
	private double getSum(double[] scores) {
		double sum = 0;
		for (int i = 0; i < scores.length; i++) sum += scores[i];
		return sum;
	}
	
	private double getScore(int[] c, int[] o) {
		double score = 0;
		
		if (isOpposed(c, o) && isBearOff(c)) {
			// and even checkers on last pip.
			score = numCheckersTakenOff(c, o) + blockBlotDiff(c, o);// + blotWithoutContest(c, o);
			if (VERBOSE) System.out.println("Current game phase: Is opposed bear off.");
		} else if (!isOpposed(c, o) && isBearOff(c) && c[0] == 0) {
			// unopposed pre if c has not bear-off yet and is unopposed bear off.
			score = numCheckersInHomeBoard(c, o);
			if (VERBOSE) System.out.println("Current game phase: Is unopposed pre-bear off.");
		} else if (!isOpposed(c, o) && isBearOff(c)) {
			score = numCheckersTakenOff(c, o) + numPipsCovered(c, o);
			if (VERBOSE) System.out.println("Current game phase: Is unopposed bear off.");
		} else {
			// if pip count difference is good then escape is good.
			// if pip count difference is bad then escape is bad
			score = pipCountDiff(c, o) + blockBlotDiff(c, o) + numHomeBoardBlocks(c, o) + lengthPrimeCapturedChecker(c, o) + anchor(c, o) + numEscapedCheckers(c, o) + numPipsCovered(c, o);// + blotWithoutContest(c, o) + primingDefense(c, o) + blotFurtherFromHome(c, o);
			if (VERBOSE) System.out.println("Current game phase: Normal");
		}
		if (DEBUG) System.out.println("Score: " + score);
		return score;
	}
	private boolean isOpposed(int[] c, int[] o) {
		// opposed if it is not unopposed.
		return !isUnopposed(c, o);
	}
	private boolean isBearOff(int[] c) {
		// checkers in home board...
		// if num checkers in home and home board is 15.
		int numCheckers = 0;
		for (int i = 0; i <= 6; i++) numCheckers += c[i];
		if (VERBOSE) System.out.println("Num checkers in home board: " + numCheckers);
		return numCheckers == 15;
	}
	private boolean isUnopposed(int[] c, int[] o) {
		// unopposed bear off if the only thing remaining is trying to bear-off,
		// i.e. no opponent's checkers in the way.
		
		// find last checker of opponent.
		int lastOpponentCheckerIndex = findLastCheckerIndex(o);
		
		// convert it into bot's perspective.
		lastOpponentCheckerIndex = 25 - lastOpponentCheckerIndex;
		if (VERBOSE) System.out.println("Last checker of opponent at: " + lastOpponentCheckerIndex);
		
		// find last checker of bot.
		int lastBotCheckerIndex = findLastCheckerIndex(c);
		if (VERBOSE) System.out.println("Last checker of current at: " + lastBotCheckerIndex);
		
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
	
	// check if can blot, provided no opponent checkers nearby the blot.
	private double blotWithoutContest(int[] c, int[] o) {
		// search for blots.
		ArrayList<Integer> blots = new ArrayList<>();
		for (int i = 1; i < c.length; i++) {
			if (c[i] == 1) {
				// 25-i to get index relative to opponent,
				// i.e. 25-5 = 20.
				// i.e. 25-1 = 24.
				int blotIndexRelativeToOpponent = 25-i;
				blots.add(blotIndexRelativeToOpponent);
			}
		}
		
		// takes into account:
		// - distance between blot and opponent's pips
		//
		// search for number of pips opponent checkers are on after blot.
		// blot index, and its distance to the closest opponent pip.
		double sumOfMinDistances = 0;
		for (int blotIndex : blots) {
			int minDistance = 0;
			for (int i = blotIndex; i < o.length; i++) {
				if (o[i] > 0) {
					if (minDistance == 0)
						minDistance = i - blotIndex;
					if ((i - blotIndex) < minDistance) {
						minDistance = i - blotIndex;
					}
				}
			}
			// further away the checkers are, the better.
			//
			// square to emphasize further away = better.
			// without square, hard to differentiate between distance of 3,3 and 5,1.
			// with square, 3,3 = 9+9 = 18 compared with 5,1 = 25+1 = 26.
			// 5,1 is better since it is better that one checker is safe than both at risk.
			// score += minDistance*minDistance;
			// above not used, it causes too much diffraction.
			sumOfMinDistances += minDistance;
			/*
			if (TEST) {
				System.out.println("Blot Index: " + blotIndex);
				System.out.println("Min Distances: " + minDistance);
			}
			*/
		}
		double score = sumOfMinDistances / blots.size();
		return (Double.isNaN(score) ? 0 : score) * blotWithoutContestWeight;
	}
	
	// if possible, do blots that are further away from home.
	// waste of dice result if we do blots nearer to home.
	private double blotFurtherFromHome(int[] c, int[] o) {
		double sumBlotIndex = 0, totalBlots = 0;
		for (int i = 1; i < c.length; i++) {
			if (c[i] == 1) {
				sumBlotIndex += i;
				totalBlots += 1;
			}
		}
		if (TEST) {
			System.out.println("Sum blot indexes: " + sumBlotIndex);
			System.out.println("Total blots: " + totalBlots);
		}
		double score = sumBlotIndex / totalBlots;
		return (Double.isNaN(score) ? 0 : score) * blotFurtherFromHomeWeight;
	}
	
	// if move played has more home board blocks, then that's a good thing.
	// however, this is not always a good thing.
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
	
	// Add priming defenses.
	// Calculate priming length of opponent player,
	// if around 3, start to move captured checkers.
	// http://www.nacr.net/backgammon-basics-priming-game.html
	private double primingDefense(int[] c, int[] o) {
		// need check if there are checkers captured.
		// else, this is useless.
		int[] resultArr = getPrimeLength(o);
		int officialLen = resultArr[0];
		int indexOfLastPrime = resultArr[1];
		
		// calculate captured checkers.
		int captured = getCapturedCheckers(c, indexOfLastPrime);
		/*
		if (TEST) {
			System.out.println("Prime length: " + officialLen);
			System.out.println("Index of last Prime: " + indexOfLastPrime);
			System.out.println("Captured: " + captured);
		}
		*/
		return (officialLen*captured) * primingDefenseWeight;
	}

	private double lengthPrimeCapturedChecker(int[] c, int[] o) {
		// need check if there are checkers captured.
		// else, this is useless.
		int[] resultArr = getPrimeLength(c);
		int officialLen = resultArr[0];
		int indexOfLastPrime = resultArr[1];
		
		// calculate captured checkers.
		int captured = getCapturedCheckers(o, indexOfLastPrime);
		/*
		System.out.println("Prime length: " + officialLen);
		System.out.println("Index of last Prime: " + indexOfLastPrime);
		System.out.println("Captured: " + captured);
		*/
		return (officialLen*captured) * primingWeight;
	}
	private int[] getPrimeLength(int[] c) {
		// length of prime with a threshold of 2 (put 1 below, but will register after 1, so 2).
		int threshold = 1;
		int maxLen = threshold, len = 0, indexOfLastPrime = -1;
		for (int i = c.length-1; i >= 1; i--) {
			if (c[i] > 1) {
				len++;
				if (len > maxLen) {
					maxLen = len;
					indexOfLastPrime = i;
				}
			} else len = 0;
		}
		return new int[]{maxLen > threshold ? maxLen : 0, maxLen > threshold ? indexOfLastPrime : 0};
	}
	private int getCapturedCheckers(int[] o, int indexOfLastPrime) {
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
		return captured;
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
			if (i == 0) {
				for (int j = 0; j < checkers[i].length; j++) {
					s += checkers[i][j] + " ";
				}
			} else {
				for (int j = checkers[i].length-1; j >= 0; j--) {
					s += checkers[i][j] + " ";
				}
			}
			s += "\n";
		}
		System.out.println(s);
	}
	
	public String getDoubleDecision() {
		return "n";
	}
}
