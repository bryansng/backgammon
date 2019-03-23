package musicplayer;

import javafx.scene.media.AudioClip;

/**
 * This class controls the sound effects of the game.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class SoundEffectsPlayer {
	private double volume = 0.1;
	private AudioClip checker;
	private AudioClip dice;
	private AudioClip bearOff;
	private AudioClip bearOn;
	private AudioClip hit;
	
	public SoundEffectsPlayer() {
		initCheckerSound();
		initDiceSound();
		initBearOffSound();
		initBearOnSound();
		initHitCheckerSound();
	}
	
	public void playCheckerSound() {
		checker.setVolume(volume);
		checker.play();
	}
	
	public void playDiceSound() {
		dice.setVolume(volume);
		dice.play();
	}
	
	public void playBearOffSound() {
		bearOff.setVolume(volume);
		bearOff.play();
	}
	
	public void playBearOnSound() {
		bearOn.setVolume(volume);
		bearOn.play();
	}
	
	public void playCheckerHitSound() {
		hit.setVolume(volume);
		hit.play();
	}
	
	private void initCheckerSound() {
		checker = new AudioClip(convertToURL("checker.aiff"));
	}
	
	private void initDiceSound() {
		dice = new AudioClip(convertToURL("dice.aiff"));
	}
	
	private void initBearOffSound() {
		bearOff = new AudioClip(convertToURL("bearoff.aiff"));
	}
	
	private void initBearOnSound() {
		bearOn = new AudioClip(convertToURL("bearon.aiff"));
	}
	
	private void initHitCheckerSound() {
		hit = new AudioClip(convertToURL("hit.aiff"));
	}
	
	private String convertToURL(String fileName) {
		String source = getClass().getResource("/musicplayer/songs/" + fileName).toExternalForm();
		return source;
	}
}
