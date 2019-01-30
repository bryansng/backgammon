package game_engine;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileInputStream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class UI extends Application {
	
	private final double STATUS_HEIGHT = 500;
	
	public void start (Stage stage) throws Exception {	
		
		// Components
		
		// Left for game board
		ImageView board = new ImageView(new Image(new FileInputStream("src/board.png")));

		// Right top for game status related text
		TextArea status = new TextArea("> " + "Welcome to Backgammon!\n");	
		status.setMinHeight(STATUS_HEIGHT);
		status.setEditable(false);
		status.setWrapText(true);
		
		// Right middle for Roll Dice button
        Button rollDie = new Button("Roll Die");
        rollDie.setMinHeight(40);
		rollDie.setMaxWidth(Double.MAX_VALUE);		
		rollDie.setOnMousePressed(new EventHandler<MouseEvent>() { 
	         @Override 
	         public void handle(MouseEvent e) { 
	        	 status.setText(status.getText() + "> You rolled the die.\n");
	        	 rollDie.setEffect(new DropShadow());
	         } 
	    });  
		rollDie.setOnMouseReleased(new EventHandler<MouseEvent>() { 
	         @Override 
	         public void handle(MouseEvent e) { 
	        	 rollDie.setEffect(new DropShadow(0, Color.BLACK));
	         } 
	    });  
	 
		
				
		// Right bottom for player input
		TextArea commandPanel = new TextArea();
		commandPanel.setWrapText(true);
		commandPanel.setPromptText("Player inputs text here, then hit Enter\n");	
		commandPanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent keyEvent) {
		        if (keyEvent.getCode() == KeyCode.ENTER)  {
		        	
		        	// Lets user quit game by entering "quit"
		        	if ("quit\n".compareTo(commandPanel.getText()) == 0) {
	            		status.setText(status.getText() + "> " + "You have quitted the game. Bye bye!\n");
	            		System.out.println("quit game"); // for debug
	            		Platform.exit();
		        	}
		        	
		        	// Do not allow user to enter empty string
		        	else if ("\n".compareTo(commandPanel.getText()) == 0) {
	            		// do nothing
		        	}

		        	// Displays user input on game status panel
		        	else {
		        		status.setText(status.getText() + "> " + commandPanel.getText());
		        	}
		            
		            // clear text
		            commandPanel.setText("");
		        }
		    }
		});
		
		// Create and customize layout		
		GridPane layout = new GridPane();
		layout.setPadding(new Insets(15));
		layout.setVgap(5);
		layout.setHgap(10);
		layout.setAlignment(Pos.CENTER);		
		
		// Add components to layout
		// The board lies in coordinate (0,0) and spans 1 col, 3 rows
		layout.add(board, 0, 0, 1, 3);
		layout.add(status, 1, 0);
		layout.add(rollDie, 1, 1);
		layout.add(commandPanel, 1, 2);
		
		GridPane.setMargin(rollDie, new Insets(5, 0, 5, 0));
		
		// Add layout to scene and set its size to screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Scene scene = new Scene(layout, screenSize.getWidth(), screenSize.getHeight());
		
		// CTRL+R also rolls die
		scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
		    final KeyCombination keyComb = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
		    public void handle(KeyEvent key) {
		        if (keyComb.match(key)) {
		            System.out.println("Key Pressed: " + keyComb);
		            status.setText(status.getText() + "> You rolled the die.\n");
		            key.consume(); // <-- stops passing the event to next node
		        }
		    }
		});
		
		// Add scene to stage
		stage.setTitle("Backgammon");
		stage.setMinWidth(layout.getWidth());
		stage.setMinHeight(layout.getHeight());
		stage.setMaxWidth(screenSize.getWidth());
		stage.setMaxHeight(screenSize.getHeight());
		stage.setMaximized(true);
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main (String args[]) {
		launch(args);
	}
}
