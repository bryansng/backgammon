package move;

/**
 * This abstract class represents that SumMove.
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
public abstract class SumMove {
	private Move intermediateMove;
	
	public SumMove(Move intermediateMove) {
		this.intermediateMove = intermediateMove;
	}
	
	public boolean hasIntermediateMove() {
		return intermediateMove != null;
	}
	
	public Move getIntermediateMove() {
		return intermediateMove;
	}
}
