public class Cube implements CubeAPI {

    private static int value;
    private static Player owner;
    private static boolean isOwned;

    Cube() {
        value = 1;
        isOwned = false;
    }

    @Override
    public int getValue() {
        return value;
    }

    public void accept(Player newOwner) {
        value = value*2;
        owner=newOwner;
        isOwned=true;
    }

    public Player getOwner() {
        return owner;
    }

    @Override
    public int getOwnerId() {
        return owner.getId();
    }

    @Override
    public boolean isOwned() {
        return isOwned;
    }

    public void reset() {
        value = 1;
        isOwned = false;
    }

    public String toString() {
        if (value == 1) {
            return "64";
        } else {
            return Integer.toString(value);
        }
    }
}
