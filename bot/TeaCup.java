package bot;

import botAPI.Plays;
import botAPI.BoardAPI;
import botAPI.CubeAPI;
import botAPI.InfoPanelAPI;
import botAPI.MatchAPI;
import botAPI.PlayerAPI;

public class TeaCup implements BotAPI {
	
	private PlayerAPI me, opponent;
    private BoardAPI board;
    private CubeAPI cube;
    private MatchAPI match;
    private InfoPanelAPI info;

    TeaCup (PlayerAPI me, PlayerAPI opponent, BoardAPI board, CubeAPI cube, MatchAPI match, InfoPanelAPI info) {
        this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.cube = cube;
        this.match = match;
        this.info = info;
    }
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommand(Plays possiblePlays) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDoubleDecision() {
		// TODO Auto-generated method stub
		return calculateDoublingWinPercentage();
	}
	
	private String calculateDoublingWinPercentage() {
		String decision = "n";
		
		double myScore = evalFunction(board, me, opponent);
		double opponentScore = 100.00 - myScore;
		
		if ((me.getScore() == 9) && (opponent.getScore() == 9)) { // assuming max points is 11
			if (myScore >= 0 && myScore >= .50)
				decision = "y";
			else if (myScore >= .50 && myScore <= 0.75)
				decision = "y";
			else if (myScore >= 0.75 && myScore <= 100)
				decision = "y"; // drop opponent's
		} else if (isPostCrawford()) {
			decision = "y";
		} else {
			if (myScore >= 0 && myScore <= 0.66)
				decision = "n";
			else if (myScore >= 0.66 && myScore <= 0.75)
				decision = "y";
			else if ((myScore >= 0.75 && myScore <= 0.80) || (myScore > 0.80 && (!board.hasCheckerOff(null)))) // no gammon change
				decision = "y"; // drop opponent's
			else if ((myScore > .80) && (board.hasCheckerOff(null))) // no gammon changes
				decision = "n";
		}
		
		return decision;
	}
	
	/**
	 * Will need to change
	 * 
	 * @return
	 */
	private boolean isPostCrawford() {
		if (info.getLatestInfo() == "Post Crawford")
			return true;
		
		return false;
	}
	
	/**
	 * TODO
	 * README 
	 * 
	 * Calculate probability of me winning
	 * 	Make sure to get the right details from the board [IN PROGRESS]
	 * 		Particularly for the methods that calculate the number of checkers at certains pips
	 * 		need to pass index of pips and know which side the player is on
	 * Deduct that from 100% for opponent's probabilty of winning [DONE]
	 * Find the different game conditions according to the picture [DONE]
	 * 		Will need to change isPostCrawford()
	 * And make decision whether to roll or not [DONE]
	 * 
	 * @param board
	 * @param me
	 * @param opponent
	 * @return
	 */
	private double evalFunction(BoardAPI board, PlayerAPI me, PlayerAPI opponent) {
		double weight = 0.50;
		int[] features = new int[8];
		double score = 0.00;
		
		features[0] = pipCount(board, me, opponent);
		features[1] = blockBlotCount(board, me, opponent);
		features[2] = homeBoardBlockCount(board, me);
		features[3] = primeCount(board, me);
		features[4] = anchorInOpponentBoard(board, me);
		features[5] = escapedCheckersCount(board, me);
		features[6] = checkersInHomeBoardCount(board, me);
		features[7] = checkersTakenOffCount(board, me); 
		
		for (int i=0; i<8; i++) {
			score += weight * (int) features[i];
		}
		
		System.out.println("Score: " + score);
	
		return score;
	}
	/**
	 * f0
	 * 
	 * @param board
	 * @param me
	 * @param opponent
	 * @return
	 */
	private int pipCount(BoardAPI board, PlayerAPI me, PlayerAPI opponent) {
		int totalNumOfCheckersMe = 0;
		int totalNumOfCheckersOpponent = 0;
		
		for (int i=0; i<24; i++) {
			totalNumOfCheckersMe += board.getNumCheckers(me.getId(), i);
			totalNumOfCheckersOpponent += board.getNumCheckers(opponent.getId(), i);
		}
		
		return totalNumOfCheckersMe - totalNumOfCheckersOpponent;
	}
	
	/**
	 * f1
	 * 
	 * @param board
	 * @param me
	 * @param opponent
	 * @return
	 */
	private int blockBlotCount(BoardAPI board, PlayerAPI me, PlayerAPI opponent) {
		/**
		 * Blocks for us, our bot
		 */
		int totalNumOfBlocks = 0;
		/**
		 * Blots for opponent, their bot
		 */
		int totalNumOfBlots = 0;
		
		for (int i=0; i<24; i++) {
			if (board.getNumCheckers(me.getId(), i) > 1)
				totalNumOfBlocks += board.getNumCheckers(me.getId(), i);
			if (board.getNumCheckers(opponent.getId(), i) <= 1)
				totalNumOfBlots += board.getNumCheckers(opponent.getId(), i);
		}
		
		return totalNumOfBlocks - totalNumOfBlots;
	}
	
	/**
	 * f2
	 * 
	 * @param board
	 * @param me
	 * @return
	 */
	private int homeBoardBlockCount(BoardAPI board, PlayerAPI me) {
		int blockCount = 0;
		
		/**
		 * Most important points
		 * 6, 5, 7, 4, 3, 2, 1 respectively
		 */
		
		for (int i=0; i<7; i++) { // Assuming our home is from 6 - 1
			if (board.getNumCheckers(me.getId(), i) > 1)
				blockCount += board.getNumCheckers(me.getId(), i);
		}
		
		return blockCount;
	}
	
	/**
	 * TODO f3
	 * 
	 * @param board
	 * @param me
	 * @return
	 */
	private int primeCount(BoardAPI board, PlayerAPI me) {
		int currentPrime = 0;
		int maxPrime = 0;
		
		for (int i=0; i<24; i++) {
			if (board.getNumCheckers(me.getId(), i) > 0)
				currentPrime++;
			else 
				if (maxPrime < currentPrime)
					maxPrime = currentPrime;
		}
		
		return maxPrime;
	}
	
	/**
	 * TODO f4
	 * 
	 * @param board
	 * @param me
	 * @return
	 */
	private int anchorInOpponentBoard(BoardAPI board, PlayerAPI me) {
		int anchorCount = 0;
		
		for (int i=19; i<24; i++) {
			if (board.getNumCheckers(me.getId(), i) > 1)
				anchorCount += board.getNumCheckers(me.getId(), i);
		}
		
		return anchorCount;
	}
	
	/**
	 * TODO f5
	 * 
	 * @param board
	 * @param me
	 * @return
	 */
	private int escapedCheckersCount(BoardAPI board, PlayerAPI me) {
		int escapedCheckers = 0;
		
		for (int i=19; i<24; i++) {
			escapedCheckers += board.getNumCheckers(me.getId(), i);
		}
		
		return escapedCheckers;
	}
	
	/**
	 * f6
	 * 
	 * @param board
	 * @param me
	 * @return
	 */
	private int checkersInHomeBoardCount(BoardAPI board, PlayerAPI me) {
		int checkerCount = 0;
		
		for (int i=0; i<6; i++) {
			checkerCount += board.getNumCheckers(me.getId(), i);
		}
		
		return checkerCount;
	}
	
	/**
	 * f7
	 * 
	 * @param board
	 * @param me
	 * @return
	 */
	private int checkersTakenOffCount(BoardAPI board, PlayerAPI me) {
		int checkerCount = 0;
		
		for (int i=0; i<24; i++) {
			checkerCount += board.getNumCheckers(me.getId(), i);
		}
		
		return checkerCount -= 25 - checkerCount;
	}
}
