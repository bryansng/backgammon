public class Game {

    private Players players;
    private Cube cube;
    private Board board;
    private boolean resigned;
    private Player winner;

    Game(Board board, Cube cube, Players players) {
        this.players = players;
        this.cube = cube;
        this.board = board;
        resigned = false;
    }

    public boolean isOver() {
        boolean gameOver = false;
        if (resigned) {
            gameOver = true;
        } else if ( board.allCheckersOff(players.get(0)) || board.allCheckersOff(players.get(1))  ) {
            gameOver = true;
        }
        return gameOver;
    }

    public Player getWinner() {
        if (!resigned) {
            if (board.allCheckersOff(players.get(0))) {
                winner = players.get(0);
            } else if (board.allCheckersOff(players.get(1))) {
                winner = players.get(1);
            }
        }
        return winner;
    }

    private Player getLoser() {
        Player loser = players.get(0);
        if (resigned) {
            players.getOpposingPlayer(winner);
        } else {
            if (board.lastCheckerInInnerBoard(players.get(0))) {
                loser = players.get(1);
            } else if (board.lastCheckerInInnerBoard(players.get(1))) {
                loser = players.get(0);
            }
        }
        return loser;
    }

    public boolean isSingle() {
        return board.hasCheckerOff(getLoser());
    }

    public boolean isGammon() {
        return !board.hasCheckerOff(getLoser()) && !board.lastCheckerInOpponentsInnerBoard(getLoser());
    }

    public boolean isBackgammon() {
        return !board.hasCheckerOff(getLoser()) && board.lastCheckerInOpponentsInnerBoard(getLoser());
    }

    public void resign(Player player) {
        resigned = true;
        winner = players.getOpposingPlayer(player);
    }

    public boolean wasResigned() {
        return resigned;
    }

    public void reset() {
        resigned = false;
        cube.reset();
        board.reset();
    }

}
