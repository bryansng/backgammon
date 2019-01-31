package game_engine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * A GridPane with certain settings + all game components added to it
 * @author @LxEmily
 *
 */
public class Layout extends GridPane {
	 
	public Layout (VBox board, TextArea infoPanel, Button rollDie, TextArea commandPanel) {
		super();
		setLayout(15, 5, 10, Pos.CENTER);
		addToLayout(board, infoPanel, rollDie, commandPanel);
	}

	public void setLayout(int padding, int vgap, int hgap, Pos position) {		
		setPadding(new Insets(padding));
		setVgap(vgap);
		setHgap(hgap);
		setAlignment(position);
	}
	
	public void addToLayout(VBox board, TextArea infoPanel, Button rollDie, TextArea commandPanel) {
		add(board, 0, 0, 1, 3);
		add(infoPanel, 1, 0);
		add(rollDie, 1, 1);
		add(commandPanel, 1, 2);
	}
}
