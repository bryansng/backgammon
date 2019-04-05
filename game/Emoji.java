package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * This class represents the emoji object in the player's panel.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class Emoji extends ImageView {
	/*
	 * default face.
	 * thinking face. (double, roll, move)
	 * hit face. (implement a delay to reset the face back to thinking face)
	 * win face.
	 * lose face.
	 */
	private ArrayList<Image> defaultImgs, thinkingImgs, hitImgs, winImgs, loseImgs;
	private Random rand = new Random();
	private final int DELAY = 10;
	
	public Emoji() {
		super();
		initImages();
	}
	
	private void initImages() {
		defaultImgs = new ArrayList<>();
		thinkingImgs = new ArrayList<>();
		hitImgs = new ArrayList<>();
		winImgs = new ArrayList<>();
		loseImgs = new ArrayList<>();
		loadImagesFromFileIntoArray("default", defaultImgs);
		loadImagesFromFileIntoArray("thinking", thinkingImgs);
		loadImagesFromFileIntoArray("hit", hitImgs);
		loadImagesFromFileIntoArray("win", winImgs);
		loadImagesFromFileIntoArray("lose", loseImgs);
		reset();
	}
	
	private void loadImagesFromFileIntoArray(String folderPath, ArrayList<Image> arr) {
		String directoryPath = "/game/img/player_panel/".concat(folderPath);
		
		// works in Eclipse and JAR, but not as flexible.
		//
		// The below goes to the hard-created txt containing names of the image file names,
		// then it loops through the txt file to get the file names and loads them.
		String classPath = System.getProperty("java.class.path");
		if (classPath.contains(".jar")) {
			// gets just the names of the images, and loads them via getResourceAsStream.
			InputStream txtStream = getClass().getResourceAsStream(directoryPath + "/" + folderPath + ".txt");
			try (BufferedReader br = new BufferedReader(new InputStreamReader(txtStream))) {
			    String line = "";
			    while ((line = br.readLine()) != null) {
			    	InputStream imgStream = getClass().getResourceAsStream(directoryPath + "/" + line);
			    	try {
			    		arr.add(new Image(imgStream));
			    		imgStream.close();
			    	} catch (IOException e) {
			    		e.printStackTrace();
			    	}
			    }
				txtStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		// works in Eclipse but not JAR.
		// This is more flexible since we don't need to hard-create a txt file
		// containing image filenames.
		} else {
			File directory = new File(classPath + directoryPath);
			for (File imgFile : directory.listFiles()) {
				if (imgFile.getPath().endsWith(".png"))
					arr.add(convertFileToImage(imgFile));
			}
		}
		/* THE BELOW IS EXPERIMENTAL.
		// below works in Eclipse, but not in JAR.
		String mainPath = "";
		try {
			mainPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		System.out.println("Main path: " + mainPath + "\n\n");
		validateIsDirectory(new File(mainPath + path));
		*/
	}
	
	private Image convertFileToImage(File file) {
		Image image = null;
		InputStream input = null;
		try {
			input = new FileInputStream(file.getPath());
			image = new Image(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image;
	}
	
	public void setDefaultFace() {
		setImage(defaultImgs.get(rand.nextInt(defaultImgs.size())));
	}
	
	public void setThinkingFace() {
		setImage(thinkingImgs.get(rand.nextInt(thinkingImgs.size())));
	}
	
	public void setHitFace() {
		setImage(hitImgs.get(rand.nextInt(hitImgs.size())));
		
		Timeline hitPause = new Timeline(new KeyFrame(Duration.seconds(DELAY), ev -> {
			setThinkingFace();
		}));
		hitPause.setCycleCount(1);
		hitPause.play();
	}
	
	public void setWinFace() {
		setImage(winImgs.get(rand.nextInt(winImgs.size())));
	}
	
	// hasPause used to induce a temporary sad face.
	public void setLoseFace() {
		setLoseFace(false);
	}
	public void setLoseFace(boolean hasPause) {
		setImage(loseImgs.get(rand.nextInt(loseImgs.size())));
		
		if (hasPause) {
			Timeline losePause = new Timeline(new KeyFrame(Duration.seconds(DELAY), ev -> {
				setThinkingFace();
			}));
			losePause.setCycleCount(1);
			losePause.play();
		}
	}
	
	public void reset() {
		setDefaultFace();
	}
	
	@SuppressWarnings("unused")
	private void validateIsDirectory(File file) {
		System.out.println("File exists: " + file.exists());
		System.out.println("hitFile: " + file.getPath());
		System.out.println("isDirectory: " + file.isDirectory());
		System.out.println("isFile: " + file.isFile());
		System.out.println("Its contents: " + Arrays.toString(file.list()));
		System.out.println("Its contents: " + Arrays.toString(file.listFiles()));
	}
	
	@SuppressWarnings("unused")
	private void validateIsFile(File file) {
		System.out.println("img path: " + file.getAbsolutePath());
		System.out.println("is img file: " + file.isFile());
	}
}
