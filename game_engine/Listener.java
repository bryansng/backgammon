package game_engine;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * Methods that "listen" for actions on game components
 * 	- Roll die with clicks or CTRL+R
 * 	- Text commands
 * 		- quit to terminate
 * 		- echoes player input
 * 		- potential: "help" for all commands, "save" to save to txt
 * @author @LxEmily
 *
 */

public class Listener {

	/**
	 * Listens for actions that roll the die
	 * 	- Clicking on rollDie button
	 * 	- CTRL + R shortcut key on overall scene
	 * @param rollDie button
	 * @param infoPanel Informs player that they rolled the die
	 * @param scene
	 */
	public void rollDieListener (Button rollDie, TextArea infoPanel, Scene scene) {
		
		// Clicking
		rollDie.setOnMousePressed(new EventHandler<MouseEvent>() { 
	         @Override 
	         public void handle(MouseEvent e) { 
	        	 // inform player
	        	 rolledDie(infoPanel);
	        	 // make button distinct on click with shadow on click
	        	 rollDie.setEffect(new DropShadow());
	         } 
	    });  
		rollDie.setOnMouseReleased(new EventHandler<MouseEvent>() { 
	         @Override 
	         public void handle(MouseEvent e) { 
	        	 // remove shadow when click is released
	        	 rollDie.setEffect(new DropShadow(0, Color.BLACK));
	         } 
	    });  
		
		// CTRL+R 
		scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
		    final KeyCombination keyComb = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
		    public void handle(KeyEvent key) {
		        if (keyComb.match(key)) {
		            System.out.println("Key Pressed: " + keyComb); // for debug
		            // inform player
		        	rolledDie(infoPanel);
		            key.consume(); // <-- stops passing the event to next node
		        }
		    }
		});
	}

	/**
	 * Listens for certain text commands from player
	 * 	- quit to terminate game
	 * 	- does not allow player to enter empty strings
	 * 	- echoes player input to infoPanel
	 * @param commandPanel listens for input 
	 * @param infoPanel echoes input
	 */
	public void commandPanelListener (TextArea commandPanel, TextArea infoPanel) {
		commandPanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent keyEvent) {
		        if (keyEvent.getCode() == KeyCode.ENTER)  {
		        	
		        	// Lets user quit game by entering "quit"
		        	if ("quit\n".compareTo(commandPanel.getText()) == 0) {
		        		printToInfoPanel("You have quitted the game. Bye bye!\n", infoPanel);
	            		System.out.println("quit game"); // for debug
	            		Platform.exit();
		        	}
		        	
		        	// Do not allow user to enter empty string
		        	else if ("\n".compareTo(commandPanel.getText()) == 0) {
	            		// do nothing
		        	}

		        	// Displays user input on game status panel
		        	else {
		        		printToInfoPanel(commandPanel.getText(), infoPanel);
		        	}
		            
		            // clear text
		            commandPanel.setText("");
		        }
		    }
		});
	}
	
	/**
	 * Prints the given text to the information panel.
	 * @param text - string to be printed
	 * @param infoPanel
	 */
	private void printToInfoPanel(String text, TextArea infoPanel) {
		// Appends text to information panel.
		infoPanel.appendText("> " + text);
	}
	
	/**
	 * Informs player they rolled the die
	 * @param infoPanel
	 */
	private void rolledDie(TextArea infoPanel) {
		printToInfoPanel("You rolled the die.\n", infoPanel);
	}
}
