package game;

import constants.DieInstance;
import constants.MoveResult;
import constants.PlayerPerspectiveFrom;
import game_engine.GameComponentsController;
import game_engine.Settings;
import javafx.scene.paint.Color;
import move.BarToPip;
import move.Move;
import move.Moves;
import move.PipToHome;
import move.PipToPip;
import move.RollMoves;

/**
 * This class represents the Board object in Backgammon from the perspective of its functions.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
public class Board extends BoardMoves {
	private GameComponentsController game;
	
	public Board(GameComponentsController game) {
		super(game);
		this.game = game;
	}
	
	/**
	 * Un-highlight the pips and checkers.
	 */
	public void unhighlightPipsAndCheckers() {
		for (int i = 0; i < pips.length; i++) {
			pips[i].setNormalImage();
			
			if (!pips[i].isEmpty()) {
				pips[i].top().setNormalImage();
			}
		}
	}
	
	/**
	 * Highlight the pips and homes.
	 * @param exceptPipNum, except this point number.
	 */
	public void highlightAllPipsExcept(int exceptPipNum) {
		game.unhighlightAll();
		
		for (int i = 0; i < pips.length; i++) {
			if (i != exceptPipNum) {
				pips[i].setHighlightImage();
			}
		}
		game.getMainHome().highlight(Settings.getTopPerspectiveColor());
		game.getMainHome().highlight(Settings.getBottomPerspectiveColor());
	}
	
	/**
	 * Highlight the top checkers of fromPips and fromBars in possible moves.
	 * @param moves the possible moves.
	 */
	public void highlightFromPipsAndFromBarChecker(Moves moves) {
		game.unhighlightAll();
		
		for (RollMoves rollMoves : moves) {
			for (Move aMove : rollMoves.getMoves()) {
				if (aMove instanceof PipToPip) {
					PipToPip move = (PipToPip) aMove;
					pips[move.getFromPip()].top().setHighlightImage();
				} else if (aMove instanceof PipToHome) {
					PipToHome move = (PipToHome) aMove;
					pips[move.getFromPip()].top().setHighlightImage();
				} else if (aMove instanceof BarToPip) {
					BarToPip move = (BarToPip) aMove;
					game.getBars().highlight(move.getFromBar());
				}
			}
		}
	}
	
	/**
	 * Highlight the toPips of the fromPip in possible moves.
	 * Used to highlight the toPips that player can move checkers from fromPip. 
	 * @param moves the possible moves.
	 * @param fromPip
	 */
	public void highlightToPipsAndToHome(Moves moves, int fromPip) {
		game.unhighlightAll();
		
		boolean isFromPipInMoves = false;
		for (RollMoves rollMoves : moves) {
			for (Move aMove : rollMoves.getMoves()) {
				if (aMove instanceof PipToPip) {
					PipToPip move = (PipToPip) aMove;
					if (move.getFromPip() == fromPip) {
						isFromPipInMoves = true;
						pips[move.getToPip()].setHighlightImage();
					}
				} else if (aMove instanceof PipToHome) {
					PipToHome move = (PipToHome) aMove;
					if (move.getFromPip() == fromPip) {
						isFromPipInMoves = true;
						game.getMainHome().getHome(pips[move.getFromPip()].top().getColor()).highlight();
					}
				}
			}
		}
		
		// Highlight the selected pip's top checker.
		// Provided the fromPip is part of the moves.
		if (isFromPipInMoves) pips[fromPip].top().setHighlightImage();
	}
	public void highlightToPipsAndToHome(Moves moves, String fromBar) {
		game.unhighlightAll();
		
		boolean isFromBarInMoves = false;
		for (RollMoves rollMoves : moves) {
			for (Move aMove : rollMoves.getMoves()) {
				if (aMove instanceof BarToPip) {
					BarToPip move = (BarToPip) aMove;
					if (move.getFromBar() == parseColor(fromBar)) {
						isFromBarInMoves = true;
						pips[move.getToPip()].setHighlightImage();
					}
				}
			}
		}
		
		// Highlight the selected pip's top checker.
		// Provided the fromPip is part of the moves.
		if (isFromBarInMoves) game.getBars().highlight(parseColor(fromBar));
	}
	
	/**
	 * Execute the roll dice methods of dices object.
	 * BOTTOM is the player with the perspective from the bottom, dices will be on the left.
	 * TOP is the player with the perspective from the top, dices will be on the right.
	 * @param pov - player's point of view. (i.e. TOP or BOTTOM).
	 * @return result of each dice roll in terms of an array of integers.
	 */
	public DieResults rollDices(PlayerPerspectiveFrom pov) {
		leftBoard.setCenter(null);
		rightBoard.setCenter(null);
		DieResults res = null;
		
		switch (pov) {
			case BOTTOM:
				leftDices = dices;
				leftBoard.setCenter(leftDices);
				rightBoard.setCenter(null);
				res = dices.getTotalRoll(DieInstance.DEFAULT);
				break;
			case TOP:
				rightDices = dices;
				rightBoard.setCenter(rightDices);
				leftBoard.setCenter(null);
				res = dices.getTotalRoll(DieInstance.DEFAULT);
				break;
			case NONE:
				leftBoard.setCenter(null);
				rightBoard.setCenter(null);
		}
		
		return res;
	}
	
	/**
	 * Execute the roll dice methods of dices object.
	 * Used to check which player rolls first.
	 * If draw, roll again.
	 * @param instance, instance where the dices are single, double or default.
	 * @return result of each dice roll in terms of an array of integers.
	 */
	public DieResults rollDices(DieInstance instance) {
		leftBoard.setCenter(null);
		rightBoard.setCenter(null);
		DieResults res = new DieResults();
		
		switch (instance) {
			case SINGLE:
				leftDices = new Dices(Color.RED);
				rightDices = new Dices(Color.RED);
				res.add(((Dices)leftDices).getTotalRoll(instance).getFirst());
				res.add(((Dices)rightDices).getTotalRoll(instance).getFirst());
				leftBoard.setCenter(leftDices);
				rightBoard.setCenter(rightDices);
				break;
			default:
				leftBoard.setCenter(null);
				rightBoard.setCenter(null);
		}
		
		// if draw, roll again.
		if (res.getFirst().getDiceResult() == res.getLast().getDiceResult()) {
			res = rollDices(instance);
		}
		return res;
	}
	
	/**
	 * Moves a checker between pips.
	 * i.e. pops a checker from one point and push it to the other.
	 * 
	 * @param fromPip, zero-based index, the point number to pop from.
	 * @param toPip, zero-based index, the point number to push to.
	 * @return returns a integer value indicating if the checker was moved.
	 */
	public MoveResult moveCheckers(int fromPip, int toPip) {
		MoveResult moveResult = isPipToPipMove(fromPip, toPip, null);
		
		switch (moveResult) {
			case MOVED_TO_PIP:
				pips[toPip].push(pips[fromPip].pop());
				pips[toPip].drawCheckers();
				pips[fromPip].drawCheckers();
				break;
			case MOVED_TO_BAR:
				break;
			default:
		}
		
		return moveResult;
	}
}
