package mywhiteboardapp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.List;
import java.awt.Rectangle;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * this is the server class, which receives data from the client so that the
 * same lines can be drawn on the main display
 *
 * @author lakshhkhatri
 */
public class Server implements Runnable {

    // serverSocket is used as we are running on a local network
    static ServerSocket serverSocket;

    // socket for establishing connection with the client
    static Socket socket;

    // buffered reader is used for reading/receiving data from client. Server doesn't have to send data since this application requires one-way communication
    BufferedReader in;

    // used for checking if there is something that has to be drawn
    boolean draw;

    // this is the JComponent onto which we will draw
    TestDraw drawOutput;

    // different brush stroke sizes
    final static BasicStroke WIDE = new BasicStroke(6.0f);
    final static BasicStroke SMALL = new BasicStroke(2.0f);

    // used to check whether the panel has been changed
    int oldPanelRow;
    int oldPanelCol;

    int numberOfRows = 7; // start from 0
    int numberOfColumns = 7;

    public Server(TestDraw drawOutput) {
        this.drawOutput = drawOutput;

        // instantiate to any value that doesn't correspond to a panel. so, anything greater than 4 for both variables.
        oldPanelRow = 99;
        oldPanelCol = 99;
    }

    @Override
    public void run() {
        try {

            // create a local server and listen for connections on the given port number (7777)
            serverSocket = new ServerSocket(7777);
            socket = serverSocket.accept();

            // when client connects
            drawOutput.connected = true;

            // get the input stream so that data can be read
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // initialize to false. will turn true when data is sent to server.
            draw = false;

            // continuously listen for incoming data
            while (true) {
                // read the line in the input stream
                String message = in.readLine();

                // if the line had text, run the draw command
                if (message != null) {
                    drawOnImage(message);
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    void drawOnImage(String message) {
        // data is sent from the client in the following order
        // PANELrow PANELcol LineColor pointOneX pointOneY pointTwoX pointTwoY command
        // thus, we use the inbuilt split method to get each value.
        String[] splitted = message.split(" ");

        // convert the strings to integer. PrintWriter, which is used in the client side for writing to the socket, only writes strings.
        int currentPanelRow = Integer.parseInt(splitted[0]);
        int currentPanelCol = Integer.parseInt(splitted[1]);
        int colorCode = Integer.parseInt(splitted[2]);

        int pointOneX = Integer.parseInt(splitted[3]);
        int pointOneY = Integer.parseInt(splitted[4]);
        int pointTwoX = Integer.parseInt(splitted[5]);
        int pointTwoY = Integer.parseInt(splitted[6]);

        int command = Integer.parseInt(splitted[7]);

        // save current panel
        drawOutput.currentPanelRow = currentPanelRow;
        drawOutput.currentPanelCol = currentPanelCol;

        // save current color based on the colorCode sent from the server
        Color currentColor;

        switch (colorCode) {
            case 1:
                currentColor = Color.RED;
                break;
            case 2:
                currentColor = Color.MAGENTA;
                break;
            case 3:
                currentColor = Color.GREEN;
                break;
            case 4:
                currentColor = Color.BLUE;
                break;
            default:
                currentColor = Color.BLACK;
        }

        // look at the command sent from the client. 
        // the command is to indicate which step the server has to take:
        // 1 = change panel. 0 = draw. 9 = clearCurPanel. 99 = clearAllPanels
        switch (command) {
            case 9:
                // clear current pannel by painting a white (color of background) rectangle all over the display
                drawOutput.graphics[currentPanelRow][currentPanelCol].setPaint(Color.white);
                drawOutput.graphics[currentPanelRow][currentPanelCol].fillRect(0, 0, 500, 300);
                drawOutput.graphics[currentPanelRow][currentPanelCol].setPaint(currentColor);

                drawOutput.graphics[currentPanelRow][currentPanelCol].setStroke(WIDE);
                drawOutput.graphics[currentPanelRow][currentPanelCol].setColor(Color.GRAY);
                drawOutput.graphics[currentPanelRow][currentPanelCol].draw(new Rectangle(0, 0, 500, 300));

                break;
            case 99:
                // go through each panel and clear each one of them by painting a white rectangle all over the display.
                for (int row = 0; row <= numberOfRows; row++) {
                    for (int col = 0; col <= numberOfColumns; col++) {
                        drawOutput.graphics[row][col].setPaint(Color.white);
                        drawOutput.graphics[row][col].fillRect(0, 0, 500, 300);
                        drawOutput.graphics[row][col].setPaint(currentColor);

                        drawOutput.graphics[currentPanelRow][currentPanelCol].setStroke(WIDE);
                        drawOutput.graphics[currentPanelRow][currentPanelCol].setColor(Color.GRAY);
                        drawOutput.graphics[currentPanelRow][currentPanelCol].draw(new Rectangle(0, 0, 500, 300));
                    }
                }
                break;
            case 0:
                // draw a line with the given coordinates (Sent from client)
                drawOutput.graphics[currentPanelRow][currentPanelCol].setStroke(WIDE);
                drawOutput.graphics[currentPanelRow][currentPanelCol].setColor(currentColor);
                drawOutput.graphics[currentPanelRow][currentPanelCol].drawLine(pointOneX, pointOneY, pointTwoX, pointTwoY);
                break;
            default:
                // if none of the above commands were activiated, 
                // it means command was equal to 1, and thus, a panel shift should occur.
                // this is already taken care of since we updated the currentPanelRow and currentPanelCol variables above
                break;
        }

        // to check whether the panel has been changed
        boolean same = oldPanelRow == currentPanelRow && oldPanelCol == currentPanelCol;

        if (same == false) {
            // if panel has been changed, draw a gray outline around the panel so that the user knows which panel he/she is currently on.
            drawOutput.graphics[currentPanelRow][currentPanelCol].setStroke(WIDE);
            drawOutput.graphics[currentPanelRow][currentPanelCol].setColor(Color.GRAY);
            drawOutput.graphics[currentPanelRow][currentPanelCol].draw(new Rectangle(0, 0, 500, 300));

            // draw a white outline around all other panels so that only one panel is outlined gray since you can only be on one panel at a time.
            // if this isn't done, the gray outline isn't removed from previously visited panels, and is thus visible on all panels that have been visited.
            for (int row = 0; row <= numberOfRows; row++) {
                for (int col = 0; col <= numberOfColumns; col++) {
                    boolean sama = row == currentPanelRow && col == currentPanelCol;
                    if (sama == false) {
                        drawOutput.graphics[row][col].setStroke(WIDE);
                        drawOutput.graphics[row][col].setColor(Color.WHITE);
                        for (int i = 0; i < 6; i++) {
                            drawOutput.graphics[row][col].draw(new Rectangle(0, 0, 500, 300));
                        }
                    }
                }
            }
        }

        // update the image since changes have been made (line has been drawn / color has been changed / panel has been changed ...)
        drawOutput.repaint();
        oldPanelRow = currentPanelRow;
        oldPanelCol = currentPanelCol;

    }
}
