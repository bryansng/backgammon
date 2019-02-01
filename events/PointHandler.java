package events;

import javafx.event.EventHandler;

public abstract class PointHandler implements EventHandler<PointEvent> {
	public abstract void onClicked(int pointSelected);
	
	@Override
	public void handle(PointEvent event) {
		event.invokeHandler(this);
	}
}
