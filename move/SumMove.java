package move;

import java.util.LinkedList;
import interfaces.ColorParser;

/**
 * This abstract class represents the SumMove.
 * This abstract class should be extended by all classes that implement the Move interface.
 * To animate checker hopping, animator will need the intermediate move.
 * 
 * EXPLANATION of Move data structure:
 * There are two types of Moves, a normal move and a sum move.
 * A normal move is derived from the calculation of a single dice roll result.
 * A sum move is derived from the calculation of a pair of single dice roll result, i.e. sum of a pair, therefore SumMove.
 * Since SumMove is their superclass, All normal move can be a sum move provided it has an intermediate move.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public abstract class SumMove extends Move implements ColorParser {
	private LinkedList<Move> intermediateMoves;
	
	public SumMove(LinkedList<Move> intermediateMoves, RollMoves rollMoves, boolean isHit) {
		super(rollMoves, isHit);
		this.intermediateMoves = intermediateMoves;
	}
	
	public boolean hasIntermediateMoves() {
		return intermediateMoves != null;
	}
	
	public LinkedList<Move> getIntermediateMoves() {
		return intermediateMoves;
	}
	
	public String printIntermediate(String spaces) {
		String s = "";
		if (hasIntermediateMoves()) {
			String prefix = "IM - ";
			s += spaces + "IntermediateMoves:\n";
			
			for (Move aMove : intermediateMoves) {
				if (aMove instanceof PipToPip) {
					PipToPip move = (PipToPip) aMove;
					s += spaces + prefix + "fromPip: " + move.getFromPip() + ", toPip: " + move.getToPip() + "\n";
				} else if (aMove instanceof PipToHome) {
					PipToHome move = (PipToHome) aMove;
					s += spaces + prefix + "fromPip: " + move.getFromPip() + ", toHome\n";
				} else if (aMove instanceof BarToPip) {
					BarToPip move = (BarToPip) aMove;
					s += spaces + prefix + "fromBar: " + parseColor(move.getFromBar()) + ", toPip: " + move.getToPip() + "\n";
				}
			}
		}
		return s;
	}
}
