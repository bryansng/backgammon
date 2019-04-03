package game;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import game_engine.Player;
import constants.GameConstants;


public class Clock {
	
	Timer timer;
	private long delay;
	private long period;
	private long duration;
	private long timeToDeduct;
	private long elapsedTime;
	private boolean cancel;
	
	public Clock() {
		timer = new Timer("Clock");
		delay = 1000;
		period = 1000;
		duration = 15000; // 15 seconds
		timeToDeduct = 0;
		elapsedTime = 0;
		cancel = false;
	}
	
	public void countdown(PlayerPanel moveTimer, PlayerPanel playerTimer, Player pCurrent) {
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (GameConstants.exit)
					killTimer();
				
				moveTimer.updateMoveTimer();
				countSeconds();
				System.out.println("duration: " + duration);
				
				if (cancel) {
					reset();
					cancel();
				} else if (isMaxDuration()) {
					cancel();
					reset();
					startTimer(playerTimer, pCurrent);
				}
			}
		}, delay, period);
	}
	
	public void killTimer() {
		timer.cancel();
		timer.purge();
	}
	
	/**
	 * Design thoughts:
	 * 
	 * Once the safe timer runs out, remove it from the panel, and start decrementing the playerTimer.
	 * Once the timer stops, then update the playerTimer, and insert the safe timer back into the playerPanel
	 */
	
	public void startTimer(PlayerPanel playerTimer, Player pCurrent) { 
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (GameConstants.exit)
					killTimer();
				
				if (cancel) {
					reset();
					cancel();
				}
				
				Date createdDate = new Date();
				timeToDeduct = createdDate.getTime(); // Printing time gives 0 mins and 0 seconds, BUG
				playerTimer.updatePlayerTimer();
				pCurrent.setTime(pCurrent.getTime() - 1);
			}
		}, delay, period); // delay, duration
	}
	
	public void stopTimer(Player pCurrent) {
		cancel = true;
		Date currentDate = new Date();
		elapsedTime = (currentDate.getTime() - timeToDeduct);
		pCurrent.setTime(pCurrent.getTime() - convertToSeconds(elapsedTime));
	}
	
	private void reset() {
		this.duration = 15000;
		cancel = false;
	}
	
	public void restartTimer() {
		timer = new Timer(); // After canceling all tasks on the current timer, we need to create a new one in order to assign tasks again
	}
	
	public long convertToMillis(long minutes) {
		return TimeUnit.MINUTES.toMillis(minutes);
	}
	
	public long convertToMinutes(long millis) {
		return TimeUnit.MILLISECONDS.toMinutes(millis);
	}
	
	public long convertToSeconds(long millis) {
		return TimeUnit.MILLISECONDS.toSeconds(millis);
	}
	
	private void countSeconds() {
		this.duration -= 1000;
	}
	
	public long getSeconds() {
		return convertToSeconds(duration);
	}
	
	/**
	 * 
	 * NOTE:
	 * 	- These setter methods will allow us to implement it into the setting method
	 * 
	 * @param delay
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public long getDuration() {
		return this.duration;
	}
	
	public long getElapsedTime() {
		return elapsedTime;
	}
	
	public boolean isMaxDuration() {
		return this.duration == 0;
	}
	
	public String toString(long time) {
		return "" + time;
	}
}
