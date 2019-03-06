/**
 * 
 */
package musicplayer;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class controls the music functionalities of the game
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class MusicPlayer {

	/**
	 * Private fields
	 */
	private MediaPlayer mediaPlayer;
	private Media media;
	private ArrayList<String> playlist;
	private final String defaultMusic = "jazz.aiff";
	private String currentMusic;

	/**
	 * Constructor
	 */
	public MusicPlayer() {
		initPlaylist();
		initMediaPlayer(defaultMusic);
		setCurrentMusic(defaultMusic);
	}

	/**
	 * Method to load the music file into the mediaPlayer object to be played
	 * 
	 * @param fileName to be loaded into the media object to play the music
	 */
	private void initMediaPlayer(String fileName) {
		media = new Media(new File(getPathOfMusic(fileName)).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setVolume(50);

		/**
		 * We cannot play the file instantly as it has not properly loaded. Therefore we can only call it when it's loaded, i.e getStatus() == READY
		 */
		mediaPlayer.setOnReady(new Runnable() {
			public void run() {
				mediaPlayer.setVolume(100);
				/**
				 * Debugging purposes
				 */
				System.out.println("Run status: " + mediaPlayer.getStatus()); // Should return READY
				System.out.println("Music duration: " + media.getDuration().toMinutes());
				System.out.println("Volume: " + mediaPlayer.getVolume());
				System.out.println("Tracks: " + media.getTracks());
				mediaPlayer.setAutoPlay(true);
			}
		});

		mediaPlayer.setOnError(new Runnable() {
			public void run() {
				System.out.println("Run status: " + mediaPlayer.getStatus());
				System.out.println("Error status: " + mediaPlayer.getError());
			}
		});
		
		mediaPlayer.setOnEndOfMedia(new Runnable() {
			public void run() {
				play();
			}
		});

		System.out.println("Init music status: " + mediaPlayer.getStatus()); // Should return UNKNOWN
	}

	private String getPathOfMusic(String fileName) {
		return "musicplayer/songs/" + fileName;
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

		setCurrentMusic(getPathOfMusic(playlist.get(indexOfSong-1)));
		
		play();
	}

	public void setCurrentMusic(String fileName) {
		this.currentMusic = fileName;
	}

	public String getCurrentMusic() {
		return this.currentMusic;
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

		/**
		 * If indexOfSong isn't the last song
		 */
		if (indexOfSong == playlist.size() - 1)
			setCurrentMusic(playlist.get(0));
		else
			setCurrentMusic(playlist.get(indexOfSong + 1));
		
		initMediaPlayer(getCurrentMusic());
	}

	public void prev() {
		stop();
		
		int indexOfSong = playlist.indexOf(currentMusic);

		/**
		 * If indexOfSong isn't the first song
		 */
		if (indexOfSong != 0)
			setCurrentMusic(playlist.get(indexOfSong - 1));
		else
			setCurrentMusic(playlist.get(playlist.size() - 1));
		
		initMediaPlayer(getCurrentMusic());
	}

	/**
	 * 
	 * This is to show the user that their command worked
	 * 
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
}
