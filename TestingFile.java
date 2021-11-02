package mywhiteboardapp;

import apple.laf.JRSUIUtils;
import java.awt.Color;
import java.awt.Image;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JComponent;

public class TestingFile {

    public static void main(String[] args) {
        ImageArrays file = new ImageArrays();

        //file.createImage();
        
        //System.out.println("NAME: " + Color.BLACK);
        
        Color curCol = Color.green;
        String currentColor = "";
        currentColor += curCol;
        System.out.println(currentColor);
    }
}

class ImageArrays extends JComponent {

    Image[][] images = {
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null}
    };

    public void createImage() {

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (images[row][col] == null) {
                    System.out.println(row + "," + col);
                }
            }
        }

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                Image image;
                image = createImage(getSize().width, getSize().height);
                images[row][col] = image;
            }
        }
        
        System.out.println("0000----000");

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (images[row][col] != null) {
                    System.out.println(row + "," + col);
                }
            }
        }
    }
}
