package musicplayer;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.ArrayList;
import java.util.Random;

import constants.GameConstants;

/**
 * This class controls the music functionalities of the game.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class MusicPlayer {
	private final String defaultMusic = "jazz.aiff";
	private MediaPlayer mediaPlayer;
	private Media media;
	private ArrayList<String> playlist;
	private String currentMusic;
	
	public MusicPlayer() {
		initPlaylist();
		initMediaPlayer(defaultMusic);
		currentMusic = defaultMusic;
	}
	
	/**
	 * Method to load the music file into the mediaPlayer object to be played
	 * @param fileName to be loaded into the media object to play the music
	 */
	private void initMediaPlayer(String fileName) {
		media = new Media(getClass().getResource("/musicplayer/songs/" + fileName).toExternalForm());
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setVolume(0.1);

		// We cannot play the file instantly as it has not properly loaded.
		// Therefore we can only call it when it's loaded, 
		// i.e getStatus() == READY.
		mediaPlayer.setOnReady(new Runnable() {
			public void run() {
				if (GameConstants.VERY_VERBOSE_MODE) {
					System.out.println("Run status: " + mediaPlayer.getStatus()); // Should return READY
					System.out.println("Music duration: " + media.getDuration().toMinutes());
					System.out.println("Volume: " + mediaPlayer.getVolume());
					System.out.println("Tracks: " + media.getTracks());
				}
				mediaPlayer.setAutoPlay(true);
			}
		});
		
		mediaPlayer.setOnError(new Runnable() {
			public void run() {
				if (GameConstants.VERY_VERBOSE_MODE) {
					System.out.println("Run status: " + mediaPlayer.getStatus());
					System.out.println("Error status: " + mediaPlayer.getError());
				}
			}
		});
		
		mediaPlayer.setOnEndOfMedia(new Runnable() {
			public void run() {
				play();
			}
		});

		if (GameConstants.VERY_VERBOSE_MODE) {
			System.out.println("Init music status: " + mediaPlayer.getStatus()); // Should return UNKNOWN
		}
	}
	
	private void initPlaylist() { // Can implement a text file and just read from text file for the names
		playlist = new ArrayList<String>();
		playlist.add("jazz.aiff");
		playlist.add("classical.aiff");
	}
	
	public void random() {
		stop();
		
		Random random = new Random();
		int indexOfSong = random.nextInt(2) + 1;
		currentMusic = playlist.get(indexOfSong-1);
		
		play();
	}
	
	public void repeat() {
		mediaPlayer.setOnEndOfMedia(new Runnable() {
			public void run() {
				mediaPlayer.play();
			}
		});
	}
	
	public void play() {
		mediaPlayer.play();
	}
	
	public void next() {
		stop();
		
		int indexOfSong = playlist.indexOf(currentMusic);

		// If indexOfSong isn't the last song.
		if (indexOfSong == playlist.size() - 1)
			currentMusic = playlist.get(0);
		else
			currentMusic = playlist.get(indexOfSong + 1);
		
		initMediaPlayer(currentMusic);
	}
	
	public void prev() {
		stop();
		
		int indexOfSong = playlist.indexOf(currentMusic);

		// If indexOfSong isn't the first song.
		if (indexOfSong != 0)
			currentMusic = playlist.get(indexOfSong - 1);
		else
			currentMusic = playlist.get(playlist.size() - 1);
		
		initMediaPlayer(currentMusic);
	}
	
	/**
	 * This is to show the user that their command worked
	 * @return String representation of the status of the mediaPlayer object
	 */
	public String getStatus(String option) {
		String outputStatus = "";
		switch (option) {
			case "play":
				outputStatus = "Playing music..";
				break;
			case "next":
				outputStatus = "Next track..";
				break;
			case "prev":
				outputStatus = "Previous track..";
				break;
			case "pause":
				outputStatus = "Pausing music..";
				break;
			case "stop":
				outputStatus = "Stopping music..";
				break;
			case "random":
				outputStatus = "Random song..";
				break;
			case "mute":
				outputStatus = "Muting..";
				break;
			case "unmute":
				outputStatus = "Unmuting..";
				break;
			default:
				outputStatus = "Please try again";
		}
		return outputStatus;
	}
	
	public void pause() {
		mediaPlayer.pause();
	}
	
	public void stop() {
		mediaPlayer.stop();
	}
	
	public void muteVolume(boolean toggle) {
		mediaPlayer.setMute(toggle);
	}
	
	public void reset() {
		random();
	}
}
