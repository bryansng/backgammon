package game;

import constants.GameConstants;
import events.OutOfTimeSelectedEvent;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * This class represents the timer in the player's panel.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class GameplayTimer extends Label {
	// in seconds.
	private final long ROLL_DELAY = 15;
	private final long INTERNAL_ROLL_DELAY = ROLL_DELAY+1;
	private final long TOTAL_PLAY_TIME = convertMinutesToSeconds(30);
	
	private Timeline roll, remaining;
	private long rollTime, remainingTime;
	
	public GameplayTimer() {
		style();
		initTimeline();
		reset();
	}
	private void style() {
		setAlignment(Pos.CENTER);
		setTextAlignment(TextAlignment.CENTER);
		setTextFill(Color.WHITE);
		setFont(Font.loadFont(GameConstants.getFontInputStream(false, true), 24));
		
		// prevents the entire player panel from
		// expanding and contracting due to changing numbers in the timer. 
		setMinWidth(getPrecalculatedWidth());
		setMaxWidth(getPrecalculatedWidth());
	}
	// pre-calculate the text's width that will be shown on stage.
	private double getPrecalculatedWidth() {
		Text sample = new Text("00:00");
		sample.setFont(getFont());
		new Scene(new Group(sample));
		sample.applyCss();
		return sample.getLayoutBounds().getWidth();
	}
	
	private void initTimeline() {
		roll = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
			rollTime -= 1;
			drawRollTimer();
			
			// if roll time expended,
			// remaining time is used.
			if (rollTime == 0) {
				resetRollTimer();
				playRemainingTimer();
			}
		}));
		roll.setCycleCount((int) INTERNAL_ROLL_DELAY);
		
		remaining = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
			remainingTime -= 1;
			drawRemainingTimer();
			
			// if remaining time is expended,
			// game should end.
			if (remainingTime == 0) {
				resetRemainingTimer();
				stopRemainingTimer();
				this.fireEvent(new OutOfTimeSelectedEvent());
			}
		}));
		remaining.setCycleCount(Animation.INDEFINITE);
	}
	
	// used by GameplayController at the start of player's turn.
	public void start() {
		playRollTimer();
	}
	public void playRollTimer() {
		stopRemainingTimer();
		roll.play();
	}
	public void playRemainingTimer() {
		stopRollTimer();
		remaining.play();
	}
	
	// used by GameplayController after end of player's turn.
	public void stop() {
		resetRollTimer();
		stopRollTimer();
		stopRemainingTimer();
		drawRollTimer();
		drawRemainingTimer();
	}
	private void stopRollTimer() {
		roll.stop();
	}
	private void stopRemainingTimer() {
		remaining.stop();
	}
	
	private void drawRollTimer() {
		setText(getTimerFormat(rollTime));
	}
	private void drawRemainingTimer() {
		setText(getTimerFormat(remainingTime));
	}
	private String getTimerFormat(long seconds) {
		long minutes = convertSecondsToMinutes(seconds);
		long remainingSeconds = seconds % 60;
		
		// Print 2 decimal places.
		return String.format("%02d:%02d", minutes, remainingSeconds);
	}
	
	private long convertSecondsToMinutes(long seconds) {
		return seconds / 60;
	}
	private long convertMinutesToSeconds(long minutes) {
		return minutes * 60;
	}
	
	// reset every match, not every game.
	public void reset() {
		stop();
		resetRollTimer();
		drawRollTimer();
		resetRemainingTimer();
		drawRemainingTimer();
	}
	// reset every turn swap.
	public void resetRollTimer() {
		if (GameConstants.FORCE_OUT_OF_TIME)
			rollTime = 1;
		else rollTime = INTERNAL_ROLL_DELAY;
	}
	// reset only after an entire match.
	public void resetRemainingTimer() {
		if (GameConstants.FORCE_OUT_OF_TIME)
			remainingTime = 4;
		else remainingTime = TOTAL_PLAY_TIME;
	}
}
