import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

class   BoardPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int FRAME_WIDTH = 752, FRAME_HEIGHT = 552;  // must be multiples of 4
    private static final int BORDER_TOP = 40, BORDER_BOTTOM = 75, BORDER_LEFT = 66, BORDER_RIGHT = 60;
    private static final int PIP_WIDTH = 47, BAR_WIDTH = 66;
    private static final int CHECKER_RADIUS = 16, CHECKER_DEPTH = 8, LINE_WIDTH = 2;   // must be even
    private static final int CUBE_HEIGHT = 40, CUBE_WIDTH=40;

    private Color[] checkerColors;
    private Board board;
    private Players players;
    private BufferedImage boardImage;
    private Graphics2D g2;
    private Cube cube;
    private Match match;

    BoardPanel(Board board, Players players, Cube cube, Match match) {
        this.board = board;
        this.players = players;
        this.cube = cube;
        this.match = match;
        checkerColors = new Color[Players.NUM_PLAYERS];
        checkerColors[0] = players.get(0).getColor();
        checkerColors[1] = players.get(1).getColor();
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        setBackground(Color.YELLOW);
        try {
            boardImage = ImageIO.read(this.getClass().getResource("board.jpg"));
        } catch (IOException ex) {
            System.out.println("Could not find the image file " + ex.toString());
        }
    }

    private void displayChecker(int player, int x, int y) {
        g2.setColor(Color.BLACK);
        Ellipse2D.Double ellipseBlack = new Ellipse2D.Double(x,y,2*CHECKER_RADIUS,2*CHECKER_RADIUS);
        g2.fill(ellipseBlack);
        Ellipse2D.Double ellipseColour = new Ellipse2D.Double(x+LINE_WIDTH,y+LINE_WIDTH,2*(CHECKER_RADIUS-LINE_WIDTH),2*(CHECKER_RADIUS-LINE_WIDTH));
        g2.setColor(checkerColors[player]);
        g2.fill(ellipseColour);
    }

    private void displayCheckerSide(int player, int x, int y) {
        g2.setColor(Color.BLACK);
        Rectangle2D.Double rectangleBlack = new Rectangle2D.Double(x,y,2*CHECKER_RADIUS,CHECKER_DEPTH);
        g2.fill(rectangleBlack);
        Rectangle2D.Double rectangleColour = new Rectangle2D.Double(x+LINE_WIDTH,y+LINE_WIDTH,2*(CHECKER_RADIUS-LINE_WIDTH),CHECKER_DEPTH-2*LINE_WIDTH);
        g2.setColor(checkerColors[player]);
        g2.fill(rectangleColour);
    }

    private void displayCube() {
        int x = 3,y;
        if (!cube.isOwned()) {
            y = FRAME_HEIGHT/2-15;
        } else if (cube.getOwner().getId()==0) {
            y = 3*(FRAME_HEIGHT)/4-10;
        } else {
            y = FRAME_HEIGHT/4-15;
        }
        Rectangle2D.Double rectangleBlack = new Rectangle2D.Double(x,y,CUBE_HEIGHT,CUBE_WIDTH);
        g2.setColor(Color.WHITE);
        g2.fill(rectangleBlack);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Courier",Font.BOLD,32));
        g2.drawString("" + cube, x+2, y+32);
    }

    private void displayScore() {
        g2.setFont(new Font("Courier",Font.BOLD,32));
        g2.setColor(players.get(0).getColor());
        g2.drawString("" + players.get(0).getScore(), FRAME_WIDTH/4, FRAME_HEIGHT/2+10);
        g2.setColor(players.get(1).getColor());
        g2.drawString("" + players.get(1).getScore(), 3*FRAME_WIDTH/4, FRAME_HEIGHT/2+10);
        g2.setColor(Color.WHITE);
        g2.drawString("" + match.getLength(), FRAME_WIDTH-40, FRAME_HEIGHT/2+10);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2 =(Graphics2D) g;
        g2.drawImage(boardImage, 0, 0, FRAME_WIDTH, FRAME_HEIGHT, this);
        for (int player = 0; player< Backgammon.NUM_PLAYERS; player++) {
            int x,y;
            // Display Pip Numbers
            for (int pip = 1; pip<= Board.NUM_PIPS; pip++) {
                if (pip>3* Board.NUM_PIPS/4) {
                    x = FRAME_WIDTH/2 + BAR_WIDTH/2 + (pip-3* Board.NUM_PIPS/4-1)*PIP_WIDTH+PIP_WIDTH/4;
                } else if (pip> Board.NUM_PIPS/2) {
                    x = BORDER_LEFT + (pip- Board.NUM_PIPS/2-1)*PIP_WIDTH+PIP_WIDTH/4;
                } else if (pip> Board.NUM_PIPS/4) {
                    x = BORDER_LEFT + (Board.NUM_PIPS/2-pip)*PIP_WIDTH+PIP_WIDTH/4;
                } else {
                    x = FRAME_WIDTH/2 + BAR_WIDTH/2 + (Board.NUM_PIPS/4-pip)*PIP_WIDTH+PIP_WIDTH/4;
                }
                if (pip> Board.NUM_PIPS/2) {
                    y = 3*BORDER_TOP/4;
                } else {
                    y = FRAME_HEIGHT-BORDER_BOTTOM/4;
                }
                g2.setColor(players.getCurrent().getColor());
                g2.setFont(new Font("Courier",Font.BOLD,16));
                if (players.getCurrent().getId()==0) {
                    g2.drawString(Integer.toString(pip), x, y);
                } else {
                    g2.drawString(Integer.toString(Board.NUM_PIPS-pip+1), x, y);
                }
            }
            // Display Bar
            for (int count = 1; count<=board.getNumCheckers(player, Board.BAR); count++) {
                x = FRAME_WIDTH/2-CHECKER_RADIUS;
                if (player==0) {
                    y = FRAME_HEIGHT/4+(count-1)*CHECKER_RADIUS;
                } else {
                    y = 3*FRAME_HEIGHT/4-(count-1)*CHECKER_RADIUS;
                }
                displayChecker(player,x,y);
            }
            // Display Main Board
            for (int pip = 1; pip<= Board.NUM_PIPS; pip++) {
                for (int count=1; count<=board.getNumCheckers(player,pip); count++) {
                    if (pip>3* Board.NUM_PIPS/4) {
                        x = FRAME_WIDTH/2 + BAR_WIDTH/2 + (pip-3* Board.NUM_PIPS/4-1)*PIP_WIDTH;
                    } else if (pip> Board.NUM_PIPS/2) {
                        x = BORDER_LEFT + (pip- Board.NUM_PIPS/2-1)*PIP_WIDTH;
                    } else if (pip> Board.NUM_PIPS/4) {
                        x = BORDER_LEFT + (Board.NUM_PIPS/2-pip)*PIP_WIDTH;
                    } else {
                        x = FRAME_WIDTH/2 + BAR_WIDTH/2 + (Board.NUM_PIPS/4-pip)*PIP_WIDTH;
                    }
                    if ( (player==0 && pip> Board.NUM_PIPS/2) || (player==1 && pip< Board.NUM_PIPS/2) ){
                        y = BORDER_TOP + (count-1)*2*CHECKER_RADIUS;
                    } else {
                        y = FRAME_HEIGHT - BORDER_BOTTOM - (count-1)*2*CHECKER_RADIUS;
                    }
                    displayChecker(player,x,y);
                }
            }
            // Display Bear Off
            for (int count = 1; count<=board.getNumCheckers(player, Board.BEAR_OFF); count++) {
                x = FRAME_WIDTH - BORDER_RIGHT/2 - CHECKER_RADIUS;
                if (player==0) {
                    y = FRAME_HEIGHT - BORDER_BOTTOM - (count-1)*CHECKER_DEPTH;
                } else {
                    y = BORDER_TOP + (count-1)*CHECKER_DEPTH;
                }
                displayCheckerSide(player,x,y);
            }

        }
        displayCube();
        displayScore();
    }

    public void refresh() {
        revalidate();
        repaint();
    }

}
