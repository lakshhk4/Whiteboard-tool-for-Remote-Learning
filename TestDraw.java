package mywhiteboardapp;

import java.awt.*;
import javax.swing.*;

/**
 * this is exactly like the 'DrawArea' class except that it is on the output
 * device.
 *
 * @author lakshhkhatri
 */
public class TestDraw extends JPanel {

    // the main display in which each of the individual panels is displayed on
    MainDisplay mainDisplayer;

    // to store current panel
    int currentPanelRow;
    int currentPanelCol;

    int numberOfRows = 7; // start from 0
    int numberOfColumns = 7;

    final static BasicStroke WIDE = new BasicStroke(6.0f);
    final static BasicStroke SMALL = new BasicStroke(2.0f);
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

    // images for each panel
    /* Image[][] images = {
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            };
            
    // graphics to edit the images of each panel
    Graphics2D[][] graphics = {
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null}
    };*/
    // to check whether the client is connected to the server
    boolean connected = false;

    public TestDraw(MainDisplay mainDisplayer) {
        setDoubleBuffered(false);
        this.mainDisplayer = mainDisplayer;

        // start on the top left panel
        currentPanelRow = 0;
        currentPanelCol = 0;
    }

    @Override
    public void paintComponent(Graphics g) {

        if (images[currentPanelRow][currentPanelCol] == null) {
            // create the image and graphics for each panel

            for (int row = 0; row <= numberOfRows; row++) {
                for (int col = 0; col <= numberOfColumns; col++) {
                    images[row][col] = createImage(getSize().width, getSize().height);
                    graphics[row][col] = (Graphics2D) images[row][col].getGraphics();
                    graphics[row][col].setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // make background white by filling in a white rectange on the entire display of each image.
                    graphics[row][col].setPaint(Color.white);
                    graphics[row][col].fillRect(0, 0, getSize().width, getSize().height);
                    graphics[row][col].setPaint(Color.black);
                }
            }

        }
        // the mainDisplayer will run its code only when a client has connected.
        if (connected) {
            mainDisplayer.connected = true;
        }

        // draw the image of the current panel
        g.drawImage(images[currentPanelRow][currentPanelCol], 0, 0, null);

        // send this image to the mainDisplay
        mainDisplayer.send(images);

    }

}
