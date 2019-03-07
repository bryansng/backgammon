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
		checker.setVolume(100000);
		checker.play();
	}
	
	public void playDiceSound() {
		dice.setVolume(100000);
		dice.play();
	}
	
	public void playBearOffSound() {
		bearOff.setVolume(100000);
		bearOff.play();
	}
	
	public void playBearOnSound() {
		bearOn.setVolume(100000);
		bearOn.play();
	}
	
	public void playCheckerHitSound() {
		hit.setVolume(100000);
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
