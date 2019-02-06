package game_engine;

import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * This class represents the roll die button in Backgammon.
 * 
 * @author @LxEmily
 * @coauthor Bryan Sng
 * 
 */
public class RollDieButton extends Button {
	public RollDieButton () {
		super("Roll Die");
		setMaxWidth(Double.MAX_VALUE);
		setMinHeight(Settings.getTopBottomHeight());
		initEventEffects();
	}
	
	public void initEventEffects() {
		// make button distinct on click with shadow on click
		setOnMousePressed((MouseEvent event) -> {
			setEffect(new DropShadow());
		});

		// remove shadow when click is released
		setOnMouseReleased((MouseEvent event) -> {
			setEffect(new DropShadow(0, Color.BLACK));
		});
	}
}
