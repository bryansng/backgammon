/**
 * 
 */
package game_engine;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

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

	private MediaPlayer mediaPlayer;
	private Media media;
	private ArrayList<String> playlist;
	private final String defaultMusic = "animal_crossing.aiff";
	private String currentMusic;

	protected MusicPlayer() {
		initPlaylist();
		initMediaPlayer(defaultMusic);
		setCurrentMusic(defaultMusic);
	}

	private void initMediaPlayer(String fileName) {
		media = new Media(new File(getPathOfMusic(fileName)).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		maxVolume();

		mediaPlayer.setOnReady(new Runnable() {
			public void run() {
				mediaPlayer.setVolume(100);
				System.out.println("Run status: " + mediaPlayer.getStatus());
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

		System.out.println("Init music status: " + mediaPlayer.getStatus());
	}

	private String getPathOfMusic(String fileName) {
		return "music/" + fileName;
	}

	private void initPlaylist() {
		playlist = new ArrayList<String>();

		playlist.add("animal_crossing.aiff");
		playlist.add("instagram.aiff");
	}

	public String random() {
		Random random = new Random();

		int indexOfSong = random.nextInt(1) + 0;

		return getPathOfMusic(playlist.get(indexOfSong));
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

	public void next() {
		int indexOfSong = playlist.indexOf(currentMusic);

		if (indexOfSong == playlist.size() - 1)
			setCurrentMusic(playlist.get(0));
		else
			setCurrentMusic(playlist.get(indexOfSong + 1));
	}

	public void prev() {
		int indexOfSong = playlist.indexOf(currentMusic);

		if (indexOfSong != 0)
			setCurrentMusic(playlist.get(indexOfSong - 1));

		else
			setCurrentMusic(playlist.get(playlist.size() - 1));
	}

	public String getStatus() {
		Status status = mediaPlayer.getStatus();
		String outputStatus;

		switch (status) {
			case READY:
				outputStatus = status.toString() + ": Music is ready to be played";
				break;
			case DISPOSED:
				outputStatus = status.toString() + ": Music is removed";
				break;
			case HALTED:
				outputStatus = status.toString() + ": Music has halted";
				break;
			case PAUSED:
				outputStatus = status.toString() + ": Music is paused";
				break;
			case PLAYING:
				outputStatus = status.toString() + ": Music is playing";
				break;
			case STALLED:
				outputStatus = status.toString() + ": Music has stalled";
				break;
			case STOPPED:
				outputStatus = status.toString() + ": Music has stopped";
				break;
			case UNKNOWN:
				outputStatus = status.toString() + ": File cannot be recognised or is incompatible";
				break;
			default:
				outputStatus = status.toString() + ": MAJOR ERROR";
				break;
		}
		
		return outputStatus;
	}

	public void pause() {
		mediaPlayer.pause();
	}

	public void stop() {
		mediaPlayer.stop();
	}
	
	public void maxVolume() {
		setVolume(100);
	}

	public void muteVolume(boolean toggle) {
		mediaPlayer.setMute(toggle);
	}

	public void setVolume(double volume) {
		mediaPlayer.setVolume(volume);
	}

	public double getVolume() {
		return this.mediaPlayer.getVolume();
	}
}
