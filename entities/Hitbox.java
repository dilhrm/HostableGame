package entities;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * [Hitbox.java]
 * Represents the physical boundaries of a game object in the game.
 * Extends the Rectangle class and implements Serializable for object serialization.
 * Used for collision detection and positioning of game objects.
 * Includes methods for translating and accessing hitbox properties.
 * @author  Michael Khart
 * @version 1.0, January 22, 2024
 */
public class Hitbox extends Rectangle implements Serializable{
    private static final long serialVersionUID = 11001100L;
    private transient long lastCollided; // transient since we dont need this field to serialize

    /**
     * Constructor for Hitbox with specific dimensions.
     * Initializes the hitbox with specified x, y coordinates, width, and length.
     * @param x The x-coordinate of the hitbox.
     * @param y The y-coordinate of the hitbox.
     * @param width The width of the hitbox.
     * @param length The length of the hitbox.
     */
    public Hitbox(int x, int y, int width, int length) {
        super(x, y, width, length);
    }

    /**
     * Constructor for Hitbox with a center point and dimension.
     * Initializes the hitbox based on a central point and dimension (width and height).
     * @param center The central point of the hitbox.
     * @param dimension The dimension (width and height) of the hitbox.
     */
    public Hitbox(Vector center, Dimension dimension) {
        super(center.getIntX(), center.getIntY(), dimension.width, dimension.height);
    }

    /**
     * contains
     * Checks for if a vector is within a hitbox.
     * @param vector The point to check.
     * @return The boolean format of if it is within the hitbox.
     */
    public boolean contains(Vector vector) {
        if ((this.contains(vector))) {
            return true;
        }
        return false;
    }

    /*
    ====================================================================
    getters and setters
    ====================================================================
     */

    public void translateUsingSpeed(Vector speed) {
        super.translate(speed.getIntX(), speed.getIntY());
    }
    public Rectangle2D getBoundingBox() {
        return new Rectangle2D.Double(x, y, width, height);
    }
    public void setIntX(int x) {
        this.x = x;
    }
    public int getIntX() {
        return this.x;
    }
    public Vector getCenter() {
        return new Vector(((int) this.getCenterX()), ((int) this.getCenterY()));
    }
    public int getIntWidth() {
        return this.width;
    }
    public int getIntHeight() {
        return this.height;
    }
    public void setIntY(int y) {
        this.y = y;
    }
    public int getIntY() {
        return this.y;
    }
    public long getLastCollided() {
        return lastCollided;
    }
    public void setLastCollided(long lastCollided) {
        this.lastCollided = lastCollided;
    }
    public String toString() {
        return this.getCenter().toString();
    }
}
