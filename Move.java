/**
 * Backgammon solution by @author Chris for Sprint 5.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class Move {
    // Move holds the details of single checker movement from one pip to another.

    private int fromPip, toPip;
    private boolean hit;

    Move() {
        fromPip = 0;
        toPip = 0;
        hit = false;
    }

    Move(int fromPip, int toPip, boolean hit) {
        this.fromPip = fromPip;
        this.toPip = toPip;
        this.hit = hit;
    }

    public int getFromPip() {
        return fromPip;
    }

    public int getToPip() {
        return toPip;
    }

    public int getPipDifference() {
        return fromPip-toPip;
    }

    public boolean isHit() {
        return hit;
    }

    public boolean equals(Move query) {
        return this.fromPip==query.fromPip && this.toPip==query.toPip;
    }

    public String toString() {
        String fromPipText, toPipText, hitText;
        if (fromPip == Board.BAR) {
            fromPipText = "Bar";
        } else {
            fromPipText = Integer.toString(fromPip);
        }
        if (toPip == Board.BEAR_OFF) {
            toPipText = "Off";
        } else {
            toPipText = Integer.toString(toPip);
        }
        if (hit) {
            hitText = "*";
        } else {
            hitText = "";
        }
        return fromPipText + "-" + toPipText + hitText;
    }

}
