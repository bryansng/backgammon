package constants;

/**
 * This enum represents the game end score in the Backgammon game.
 * Opponent:
 * - Nada - represents nothing in the game, used to represent 0.
 * - Single - has checkers bore off.
 * - Gammon - has no checkers bore off, and furthest checker on board is in outer baord or their home board, then 2.
 * - Backgammon - has no checkers and last checker is in opponent's home board or bar, then 3.
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
