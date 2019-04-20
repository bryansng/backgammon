import java.awt.*;

public class Player implements PlayerAPI {
    // Player holds the details for one player

    private int id;
    private String colorName;
    private Color color;
    private String name;
    private Dice dice;
    private int score;

    Player(int id, String colorName, Color color) {
        this.id = id;
        name = "";
        this.colorName = colorName;
        this.color = color;
        dice = new Dice();
        score = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getColorName() {
        return this.colorName;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    public Dice getDice() { return dice; }

    public String toString() {
        return name;
    }

    public void addPoints(int points) {
        score = score + points;
    }

    @Override
    public int getScore() {
        return score;
    }

    public boolean equals(Player player) {
        return this.id == player.id;
    }

    public void reset() {
        name = "";
        score = 0;
    }
}
