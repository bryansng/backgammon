package move;

import java.util.Iterator;
import java.util.LinkedList;
import interfaces.InputValidator;

/**
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 *
 */
@SuppressWarnings("serial")
public class Moves extends LinkedList<RollMoves> implements InputValidator {
	public Moves() {
		super();
	}
	
	// If there are no moves left, but there are still dice results.
	// In this case, we should recalculate possible moves with the remaining dice result.
	public boolean hasDiceResultsLeft() {
		// if possible moves not empty (expected there is moves left in RollMoves),
		// but there are no moves left in each RollMoves,
		// means there are dice results left.
		boolean noMovesLeft = true;
		for (RollMoves aRollMoves : this) {
			if (!aRollMoves.getMoves().isEmpty()) {
				noMovesLeft = false;
				break;
			}
		}
		return !this.isEmpty() && noMovesLeft;
	}
	
	public Move isValidPipToPip(String fro, String to) {
		Move theValidMove = null;
		
		if (isPip(fro) && isPip(to)) {
			int fromPip = Integer.parseInt(fro);
			int toPip = Integer.parseInt(to);
			
			for (RollMoves rollMoves : this) {
				// check if fromPip is part of possible moves.
				PipToPip move = null;
				for (Move aMove : rollMoves.getMoves()) {
					if (aMove instanceof PipToPip) {
						move = (PipToPip) aMove;
						if (move.getFromPip() == fromPip) {
							// check if toPip is part of fromPip's possible toPips.
							if (move.getToPip() == toPip) theValidMove = (Move) move;
							break;
						}
					}
				}
				if (theValidMove != null) {
					rollMoves.setUsed();
					break;
				}
			}
		}
		return theValidMove;
	}
	
	public Move isValidPipToHome(String fro, String to) {
		Move theValidMove = null;
		
		if (isPip(fro) && isBarOrHome(to)) {
			int fromPip = Integer.parseInt(fro);
			
			for (RollMoves rollMoves : this) {
				// check if fromPip is part of possible moves.
				PipToHome move = null;
				for (Move aMove : rollMoves.getMoves()) {
					if (aMove instanceof PipToHome) {
						move = (PipToHome) aMove;
						if (move.getFromPip() == fromPip) {
							theValidMove = (Move) move;
							break;
						}
					}
				}
				if (theValidMove != null) {
					rollMoves.setUsed();
					break;
				}
			}
		}
		return theValidMove;
	}
	
	@SuppressWarnings("unused")
	private Move isValidBarToPip() {
		return null;
	}
	
	// Removes all the moves (PipToPip, PipToHome) that has the fromPip, 'fromPip'.
	// used by removeMovesOfEmptyCheckersStorer().
	public void removeMovesOfFromPip(int fromPip) {
		// we use this way of iterating and use iter.remove() to remove,
		// if not while removing will raised ConcurrentModificationException.
		for (Iterator<RollMoves> iterRollMoves = this.iterator(); iterRollMoves.hasNext();) {
			RollMoves aRollMoves = iterRollMoves.next();
			for (Iterator<Move> iterMove = aRollMoves.getMoves().iterator(); iterMove.hasNext();) {
				Move aMove = iterMove.next();
				if (aMove instanceof PipToPip) {
					if (((PipToPip) aMove).getFromPip() == fromPip) iterMove.remove();
				} else if (aMove instanceof PipToHome) {
					if (((PipToHome) aMove).getFromPip() == fromPip) iterMove.remove();
				}
			}
			
			// removes the entire rollMoves if it has no moves left and is used.
			if (aRollMoves.getMoves().isEmpty() && aRollMoves.isUsed()) iterRollMoves.remove();
		}
	}
	
	/**
	 * Remove the rollMoves from moves.
	 * i.e. remove everything related to the dice roll result completely from moves.
	 * @param rollMoves rollMoves to remove.
	 */
	public void removeRollMoves(RollMoves theRollMoves) {
		removeOtherRollMoves(theRollMoves);
		this.remove(theRollMoves);		// remove used rollMoves.
	}
	
	/**
	 * Removes other rollMoves from moves based on argument 'rollMoves'.
	 * Rules state,
	 * - if sum result is moved, then two other result is forfeited.
	 * - if either one result moved, sum result is forfeited.
	 * @param rollMoves rollMoves that was removed.
	 */
	private void removeOtherRollMoves(RollMoves theRollMoves) {
		// we use this way of iterating and use iter.remove() to remove,
		// if not while removing will raised ConcurrentModificationException.
		int count = 1;
		for (Iterator<RollMoves> iter = this.iterator(); iter.hasNext();) {
			RollMoves aRollMoves = iter.next();
			
			// if sum moved, remove other two.
			if (theRollMoves.isSumMove() && aRollMoves.isNormalMove()) {
				iter.remove();
				if (count == 2) break;
				count++;
			}
			// if not sum moved, remove sum.
			if (theRollMoves.isNormalMove() && aRollMoves.isSumMove()) {
				iter.remove();
				break;
			}
		}
	}
	
	/**
	 * Checks if fromPip is part of possible moves.
	 * Only used for mouse clicks of fro.
	 * Atm, considers pips, no bars.
	 * @param fromPip
	 * @return boolean value indicating if fromPip is part of possible moves.
	 */
	public boolean isValidFro(int fromPip) {
		boolean isValidFro = false;
		for (RollMoves rollMoves : this) {
			for (Move aMove : rollMoves.getMoves()) {
				if (aMove instanceof PipToPip) {
					if (((PipToPip) aMove).getFromPip() == fromPip) {
						isValidFro = true;
						break;
					}
				} else if (aMove instanceof PipToHome) {
					if (((PipToHome) aMove).getFromPip() == fromPip) {
						isValidFro = true;
						break;
					}
				}
			}
		}
		return isValidFro;
	}
}
