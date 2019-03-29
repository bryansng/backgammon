package game;

public class DoublingCubeStorer extends TouchablesStorer {
	public DoublingCubeStorer() {
		super();
	}
	
	public void addCube() {
		initCube();
		drawCube();
	}
	
	/**
	 * Initialize num number of checkers with the checkerColor and pushes them to the stack.
	 * Then draw the checkers (i.e. add them to the point object that will be drawn on the stage).
	 * @param num number of checkers.
	 * @param checkerColor color of the checkers.
	 */
	public void initCube() {
		removeCube();
		
		push(new DoublingCube());
		drawCube();
	}
	
	/**
	 * Removes all checkers in the storer (pop off stack).
	 */
	public void removeCube() {
		getChildren().clear();
		clear();
		drawCube();
	}
}
