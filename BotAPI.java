import java.util.ArrayList;

/**
 * Backgammon BotAPI by @author Chris for Sprint 5.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public interface BotAPI {
    String getName();

    String getCommand(Plays possiblePlays);

    String getDoubleDecision();
    
    // REMEMBER TO REMOVE THIS.
    ArrayList<Double> getWeights();
    void setWeights(ArrayList<Double> newWeights);
}
