package mywhiteboardapp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * this file creates the GUI of the input device
 *
 * @author lakshhkhatri
 */
public class SwingPaint {

    // controls that will be displayed on input end
    JButton clearBtn, leftBtn, rightBtn, upBtn, downBtn, clearAllBtn;
    DrawArea drawArea;
    JComboBox colorOptions;
    JToggleButton autoPanelMove;

    ActionListener actionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == clearBtn) {
                // clear current panel
                drawArea.clear(9);
            } else if (e.getSource() == clearAllBtn) {
                // clear all panels
                drawArea.clear(99);
            } else if (e.getSource() == autoPanelMove) {
                //auto Panel Movement Detection turned on
                if (autoPanelMove.isSelected()) {
                    autoPanelMove.setText("ON");
                    drawArea.autoMove(true);

                } else {
                    //auto Panel Movement Detection turned on
                    autoPanelMove.setText("OFF");
                    drawArea.autoMove(false);
                }
            } else if (e.getSource() == leftBtn) {
                // move to left panel
                drawArea.left();
            } else if (e.getSource() == rightBtn) {
                // move to right panel
                drawArea.right();
            } else if (e.getSource() == upBtn) {
                // move to panel above
                drawArea.up();
            } else if (e.getSource() == downBtn) {
                // move to the panel below
                drawArea.down();
            } else if (e.getSource() == colorOptions) {
                // change painting color 
                String colorChosen = (String) colorOptions.getSelectedItem();

                switch (colorChosen) {
                    case "Red":
                        drawArea.red();
                        break;
                    case "Blue":
                        drawArea.blue();
                        break;
                    case "Green":
                        drawArea.green();
                        break;
                    case "Magenta":
                        drawArea.magenta();
                        break;
                    default:
                        drawArea.black();

                }

            }
        }
    };

    public void show() throws IOException {

        // Main Frame
        JFrame frame = new JFrame("Swing Paint");

        // Container for main frame components
        Container content = frame.getContentPane();
        content.setLayout(new BorderLayout());

        // create and add input draw area
        drawArea = new DrawArea();
        content.add(drawArea, BorderLayout.CENTER);

        // top controls 
        JPanel controls = new JPanel();

        // clear button
        clearBtn = new JButton("Clear this panel");
        clearBtn.addActionListener(actionListener);

        // clear all button (all panels)
        clearAllBtn = new JButton("Clear all panels");
        clearAllBtn.addActionListener(actionListener);

        // left, right, up, down move panel buttons
        leftBtn = new JButton("Left");
        leftBtn.addActionListener(actionListener);
        rightBtn = new JButton("Right");
        rightBtn.addActionListener(actionListener);
        upBtn = new JButton("Up");
        upBtn.addActionListener(actionListener);
        downBtn = new JButton("Down");
        downBtn.addActionListener(actionListener);

        // autoMovePanel detection toggle button. ON by default.
        autoPanelMove = new JToggleButton("ON");
        autoPanelMove.doClick();
        drawArea.autoMove(true);
        autoPanelMove.addActionListener(actionListener);

        // different color options
        String[] colorsInCmb = {"Black", "Green", "Blue", "Red", "Magenta"};
        colorOptions = new JComboBox(colorsInCmb);
        colorOptions.addActionListener(actionListener);

        // some controls will be added in the bottom.
        JPanel rightControls = new JPanel();
        rightControls.add(colorOptions);
        rightControls.add(clearBtn);
        rightControls.add(clearAllBtn);

        // some controls on top.
        JLabel label0 = new JLabel();
        label0.setText("Move Panel ");
        controls.add(label0);
        controls.add(leftBtn);
        controls.add(rightBtn);
        controls.add(upBtn);
        controls.add(downBtn);
        JLabel label1 = new JLabel();
        label1.setText("AutoPanelMove");
        JLabel space = new JLabel();
        space.setText("      ");
        controls.add(space);
        controls.add(label1);
        controls.add(autoPanelMove);

        // add controls to content area of jFrame
        content.add(controls, BorderLayout.NORTH);
        content.add(rightControls, BorderLayout.SOUTH);

        // frame size. drawing area is 350. but frameSize is 395 to accomodate control panels.
        frame.setSize(500, 395);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
    }

}
