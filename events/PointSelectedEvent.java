package events;

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
public class PointSelectedEvent extends PointEvent {
	public static final EventType<PointEvent> POINT_SELECTED = new EventType<>(POINT, "POINT_SELECTED");
	
	private final int pointSelected;
	
	public PointSelectedEvent(int pointSelected) {
		super(POINT_SELECTED);
		this.pointSelected = pointSelected;
	}
	
	@Override
	public void invokeHandler(PointHandler handler) {
		handler.onClicked(pointSelected);
	}
}
