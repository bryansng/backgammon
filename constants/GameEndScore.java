package constants;

/**
 * This enum represents the game end score in the Backgammon game.
 * Opponent's furthest checker:
 * - Nada - represents nothing in the game, used to represent 0.
 * - Single - in his inner board.
 * - Gammon - in outer boards.
 * - Backgammon - in winner's inner board.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public enum GameEndScore {
	NADA, SINGLE, GAMMON, BACKGAMMON;
}
