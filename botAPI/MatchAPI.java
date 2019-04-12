package botAPI;

import game_engine.Player;

public interface MatchAPI {
    int getLength();
    
    boolean canDouble(Player player);
}
