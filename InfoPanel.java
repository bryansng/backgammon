import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

/**
 * Backgammon solution by @author Chris for Sprint 5.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class InfoPanel extends JPanel implements InfoPanelAPI {

    private static final long serialVersionUID = 1L;
    private static final int TEXT_AREA_HEIGHT = 40;
    private static final int CHARACTER_WIDTH = 47;
    private static final int FONT_SIZE = 12;

    private final JTextArea textArea;
    private String allInfo = "", latestInfo = "";

    InfoPanel() {
        textArea  = new JTextArea(TEXT_AREA_HEIGHT, CHARACTER_WIDTH);
        JScrollPane scrollPane = new JScrollPane(textArea);
        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        textArea.setEditable(false);
        textArea.setFont(new Font("monospaced", Font.PLAIN, FONT_SIZE));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addText(String text) {
        textArea.setText(textArea.getText()+"\n"+text);
        allInfo = allInfo + text;
        latestInfo = latestInfo + text;
    }

    public void clear() {
        textArea.setText("");
    }

    @Override
    public String getAllInfo() {
        return allInfo;
    }

    @Override
    public String getLatestInfo() {
        String text = latestInfo;
        latestInfo = "";
        return text;
    }

}
