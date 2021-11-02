package mywhiteboardapp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;

/**
 * this draw area is displayed on the input side's GUI. this is what the user
 * will paint on.
 *
 * @author lakshhkhatri
 */
public class DrawArea extends JComponent {

    // Mouse coordinates
    private int currentX, currentY, oldX, oldY;

    // variable used to check whether data should be sent.
    int toSend;

    // socket to connect with server
    Socket socket;

    // output channel of the one-way server.
    PrintWriter out;

    // painting tool stroke size
    final static BasicStroke WIDESTROKE = new BasicStroke(6.0f);

    // images that will be drawn on
    /*    Image[][] images = {
    {null, null, null, null},
    {null, null, null, null},
    {null, null, null, null},
    {null, null, null, null}
    };
    
    // graphics used to draw on images
    Graphics2D[][] graphics = {
    {null, null, null, null},
    {null, null, null, null},
    {null, null, null, null},
    {null, null, null, null}
    };*/
    Image[][] images = {
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},};

    Graphics2D[][] graphics = {
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null},};
    // to store which is the current pannel
    int currentPannelRow;
    int currentPannelCol;

    // storing current color.
    Color currentColor;
    // stores a 'code' for the currentColor which can be deciphered by the server so that the same colors are displayed on both ends.
    int colorCode;

    int numberOfRows = 7; // start from 0
    int numberOfColumns = 7;

    // detection system for auto-panel movement
    double secSinceLatestLineDrawn = 0;
    boolean beyondPanelMVmargin = false;
    // autoPanelMovement is set ON by default
    boolean autoMovePanel = true;
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            // every 0.1 seconds this method is called, and thereby, we have a timer since we increment this variable.
            secSinceLatestLineDrawn += 0.1;

            // auto-panel movement detection
            if (autoMovePanel) {
                if (beyondPanelMVmargin && secSinceLatestLineDrawn >= 1) {
                    // move panel detected
                    boolean lastPanel = currentPannelRow == numberOfRows && currentPannelCol == numberOfColumns;

                    // move to right panel if possible. otherwise, go to next row.
                    if (lastPanel == false) {
                        boolean rightMostPanel = currentPannelCol == numberOfColumns;

                        if (rightMostPanel) {
                            currentPannelRow = currentPannelRow + 1;
                            currentPannelCol = 0;
                            repaint();
                            sendData(true, toSend);

                        } else {
                            right();
                            repaint();
                            //sendData(true,90);
                        }
                    }

                    beyondPanelMVmargin = false;
                }
            }
        }

    };

    public DrawArea() throws IOException {
        setDoubleBuffered(false);

        // increments the timer
        timer.scheduleAtFixedRate(task, 100, 100);

        // start with top left panel
        currentPannelRow = 0;
        currentPannelCol = 0;

        // default color is blacl
        currentColor = Color.BLACK;

        // connecting to correct port of the localserver and getting output stream. OUTPUT device should be open before this input device.
        socket = new Socket("localhost", 7777);
        out = new PrintWriter(socket.getOutputStream(), true);

        // store mouse coordinates
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // save coordinates of mouse when it's pressed
                oldX = e.getX();
                oldY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // new coordinates when the mouse is dragged
                currentX = e.getX();
                currentY = e.getY();

                if (graphics[currentPannelRow][currentPannelCol] != null) {

                    // set stroke and color, then, draw the line on current image through the current graphics object.
                    graphics[currentPannelRow][currentPannelCol].setStroke(WIDESTROKE);
                    graphics[currentPannelRow][currentPannelCol].setColor(currentColor);
                    graphics[currentPannelRow][currentPannelCol].drawLine(oldX, oldY, currentX, currentY);

                    // activate function to send data to server.
                    sendData(false, 90);

                    // stores the number of seconds since the latest line has been drawn. used for autoPanelMovementDetection
                    secSinceLatestLineDrawn = 0;

                    // margin for autoPanelMovement detection
                    if (currentX > 420 || oldX > 420) {
                        beyondPanelMVmargin = true;
                    }

                    // update the image.
                    repaint();

                    // store current coordinates as old.
                    oldX = currentX;
                    oldY = currentY;
                }

            }
        });
    }

    // method to send data to server
    public void sendData(boolean panelChanged, int colC) {

        // stores current color
        Color curCol = graphics[currentPannelRow][currentPannelCol].getColor();
        String currentColor = "";
        currentColor += curCol;

        // creates a 'coded' version of the color so that it can be decipher on the server's send
        switch (currentColor) {
            case "java.awt.Color[r=255,g=0,b=0]":
                //red
                colorCode = 1;
                break;
            case "java.awt.Color[r=255,g=0,b=255]":
                // magenta
                colorCode = 2;
                break;
            case "java.awt.Color[r=0,g=255,b=0]":
                // green
                colorCode = 3;
                break;
            case "java.awt.Color[r=0,g=0,b=255]":
                // blue
                colorCode = 4;
                break;
            default:
                // black
                colorCode = 0;
        }

        // data is sent to server in the following order
        // PANELrow PANELcol LineColor pointOneX pointOneY pointTwoX pointTwoY command
        // the command is to indicate which step the server has to take: // 1 = change panel. 0 = draw. 9 = clearCurPanel. 99 = clearAllPanels
        if (panelChanged) {
            // if panel is being changed, no need to draw a line; thus, coordinates sent are 0,0,0,0. But this is irrelevant because the server will read the command and if it is to change panel, no line will be drawn anyway.
            // some coordinates need to be send, although random, so that there wont be an 'out of bounds exception'
            out.println(currentPannelRow + " " + currentPannelCol + " " + colC + " " + 0 + " " + 0 + " " + 0 + " " + 0 + " " + 1);
        } else {
            // data sent when a line is to be drawn.
            out.println(currentPannelRow + " " + currentPannelCol + " " + colorCode + " " + oldX + " " + oldY + " " + currentX + " " + currentY + " " + 0);
        }

    }

    // most of tthe code in this method isn't mine, and has been take from an oracle web tutorial. Link: 
    @Override
    protected void paintComponent(Graphics g) {
        if (images[currentPannelRow][currentPannelCol] == null) {
            // instantiate all images and graphcis
            images[currentPannelRow][currentPannelCol] = createImage(getSize().width, getSize().height);
            graphics[currentPannelRow][currentPannelCol] = (Graphics2D) images[currentPannelRow][currentPannelCol].getGraphics();
            graphics[currentPannelRow][currentPannelCol].setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int row = 0; row <= numberOfRows; row++) {
                for (int col = 0; col <= numberOfColumns; col++) {
                    images[row][col] = createImage(getSize().width, getSize().height);
                    graphics[row][col] = (Graphics2D) images[row][col].getGraphics();

                    graphics[row][col].setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // clear draw area by painting white on entire screen
                    graphics[row][col].setPaint(Color.white);
                    graphics[row][col].fillRect(0, 0, getSize().width, getSize().height);
                    graphics[row][col].setPaint(currentColor);
                }
            }
        }
        // draw on current image
        g.drawImage(images[currentPannelRow][currentPannelCol], 0, 0, null);
    }

    // method used to clear current/all panel(s)
    public void clear(int command) {
        // when command = 9, only the current panel is to be erased. otherwise, erase all panels.
        if (command == 9) {
            graphics[currentPannelRow][currentPannelCol].setPaint(Color.white);
            // draw white on entire draw area to clear
            graphics[currentPannelRow][currentPannelCol].fillRect(0, 0, getSize().width, getSize().height);
            graphics[currentPannelRow][currentPannelCol].setPaint(currentColor);

        } else {
            for (int row = 0; row <= numberOfRows; row++) {
                for (int col = 0; col <= numberOfColumns; col++) {
                    if (graphics[row][col] != null) {
                        graphics[row][col].setPaint(Color.white);
                        // draw white on entire draw area to clear
                        graphics[row][col].fillRect(0, 0, 500, 300);
                        graphics[row][col].setPaint(currentColor);
                    }
                }
            }
        }

        // activate repaint
        repaint();
        // send data to client, with current command (either 9 or 99)
        out.println(currentPannelRow + " " + currentPannelCol + " " + colorCode + " " + oldX + " " + oldY + " " + currentX + " " + currentY + " " + command);
    }

    // change drawing colors of currentPanel
    public void red() {

        graphics[currentPannelRow][currentPannelCol].setPaint(Color.RED);
        currentColor = Color.RED;
    }

    public void black() {
        graphics[currentPannelRow][currentPannelCol].setPaint(Color.BLACK);
        currentColor = Color.BLACK;
    }

    public void magenta() {
        graphics[currentPannelRow][currentPannelCol].setPaint(Color.MAGENTA);
        currentColor = Color.MAGENTA;
    }

    public void green() {
        graphics[currentPannelRow][currentPannelCol].setPaint(Color.GREEN);
        currentColor = Color.GREEN;
    }

    public void blue() {
        graphics[currentPannelRow][currentPannelCol].setPaint(Color.BLUE);
        currentColor = Color.BLUE;
    }

    // move to the panel on the left, if possible
    public void left() {
        String colC = Integer.toString(colorCode);
        toSend = Integer.parseInt(colC);

        if (currentPannelCol != 0) {
            currentPannelCol--;
            repaint();
            sendData(true, toSend);
        }

    }

    // move to the panel on the right, if possible
    public void right() {
        String colC = Integer.toString(colorCode);
        int toSend = Integer.parseInt(colC);

        if (currentPannelCol != numberOfColumns) {
            currentPannelCol++;
            repaint();
            sendData(true, toSend);
        }
    }

    // move to the panel above, if possible
    public void up() {
        String colC = Integer.toString(colorCode);
        int toSend = Integer.parseInt(colC);
        if (currentPannelRow != 0) {
            currentPannelRow--;
            repaint();
            sendData(true, toSend);
        }
    }

    // move to the panel below, if possible
    public void down() {
        String colC = Integer.toString(colorCode);
        int toSend = Integer.parseInt(colC);
        if (currentPannelRow != numberOfRows) {
            currentPannelRow++;
            repaint();
            sendData(true, toSend);
        }
    }

    // enable autoPanelMovement detection
    public void autoMove(boolean autoMovePanel) {
        this.autoMovePanel = autoMovePanel;
    }

}
