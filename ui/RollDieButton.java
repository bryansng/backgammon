package ui;

import constants.GameConstants;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * This class represents the roll die button in Backgammon.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 * 
 */
public class RollDieButton extends Button {
	public RollDieButton () {
		super("Roll Die");
		setMaxWidth(Double.MAX_VALUE);
		setMinHeight(GameConstants.getUIHeight());
		setFont(GameConstants.getFont());
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
	
	public void removeListeners() {
		setOnMousePressed(null);
		setOnMouseReleased(null);
	}
}
