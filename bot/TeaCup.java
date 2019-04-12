package bot;

import java.util.ArrayList;
import java.util.Arrays;

import botAPI.BoardAPI;
import botAPI.CubeAPI;
import botAPI.InfoPanelAPI;
import botAPI.MatchAPI;
import botAPI.PlayerAPI;
import botAPI.Plays;
import move.Move;
import move.Moves;
import move.RollMoves;

public class TeaCup implements BotAPI {
    private PlayerAPI me, opponent;
    private BoardAPI board;
    private CubeAPI cube;
    private MatchAPI match;
    private InfoPanelAPI info;
    
    public TeaCup(PlayerAPI me, PlayerAPI opponent, BoardAPI board, CubeAPI cube, MatchAPI match, InfoPanelAPI info) {
        this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.cube = cube;
        this.match = match;
        this.info = info;
    }
    
	public String getName() {
		return getClass().getName();
	}
	
	/**
	 * » Generate a list of all possible (i.e. legal) plays
	   » Generate all possible resulting board positions, i.e. position AFTER each play has been made
	   » Go through the board positions and score to them
	   » The score should reflect the probability that the bot will win from that position.
	   » In reality, that is very hard to calculate, so the score should be an estimate of how good the position is.
	   » The bot should select the play with the highest score (i.e. the highest probability of the bot winning).
	 */
	// expected to getCommand more than once in a game, as long as there are still diceResults left.
	// this is because how we calculate our moves, in blocks rather than all at once.
	//
	// expected possiblePlays contains no duplicate moves.
	public String getCommand(Plays possiblePlays) {
		// generate resulting board positions.
		// go through board positions and score them.
		return generateCommand(getBestMove(getBoardPositionsScores(getResultingBoardPositions(possiblePlays))), possiblePlays);
	}
	// generates resulting board positions.
	private ArrayList<int[][]> getResultingBoardPositions(Moves moves) {
		ArrayList<int[][]> boardPositions = new ArrayList<>();
		int currID = me.getId();
		int oppoID = opponent.getId();
		int[][] checkers = board.get();
		for (RollMoves aRollMoves : moves) {
			for (Move aMove : aRollMoves.getMoves()) {
				int[][] temp = getCopy(checkers);
				int fro, to;
				fro = aMove.getFro();
				to = aMove.getTo();
				
				// check if its from bar.
				if (fro == -1 || fro == 24) {
					
				} else {
					
				}
					
				// check if its to home.
				if (to == -1 || to == 24) {
					
				} else {
					if (aMove.isHit()) {
						
					}
				}
				boardPositions.add(temp);
			}
		}
		return boardPositions;
	}
	// scores the board positions.
	private int[] getBoardPositionsScores(ArrayList<int[][]> boardPositions) {
		int[] scores = new int[boardPositions.size()];
		
		return scores;
	}
	// picks the best move.
	// returns the index of the move that results to the best board positions.
	private int getBestMove(int[] scores) {
		int maxAt = 0;
		for (int i = 0; i < scores.length; i++)
			maxAt = scores[i] > scores[maxAt] ? i : maxAt;
		return maxAt;
	}
	// creates the command from the index.
	private String generateCommand(int ind, Moves moves) {
		// command should just be letters.
		return String.valueOf(ind);
	}
	
	// return a carbon copy of arr.
	private int[][] getCopy(int[][] arr) {
		int[][] newArr = new int[arr.length][arr[0].length];
		for (int i = 0; i < arr.length; i++)
			for (int j = 0; j < arr[i].length; j++)
				newArr[i][j] = arr[i][j];
		return newArr;
	}
	
	public String getDoubleDecision() {
		// TODO Auto-generated method stub
		return null;
	}
}
