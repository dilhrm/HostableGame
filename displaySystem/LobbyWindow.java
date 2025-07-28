package displaySystem;
import constants.AvatarConstants;
import constants.GameConstants;
import constants.ImageConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
/**
 * [LobbyWindow.java]
 * This class is the GUI in which the user can choose the character and learn about the character
 * It loads each sprite as well as their icons.
 * @author Dilen De Silva
 * @version 1.0 January 22, 2024
 */

public class LobbyWindow extends JFrame implements GameConstants, ImageConstants {
    LobbyPanel lobby;
    JButton readyBtn, mapBtn;
    Picture[] icons;
    int selectedChar, permSelectedChar;
    Picture[] sprites;
    boolean ready, charConfirmed;

    /**
     * LobbyWindow
     * Constructor
     */
    public LobbyWindow(){
        super("TBD");
        this.setSize(new Dimension(GameConstants.windowSize));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ready = false;
        charConfirmed = false;

        selectedChar = -1;
        permSelectedChar = -1;

        lobby = new LobbyPanel();
        this.add(lobby);

        ImageIcon appIcon = new ImageIcon("images/smashBall.png");
        this.setIconImage(appIcon.getImage());

        this.setVisible(true);
    }

    public synchronized boolean isReady(){
        return this.ready;
    }

    private void setReady(boolean b){
        this.ready = b;
    }

    public synchronized boolean getCharConfirmed() {
        return charConfirmed;
    }
    private void setCharConfirmed(boolean b){
        this.charConfirmed = b;
    }
    public int getSelectedChar() {
        return selectedChar;
    }

    private void setSelectedChar(int selectedChar) {
        this.selectedChar = selectedChar;
    }

    public int getPermSelectedChar(){
        return permSelectedChar;
    }

    private void setPermSelectedChar(int ind){
        permSelectedChar = ind;
    }

    private class LobbyPanel extends JPanel {
        JPanel characterPanel, buttonPanel;
        JLabel charName, charLore;
        LobbyPanel() {
            this.setLayout(new BorderLayout());

            characterPanel = new JPanel(new FlowLayout());
            buttonPanel = new JPanel(new FlowLayout());

            charLore = new JLabel();
            characterPanel.add(charLore);

            this.setupListeners();
            this.setUpButtonPanel();
            this.loadPictures();
        }

        /**
         * paintComponent
         * method that allows us to draw what we want
         * @param g
         */
        @Override
        public void paintComponent(Graphics g) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Bodoni MT", 0, 40));
            this.drawCharacters(g);
            this.drawChosenCharacter(g);
        }

        /**
         * drawCharacters
         * @param g - graphics component of panel
         * draws the icon of each character
         */
        public void drawCharacters(Graphics g) {
            g.clearRect(0,0, 1080, 800);
            for (Picture icon: icons) {
                icon.draw(g);
            }
            if (permSelectedChar == 0) {
                g.drawRect(icons[0].x - 10, icons[0].y - 10, 120, 120);
            } else if (permSelectedChar == 1) {
                g.drawRect(icons[1].x - 10, icons[1].y - 10, 120, 120);
            } else if (permSelectedChar == 2) {
                g.drawRect(icons[2].x - 10, icons[2].y - 10, 120, 120);
            } else {
                if (selectedChar == 0) {
                    g.drawRect(icons[0].x - 10, icons[0].y - 10, 120, 120);
                } else if (selectedChar == 1) {
                    g.drawRect(icons[1].x - 10, icons[1].y - 10, 120, 120);
                } else if (selectedChar == 2) {
                    g.drawRect(icons[2].x - 10, icons[2].y - 10, 120, 120);
                } else {
                    if (selectedChar != -1) {
                        System.out.println(selectedChar);
                    }
                }
            }
        }

        /**
         * drawChosenCharacters
         * based on the current choice of the user, displays a larger sprite alongside their name and description
         * @param g
         */
        public void drawChosenCharacter(Graphics g){
            if(permSelectedChar == 0){
                sprites[0].draw(g);
                g.drawString("Norman - Average ", sprites[0].getX() + 200, sprites[1].getY());
            } else if(permSelectedChar == 1){
                sprites[1].draw(g);
                g.drawString("Titan - Strong ", sprites[1].getX() + 200, sprites[1].getY());
            } else if(permSelectedChar == 2){
                sprites[2].draw(g);
                g.drawString("Goblino - Swift ", sprites[2].getX() + 200, sprites[2].getY());
            }
        }

        /**
         * loadPictures
         * just retrieves pictures from file and places them in here
         */
        public void loadPictures() {
            icons = new Picture[3];

            icons[0] = new Picture(190, 200, 100, 100, ImageConstants.CHARACTER1_PATH);
            icons[1] = new Picture(350, 200, 100, 100, ImageConstants.CHARACTER2_PATH);
            icons[2] = new Picture(510, 200, 100, 100, ImageConstants.CHARACTER3_PATH);

            sprites = new Picture[3];
            sprites[0] = new Picture(440, 350, 100, 100, ImageConstants.CHARACTER1_PATH);
            sprites[0].resize(2);
            sprites[1] = new Picture(440, 350, 100, 100, ImageConstants.CHARACTER2_PATH);
            sprites[1].resize(2);
            sprites[2] = new Picture(440, 350, 100, 100, ImageConstants.CHARACTER3_PATH);
            sprites[2].resize(2);
        }

        /**
         * setUpButtonPanel
         * creates buttons for user to click Ready and Character Confirmed
         */
        public void setUpButtonPanel() {
            readyBtn = new JButton("Ready");
            readyBtn.setBackground(Color.GREEN);
            readyBtn.setFocusable(false);
            readyBtn.setPreferredSize(new Dimension(200, 100));
            readyBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(getCharConfirmed()) {
                        setReady(true);
                        repaint();
                    }
                }
            });

            mapBtn = new JButton("Confirm Character");
            mapBtn.setBackground(Color.LIGHT_GRAY);
            mapBtn.setFocusable(false);
            mapBtn.setPreferredSize(new Dimension(200, 100));
            mapBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(permSelectedChar != -1) {
                        setCharConfirmed(true);
                    }
                }
            });

            buttonPanel.add(readyBtn);
            buttonPanel.add(mapBtn);

            this.add(buttonPanel, BorderLayout.NORTH);
            repaint();
        }

        /**
         * setUpListeners
         * sets up mouse listeners in order to know what the user is doing
         */
        private void setupListeners() {
            this.addMouseMotionListener(new MouseAdapter() {
                @Override
                //used for dragging cities
                public void mouseMoved(MouseEvent e) {
                    boolean foundChar = false;
                    for (int index = 0; index < icons.length; index = index + 1) {
                        if (icons[index].isMouseInPicture(e.getPoint())) {
                            setSelectedChar(index);
                            foundChar = true;
                        }
                    }
                    if (!foundChar){
                        setSelectedChar(-1);
                    }
                    repaint();
                }
            });

            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    boolean somethingClicked = false;
                    for (int index = 0; index < icons.length; index = index + 1) {
                        if (icons[index].isMouseInPicture(e.getPoint())) {
                            permSelectedChar = index;
                            somethingClicked = true;
                            repaint();
                        }
                    }
                    if (mapBtn.contains(e.getPoint())){
                        somethingClicked = true;
                    }
                    else if (readyBtn.contains(e.getPoint())){
                        somethingClicked = true;
                    }
                    if(!somethingClicked){
                        permSelectedChar = -1;
                    }
                }
            });
        }

    }
}
