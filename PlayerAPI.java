import java.awt.*;

/**
 * Backgammon PlayerAPI for bots by @author Chris for Sprint 5.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public interface PlayerAPI {

    int getId();

    String getColorName();

    Color getColor();

    int getScore();
}
