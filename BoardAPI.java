/**
 * Backgammon BoardAPI for bots by @author Chris for Sprint 5.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public interface BoardAPI {

    int[][] get();

    int getNumCheckers(int player, int pip);

    Plays getPossiblePlays(Player player, Dice dice);

    boolean lastCheckerInInnerBoard(Player player);

    boolean lastCheckerInOpponentsInnerBoard(Player player);

    boolean allCheckersOff(Player player);

    boolean hasCheckerOff(Player player);
}
