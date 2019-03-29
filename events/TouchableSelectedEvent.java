package events;

import game.Touchable;
import javafx.event.EventType;

/**
 * Custom Selected Event for checkers storers.
 * 
 * References:
 * https://stackoverflow.com/questions/27416758/how-to-emit-and-handle-custom-events
 * https://stackoverflow.com/questions/46649406/custom-javafx-events?noredirect=1&lq=1
 *
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 * 
 */
@SuppressWarnings("serial")
public class TouchableSelectedEvent extends TouchableEvent {
	public static final EventType<TouchableEvent> TOUCHABLE_SELECTED = new EventType<>(TOUCHABLE, "TOUCHABLE_SELECTED");
	
	private final Touchable touchableSelected;
	
	public TouchableSelectedEvent(Touchable touchableSelected) {
		super(TOUCHABLE_SELECTED);
		this.touchableSelected = touchableSelected;
	}
	
	@Override
	public void invokeHandler(TouchableHandler handler) {
		handler.onClicked(touchableSelected);
	}
}
