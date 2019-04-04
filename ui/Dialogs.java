package ui;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Class that handles common components in dialogs.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class Dialogs<T> extends Dialog<T> {		
	// Each dialog has a start/play again button and a cancel button.
	ButtonType button;
	
	public Dialogs(String string, Stage stage, String btnString) {
		initDialog(string, stage);
		setButton(btnString);
	}
	
	private void initDialog(String string, Stage stage) {
		setTitle(string);
		initModality(Modality.APPLICATION_MODAL);
		initOwner(stage);
	}
	
	private void setButton(String string) {
		button = new ButtonType(string, ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, button);
	}
	
	public ButtonType getButton() {
		return button;
	}
}