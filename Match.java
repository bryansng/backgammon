public class Match implements MatchAPI {

        private Game game;
        private Cube cube;
        private Players players;
        private int matchLength;
        private boolean crawford, crawfordDone;

        Match(Game game, Cube cube, Players players) {
            this.game = game;
            this.cube = cube;
            this.players = players;
            matchLength = 0;
            crawford = false;
            crawfordDone = false;
        }

        public void setLength(int matchLength) {
            this.matchLength = matchLength;
        }

        public int getLength() {
            return matchLength;
        }

        public boolean isOver() {
            return (players.get(0).getScore()>=matchLength || players.get(1).getScore()>=matchLength);
        }

        public int getPoints() {
            int points;
            if (game.wasResigned()) {
                points = cube.getValue();
            } else {
                if (game.isSingle()) {
                    points = 1;
                } else if (game.isGammon()) {
                    points = 2;
                } else {
                    points = 3;
                }
                points = points * cube.getValue();
            }
            return points;
        }

        public void updateScores(int points) {
            Player winner = game.getWinner();
            winner.addPoints(points);
            if (!crawfordDone && (players.get(0).getScore()==matchLength-1 || players.get(1).getScore()==matchLength-1)) {
                crawfordDone = true;
                crawford = true;
            } else {
                crawford = false;
            }
        }

        public Player getWinner() {
            if (players.get(0).getScore()>players.get(1).getScore()) {
                return players.get(0);
            } else {
                return players.get(1);
            }
        }

        public boolean canDouble(Player player) {
            if (crawford || cube.getValue()<matchLength-player.getScore()) {
                return true;
            } else {
                return false;
            }
        }

        public void reset() {
            matchLength=0;
            players.reset();
            crawford = false;
            crawfordDone = false;
        }

    }
