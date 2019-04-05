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
	
	public Dialogs(String headerText, Stage stage, String btnText) {
		initDialog(headerText, stage);
		setButton(btnText);
	}
	
	private void initDialog(String headerText, Stage stage) {
		setTitle(headerText);
		initModality(Modality.APPLICATION_MODAL);
		initOwner(stage);
	}
	
	private void setButton(String btnText) {
		button = new ButtonType(btnText, ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, button);
	}
	
	public ButtonType getButton() {
		return button;
	}
}