package mywhiteboardapp;

import java.awt.*;
import javax.swing.*;

/**
 * this is the mainDisplay onto which each of the panels (sections) are added
 * and displayed.
 *
 * @author lakshhkhatri
 */
public class MainDisplay extends JPanel {

    // to store the images of each of the panels
    Image[][] images;

    // to check whether a client has connected to server
    boolean connected;

    int numberOfRows = 7; // start from 0
    int numberOfColumns = 7;

    public MainDisplay() {
        connected = false;
        setDoubleBuffered(false);
        setBackground(Color.WHITE);
    }

    // this method is called from 'TestDraw' whenever the images are updates
    public void send(Image[][] images) {
        this.images = images;

        // repaint the Jpanel since there have been changes to the images
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {

        // if a client has connected, then execute the code below. otherwise, an error would occur as the images would be 'null'.
        // this boolean is set to false in the constructor, but is set to true from the 'testDraw' class when a client connects.
        if (connected) {
            // draw the images for each of the panels in their appropriate location
            /* g.drawImage(images[0][0], 0, 0, 250, 150, null);
            g.drawImage(images[0][1], 250, 0, 250, 150, null);
            g.drawImage(images[0][2], 500, 0, 250, 150, null);
            g.drawImage(images[0][3], 750, 0, 250, 150, null);
            
            g.drawImage(images[1][0], 0, 150, 250, 150, null);
            g.drawImage(images[1][1], 250, 150, 250, 150, null);
            g.drawImage(images[1][2], 500, 150, 250, 150, null);
            g.drawImage(images[1][3], 750, 150, 250, 150, null);
            
            g.drawImage(images[2][0], 0, 300, 250, 150, null);
            g.drawImage(images[2][1], 250, 300, 250, 150, null);
            g.drawImage(images[2][2], 500, 300, 250, 150, null);
            g.drawImage(images[2][3], 750, 300, 250, 150, null);
            
            g.drawImage(images[3][0], 0, 450, 250, 150, null);
            g.drawImage(images[3][1], 250, 450, 250, 150, null);
            g.drawImage(images[3][2], 500, 450, 250, 150, null);
            g.drawImage(images[3][3], 750, 450, 250, 150, null);
            }*/

            int width = 125;
            int height = 75; // scale from 500 300

            for (int row = 0; row <= numberOfRows; row++) {
                for (int col = 0; col <= numberOfColumns; col++) {
                    g.drawImage(images[row][col], col * width, row * height, width, height, null);
                }
            }

        }

    }
}
