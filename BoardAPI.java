public interface BoardAPI {

    int[][] get();

    int getNumCheckers(int player, int pip);

    Plays getPossiblePlays(Player player, Dice dice);

    boolean lastCheckerInInnerBoard(Player player);

    boolean lastCheckerInOpponentsInnerBoard(Player player);

    boolean allCheckersOff(Player player);

    boolean hasCheckerOff(Player player);
}
