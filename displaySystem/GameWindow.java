package displaySystem;
import constants.AvatarConstants;
import constants.EnemyConstants;
import constants.ImageConstants;
import constants.PrefixConstants;
import entities.*;
import networking.Client;
import networking.ServerMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/**
 * [GameWindow.java]
 * This class is the GUI in which the main game is played
 * @author Dilen De Silva
 * @version 1.0 January 22, 2024
 */
public class GameWindow extends JFrame implements ImageConstants, PrefixConstants, AvatarConstants, EnemyConstants {
    private GraphicsPanel canvas;
    private Map<ServerMessage, ServerMessage> syncAvatars;
    private Map<ServerMessage, ServerMessage> syncEnemies;
    private Map<ServerMessage, ServerMessage> syncInteractables;
    private Map<ServerMessage, ServerMessage> syncAttacks;

    private BufferedImage[] avatarImages = new BufferedImage[20];
    private BufferedImage[] enemyImages = new BufferedImage[20];

    private BufferedImage[] interactableImages = new BufferedImage[20];
    private BufferedImage other;
    private boolean gameOver = false;

    /**
     * GameWindow
     * Constructor
     */
    public GameWindow() {
        super();
        this.setSize(1800, 1000);

        this.syncAvatars = Collections.synchronizedMap(new ConcurrentHashMap<>());
        this.syncEnemies = Collections.synchronizedMap(new ConcurrentHashMap<>());
        this.syncAttacks = Collections.synchronizedMap(new ConcurrentHashMap<>());
        this.syncInteractables = Collections.synchronizedMap(new ConcurrentHashMap<>());

        try {
            avatarImages[0] = image(CHARACTER1_PATH);
            avatarImages[0] = resize(avatarImages[0], avatarImages[0].getWidth()*5,avatarImages[0].getHeight()*8);
            avatarImages[1] = image(CHARACTER2_PATH);
            avatarImages[1] = resize(avatarImages[1], avatarImages[1].getWidth()*7,avatarImages[1].getHeight()*9);
            avatarImages[2] = image(CHARACTER3_PATH);
            avatarImages[2] = resize(avatarImages[2], avatarImages[2].getWidth()*6,avatarImages[2].getHeight()*6);

            interactableImages[0] = image(TILE_PATH);
            interactableImages[0] = resize(interactableImages[0], 50, 50);

            other = image(CHARACTER1_PATH);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        canvas = new GraphicsPanel();
        this.add(canvas);
        this.setVisible(true);
    }

    /**
     * image
     * creates bufferedImage for usage
     * @param path
     * @return
     * @throws IOException
     */
    public BufferedImage image(String path) throws IOException {
        return ImageIO.read(new File(path));
    }

    /**
     * setGameOver
     * sets this.gameOver to true
     * tells panel that game is over
     */
    public void setGameOver(){
        canvas.drawWinScreen(canvas.getGraphics());
        this.gameOver = true;
    }

    /**
     * resize
     * generates new scaled image from given parameters
     * @param img
     * @param newW
     * @param newH
     * @return BufferedImage rescaledImage
     */
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    /**
     * updateHitbox
     * adds and updates hitboxes by checking for messages from server(through client)
     * @param update
     */
    public void updateHitbox(ServerMessage update) {
        String id = update.getID();
        String prefix = id.split("-")[0];
        String prefixType = prefix.substring(0, 1);

        Map<ServerMessage, ServerMessage> targetMap = null;

        if (ATTACK_TYPE_PREFIX.equals(prefixType)) {
            targetMap = syncAttacks;
        } else if (INTERACTABLE_TYPE_PREFIX.equals(prefixType)) {
            targetMap = syncInteractables;
        } else if (ENEMY_TYPE_PREFIX.equals(prefixType)) {
            targetMap = syncEnemies;
        } else if (AVATAR_TYPE_PREFIX.equals(prefixType)) {
            targetMap = syncAvatars;
        } else{
            System.out.println("TARGET MAP NOT FOUND");
        }

        Map.Entry<ServerMessage, ServerMessage> valPair = findServerMessageById(targetMap, id);
        if (valPair != null) {
            valPair.getValue().setUpdatedHitbox(valPair.getKey().getUpdatedHitbox());
            valPair.getKey().setUpdatedHitbox(update.getUpdatedHitbox());
        } else {
            if(targetMap != null) {
                targetMap.put(update, update);
            }
        }

        this.repaint();
    }

    /**
     * findServerMessageById
     * helps in finding certain server messages when we only have id
     * @param map
     * @param id
     * @return
     */

    private Map.Entry<ServerMessage, ServerMessage> findServerMessageById(Map<ServerMessage, ServerMessage> map, String id) {
        for (Map.Entry<ServerMessage, ServerMessage> entry : map.entrySet()) {
            ServerMessage key = entry.getKey();
            if (key.getID().equals(id)) {
                return entry;
            }
        }
        return null;
    }

    public void removeHitbox(String id) {
        String prefix = id.split("-")[0];
        String prefixType = prefix.substring(0, 1);

        Map<ServerMessage, ServerMessage> targetMap;

        if (ATTACK_TYPE_PREFIX.equals(prefixType)) {
            targetMap = syncAttacks;
        } else if (INTERACTABLE_TYPE_PREFIX.equals(prefixType)) {
            targetMap = syncInteractables;
        } else if (ENEMY_TYPE_PREFIX.equals(prefixType)) {
            targetMap = syncEnemies;
        } else if (AVATAR_TYPE_PREFIX.equals(prefixType)) {
            targetMap = syncAvatars;
        } else {
            System.out.println("ERROR IN FINDING THE TARGET MAP");
            return;
        }

        removeServerMessageById(targetMap, id);
    }

    private void removeServerMessageById(Map<ServerMessage, ServerMessage> map, String id) {
        for (Iterator<Map.Entry<ServerMessage, ServerMessage>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<ServerMessage, ServerMessage> entry = iterator.next();
            if (entry.getKey().getID().equals(id)) {
                iterator.remove();
                break;
            }
        }
    }


    /**
     * calculateRotationAngle
     * calculates angles to display objects at when shooting
     * @param current
     * @param previous
     * @return
     */
    private double calculateRotationAngle(ServerMessage current, ServerMessage previous) {
        Hitbox currentHitbox = current.getUpdatedHitbox();
        Hitbox previousHitbox = previous.getUpdatedHitbox();
        double deltaX = currentHitbox.getX() - previousHitbox.getX();
        double deltaY = currentHitbox.getY() - previousHitbox.getY();
        return Math.atan2(deltaY, deltaX);
    }

    /**
     * class GraphicsPanel
     */
    class GraphicsPanel extends JPanel {
        /**
         * paintComponent
         * method that allows for drawing
         * @param g
         */
        @Override
        public void paintComponent(Graphics g) {
            if (!gameOver) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                drawServerMessages(syncAvatars, g2d);
                drawServerMessages(syncEnemies, g2d);
                drawServerMessages(syncInteractables, g2d);
                drawServerMessages(syncAttacks, g2d);
            } else {
                drawWinScreen(g);
            }

        }

        /**
         * drawWinScreen
         * draws screen that appears when a player wins
         * @param g
         */
        private void drawWinScreen(Graphics g){
            int screenWidth = 1800;
            int screenHeight = 1000;

            // Clear the screen
            g.clearRect(0, 0, screenWidth, screenHeight);

            // Set a background color
            g.setColor(new Color(135, 206, 250)); // Light sky blue color
            g.fillRect(0, 0, screenWidth, screenHeight);

            // Prepare the text
            String message = "You Won!";
            Font font = new Font("Castellar Regular", Font.PLAIN, 100);
            g.setFont(font);
            g.setColor(Color.BLUE);

            // Measure the size of the text
            FontMetrics metrics = g.getFontMetrics(font);
            int x = (screenWidth - metrics.stringWidth(message)) / 2; // Center text
            int y = ((screenHeight - metrics.getHeight()) / 2) + metrics.getAscent(); // Center text

            // Draw shadow for the text
            g.setColor(Color.GRAY);
            g.drawString(message, x + 2, y + 2); // Shadow position

            // Draw the main text
            g.setColor(Color.BLUE);
            g.drawString(message, x, y);
        }

        /**
         * drawServerMessage
         * draws images based on the messages provided by server
         * @param map
         * @param g2d
         */
        private void drawServerMessages(Map<ServerMessage, ServerMessage> map, Graphics2D g2d) {
            for (Map.Entry<ServerMessage, ServerMessage> entry : map.entrySet()) {
                ServerMessage currentMessage = entry.getKey();
                Hitbox currentHitbox = currentMessage.getUpdatedHitbox();
                String prefix = (currentMessage.getID().split("-")[0] + "-");


                if (BULLET_ID_PREFIX.equals(prefix)) {
//                    double rotationalAngle = calculateRotationAngle(currentMessage, entry.getValue());
//                    g2d.rotate(rotationalAngle, currentHitbox.getCenterX(), currentHitbox.getCenterY());
                    g2d.setColor(Color.RED);
                    g2d.fillRect(currentHitbox.getIntX(), currentHitbox.getIntY(), currentHitbox.getIntWidth(), currentHitbox.getIntHeight());
                } else if (WALL_ID_PREFIX.equals(prefix)) {
                    interactableImages[0] =  GameWindow.resize(interactableImages[0], currentHitbox.getIntWidth(), currentHitbox.getIntHeight());
                    g2d.drawImage(interactableImages[0], currentHitbox.getIntX(), currentHitbox.getIntY(), this);
//                } else if (MIDDLE_WALL_ID.equals(prefix)) {
//                    interactableImages[1] =  GameWindow.resize(interactableImages[1], currentHitbox.getIntWidth(), currentHitbox.getIntHeight());
//                    g2d.drawImage(interactableImages[0], currentHitbox.getIntX(), currentHitbox.getIntY(), this);
                }else if (HAZARD_ID_PREFIX.equals(prefix)) {
                    g2d.setColor(Color.RED);
                    g2d.fillRect(currentHitbox.getIntX(), currentHitbox.getIntY(), currentHitbox.getIntWidth(), currentHitbox.getIntHeight());
                } else if (POWERUP_ID_PREFIX.equals(prefix)) {
                    g2d.setColor(Color.GREEN);
                    g2d.fillRect(currentHitbox.getIntX(), currentHitbox.getIntY(), currentHitbox.getIntWidth(), currentHitbox.getIntHeight());
                } else if (EXPLOSION_ID_PREFIX.equals(prefix)) {
                    g2d.setColor(Color.YELLOW);
                    g2d.fillOval(currentHitbox.getIntX(), currentHitbox.getIntY(), currentHitbox.getIntWidth(), currentHitbox.getIntHeight());
                } else if (ROCKET_ID_PREFIX.equals(prefix) || MELEE_ID_PREFIX.equals(prefix)) {
                    g2d.setColor(Color.BLUE);
                    g2d.fillRect(currentHitbox.getIntX(), currentHitbox.getIntY(), currentHitbox.getIntWidth(), currentHitbox.getIntHeight());
                } else if (THUNDERGUARD_ID_PREFIX.equals(prefix)) {
                    g2d.drawImage(enemyImages[1], currentHitbox.getIntX(), currentHitbox.getIntY(), this);
                } else if (NORMAN_ID_PREFIX.equals(prefix)) {
                    g2d.drawImage(avatarImages[0], currentHitbox.getIntX(), currentHitbox.getIntY(), this);
                } else if (TITAN_ID_PREFIX.equals(prefix)) {
                    g2d.drawImage(avatarImages[1], currentHitbox.getIntX(), currentHitbox.getIntY(), this);
                } else if (GOBLINO_ID_PREFIX.equals(prefix)) {
                    g2d.drawImage(avatarImages[2], currentHitbox.getIntX(), currentHitbox.getIntY(), this);
                }

            }
        }

    }

}
