import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

/**
 * Backgammon solution by @author Chris for Sprint 5.
 * 
 * @teamname TeaCup
 * @author Bryan Sng, 17205050
 * @author @LxEmily, 17200573
 * @author Braddy Yeoh, 17357376
 *
 */
public class CommandPanel extends JPanel  {

    private static final long serialVersionUID = 1L;
    private static final int FONT_SIZE = 14;

    private final JTextField commandField;
    private final LinkedList<String> commandBuffer;

    CommandPanel() {
        commandField = new JTextField();
        commandBuffer = new LinkedList<>();
        class AddActionListener implements ActionListener {
            public void actionPerformed(ActionEvent event)	{
                synchronized (commandBuffer) {
                    commandBuffer.add(commandField.getText());
                    commandField.setText("");
                    commandBuffer.notify();
                }
            }
        }
        ActionListener listener = new AddActionListener();
        commandField.addActionListener(listener);
        commandField.setFont(new Font("Times New Roman", Font.PLAIN, FONT_SIZE));
        setLayout(new BorderLayout());
        add(commandField, BorderLayout.CENTER);
    }

    public String getString() {
        String command;
        synchronized(commandBuffer) {
            while (commandBuffer.isEmpty()) {
                try {
                    commandBuffer.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            command = commandBuffer.pop();
        }
        return command;
    }

}
