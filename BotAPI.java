import java.util.ArrayList;

public interface BotAPI {
    String getName();

    String getCommand(Plays possiblePlays);

    String getDoubleDecision();
    
    // REMEMBER TO REMOVE THIS.
    ArrayList<Double> getWeights();
    void setWeights(ArrayList<Double> newWeights);
}
