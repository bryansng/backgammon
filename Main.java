import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javafx.event.ActionEvent;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {
	private Board board;
	private TextArea infoPnl;
	
	public static void main(String[] args) {
		launch(args);	// calls start method.
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		initUI(stage);
	}
	
	private void initUI(Stage stage) {
		// Board.
		/* 
		 * 1. Point to image to display.
		 * 2. Load image from file.
		 * 3. Create imageView instance.
		 * 4. Need to pass to scene graph, therefore requires a component.
		FileInputStream input = new FileInputStream("src/img/board/board.png");
		Image image = scale(new Image(input), 0.8);
		ImageView imageView = new ImageView(image);
		HBox game = new HBox(imageView);
		 */
		board = new Board();
		Canvas canvas = new Canvas(board.getImg().getWidth(), board.getImg().getHeight());
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		// final long startNanoTime = System.nanoTime();
		
		new AnimationTimer() {
			public void handle(long currentNanoTime) {
				// double t = (currentNanoTime - startNanoTime) / 1000000000.0;
				
				// Clear canvas.
				gc.setFill(new Color(0.0, 0.0, 0.0, 0.0));	// white color.
				gc.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
				
				gc.drawImage(board.getImg(), 0, 0);
				
				Point[] points = board.getPoints();
				
				// 1-6, black home board.
				// x of middle point 1 = 920.24.
				// y of middle point 1 = 409.56 + 250.443 = 600.003.
				// between two point = 4.44
				double btwPoints = 4.44;
				// width of point = 61.28.
				double pointWidth = 61.28;
				// height of point = 250.443.
				// checker width = 56.278.
				double checkerWidth = 58.0;
				double xOffset = btwPoints + pointWidth;
				double yOffset = checkerWidth * 0.8;
				double x, y;
				
				// checker start at 1. middle of 1, bottom of 1 plus half of its width.
				for (int i = 0; i < 6; i++) {
					int j = 0;
					for (Checker chk : points[i]) {
						x = (920.24-checkerWidth/2.0) + (i*-xOffset);
						y = (660.003-checkerWidth) + (j*-yOffset);
						gc.drawImage(chk.getImg(), x, y);
						j++;
					}
				}
				
				// 7-12, black outer board.
				
				// 13-18, white outer board.
				
				// 19-24, white home board.
			}
		}.start();
		
		// Information Panel.
		infoPnl = new TextArea();
		infoPnl.setEditable(false);
		
		// Command Panel.
		TextField cmdPnl = new TextField(); 
		cmdPnl.setOnAction((ActionEvent event) -> {
			String text = cmdPnl.getText();
			
			if (text.equals("")) {
				printToInfoPanel("Error: Unknown Command.");
			}
			else {
				printToInfoPanel("Running command: " + text + ".");
			}
			
			cmdPnl.setText("");
		});
				
		// Rolling Dice button.
		Button rollBtn = new Button("Roll Dice");
		rollBtn.setOnAction((ActionEvent event) -> {
			printToInfoPanel("Rolling Dice.");
		});

		// Information panel + roll dice button.
		VBox right = new VBox();
		right.getChildren().addAll(infoPnl, rollBtn);
		
		// Game + right (info panel + roll dice button)
		HBox top = new HBox();
		top.getChildren().addAll(canvas, right);
		
		// Top + Bottom.
		VBox main = new VBox();
		main.getChildren().addAll(top, cmdPnl);
		
		// root Layout.
		HBox root = new HBox();
		root.getChildren().add(main);
		
		Scene scene = new Scene(root);
		//Scene scene = new Scene(root, Settings.getScreenWidth(), Settings.getScreenHeight()); // width, height.
		//scene.setFill(Color.TRANSPARENT);

		stage.setScene(scene);	// add scene with layout.
		stage.setTitle("Backgammon");
		
		//stage.initStyle(StageStyle.TRANSPARENT); // could be used to make it fullscreen.
		stage.setResizable(false);
		// stage.setFullScreen(true);
		stage.show();
		
		setRollDiceAccelarator(rollBtn, infoPnl);
	}
	
	
	// Print string to information panel.
	private void printToInfoPanel(String text) {
		// Appends text to information panel.
		infoPnl.appendText(text + "\n");
	}
	
	
	// Bind shortcut CTRL+R key combination to roll dice.
	private void setRollDiceAccelarator(Button button, TextArea infoPnl) {
		Scene scene = button.getScene();
		if (scene == null) {
			throw new IllegalArgumentException("Roll Dice Button not attached to a scene.");
		}
		
		scene.getAccelerators().put(
			new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN),
			new Runnable() {
				@Override
				public void run() {
					printToInfoPanel("Rolling Dice.");
				}
			}
		);
	}
}
