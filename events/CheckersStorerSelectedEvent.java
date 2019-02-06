package events;

import game_engine.CheckersStorer;
import javafx.event.EventType;

/**
 * 
 * @author Bryan Sng
 * References:
 * https://stackoverflow.com/questions/27416758/how-to-emit-and-handle-custom-events
 * https://stackoverflow.com/questions/46649406/custom-javafx-events?noredirect=1&lq=1
 *
 */
@SuppressWarnings("serial")
public class CheckersStorerSelectedEvent extends CheckersStorerEvent {
	public static final EventType<CheckersStorerEvent> STORER_SELECTED = new EventType<>(STORER, "STORER_SELECTED");
	
	private final CheckersStorer storerSelected;
	
	public CheckersStorerSelectedEvent(CheckersStorer checkersStorerSelected) {
		super(STORER_SELECTED);
		this.storerSelected = checkersStorerSelected;
	}
	
	@Override
	public void invokeHandler(CheckersStorerHandler handler) {
		handler.onClicked(storerSelected);
	}
}
