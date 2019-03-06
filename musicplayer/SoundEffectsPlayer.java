package musicplayer;

import java.net.URL;

import javafx.scene.media.AudioClip;

/**
 * This class controls the sound effects of the game
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
	
	public SoundEffectsPlayer() {
		initCheckerSound();
		//dice = new AudioClip(getPathOfMusic("dice.aiff"));
	}
	
	public void playCheckerSound() {
		checker.setVolume(100);
		checker.play();
		System.out.println("Checker: SUCESS");
	}
	
	public void playDiceSound() {
		dice.play();
	}
	
	private void initCheckerSound() {
		checker = new AudioClip(convertToURL("checker"));
	}
	
	private String getPathOfMusic(String fileName) {
		return ("musicplayer/songs/" + fileName);
	}
	
	private String convertToURL(String fileName) {
		URL url = getClass().getClassLoader().getResource(getPathOfMusic(fileName));
		return url.toExternalForm();
	}
}
