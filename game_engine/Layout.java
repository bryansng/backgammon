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
public class Layout {
	
	private GridPane layout;
	 
	public Layout () {
		setLayout(new GridPane(), 15, 5, 10, Pos.CENTER);
	}

	public void setLayout(GridPane grid, int padding, int vgap, int hgap, Pos position) {
		this.layout = grid;
		layout.setPadding(new Insets(padding));
		layout.setVgap(vgap);
		layout.setHgap(hgap);
		layout.setAlignment(position);
	}
	
	public GridPane addToLayout(VBox board, TextArea infoPanel, Button rollDie, TextArea commandPanel) {
		layout.add(board, 0, 0, 1, 3);
		layout.add(infoPanel, 1, 0);
		layout.add(rollDie, 1, 1);
		layout.add(commandPanel, 1, 2);
		
		return getLayout();
	}
	
	public GridPane getLayout() {
		return layout;
	}
}
