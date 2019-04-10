package bot;

import botAPI.Plays;

public interface BotAPI {
    String getName();

    String getCommand(Plays possiblePlays);

    String getDoubleDecision();
}
