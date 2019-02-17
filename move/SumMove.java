package move;

// used to animate checker hopping, animator will need the intermediate move.
public abstract class SumMove {
	private Move intermediateMove;
	
	public SumMove() {
		this.intermediateMove = null;
	}
	
	public boolean hasIntermediateMove() {
		return intermediateMove != null;
	}
	
	public Move getIntermediateMove() {
		return intermediateMove;
	}
}
