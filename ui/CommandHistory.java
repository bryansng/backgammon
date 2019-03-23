package ui;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * This class is an attempt at representing the command history that BASH has.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 * 
 */
@SuppressWarnings("serial")
public class CommandHistory extends LinkedList<String> {
	boolean wasUp, wasDown, isStashed;
	ListIterator<String> iterCurr;
	String curr;
	
	public CommandHistory() {
		super();
		reset();
	}
	
	// moves forwards in the linkedlist, and
	// returns the next entry.
	public String up(String newCmd) {
		wasUp = true;
		handleStashing(newCmd);
		handleEdits(newCmd);
		
		if (iterCurr.hasNext()) {
			curr = iterCurr.next();
			
			// prevents the need to enter up twice.
			if (wasDown && iterCurr.hasNext()) {
				curr = iterCurr.next();
				wasDown = false;
			}
		}
		return curr;
	}
	
	// moves backwards in the linkedlist, and
	// returns the previous entry.
	public String down(String newCmd) {
		wasDown = true;
		handleStashing(newCmd);
		handleEdits(newCmd);
		
		if (iterCurr.hasPrevious()) {
			curr = iterCurr.previous();

			// prevents the need to enter down twice.
			if (wasUp && iterCurr.hasPrevious()) {
				curr = iterCurr.previous();
				wasUp = false;
			}
		}
		return curr;
	}
	
	// adds the 'newCmd' at the front of the linkedlist.
	public void addNewCommand(String newCmd) {
		if (isStashed) removeFirst();
		addFirst(newCmd);
		reset();
	}
	
	// stashes the 'newCmd' at the front of the linkedlist.
	private void handleStashing(String newCmd) {
		if (!isStashed) {
			addFirst(newCmd);
			reset();
			isStashed = true;
		}
	}
	
	// edits current command history with 'newCmd' if edited.
	private void handleEdits(String newCmd) {
		if (isEdited(newCmd)) {
			iterCurr.set(newCmd);
		}
	}

	// checks if current command history displayed is edited.
	private boolean isEdited(String newCmd) {
		return !curr.equals(newCmd);
	}
	
	public void reset() {
		iterCurr = null;
		curr = null;
		
		iterCurr = this.listIterator();
		if (iterCurr.hasNext()) curr = iterCurr.next();
		isStashed = false;
		wasUp = false;
		wasDown = false;
	}
}
