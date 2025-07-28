package entities;
import constants.EnemyConstants;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * [Enemy.java]
 * Represents an enemy in the game.
 * Extends the Alive class, providing additional functionality specific to enemies.
 * Includes properties like collide damage and mood, and methods for movement and sight detection.
 * The Mood enum defines different behavioral states of the enemy.
 * @author Michael Khart
 * @version 1.0, January 22, 2024
 */
public abstract class Enemy extends Alive implements EnemyConstants {
    private double collideDamage;
    private Mood mood;
    /**
     * [Mood.java]
     * Enum representing different moods or states of an enemy, affecting its behavior.
     */
    public enum Mood {PASSIVE, CHARGING, AGGRESSIVE, COOLDOWN;}

    /**
     * Construtor for Enemy
     * Initializes an enemy with specified parameters
     */
    public Enemy(Hitbox hitbox, String ID, int maxHealth, double defence, double damageMultiplier, double defenceMultiplier, double collideDamage) {
        super(hitbox, ID, maxHealth,  defence, damageMultiplier, defenceMultiplier);
        this.collideDamage = collideDamage;
        this.mood = Mood.PASSIVE;
    }

    /**
     * move
     * Abstract method to define the movement behavior of the enemy.
     * Specific movement logic to be implemented in subclasses.
     * @param proximity A list of game objects in proximity to the enemy.
     */
    abstract void move(ArrayList<GameObject> proximity);


    /**
     * onEdge
     * Checks if an enemy instance is hanging on an edge, returns boolean
     * Needed due to bugs where enemies get knocked back onto ledges and bug out.
     * @param otherObject The object they are ontop of
     * @return Boolean value of if they are on edge
     */
     boolean onEdge(Interactable otherObject) {
        if (!(otherObject instanceof PowerUp)) {
            Vector bottomLeft = new Vector(this.getHitbox().getIntX(), (this.getHitbox().getIntY() + this.getHitbox().getIntHeight()));
            Vector bottomRight = new Vector(((int) (this.getHitbox().getIntX() + this.getHitbox().getWidth())), (this.getHitbox().getIntY() + this.getHitbox().getIntHeight()));
            Vector bottomMiddleRight = new Vector((bottomRight.getIntX() - 15), (bottomRight.getIntY()));
            Vector bottomMiddleLeft = new Vector((bottomLeft.getIntX() + 15), (bottomLeft.getIntY()));

            // if the enemy had half of it hanging off then kill it
            if ((((otherObject.getHitbox().contains(bottomRight)) && ((otherObject.getHitbox().contains(bottomMiddleRight))))
                    && ((!otherObject.getHitbox().contains(bottomLeft)) && (!otherObject.getHitbox().contains(bottomMiddleLeft))))
                    || ( ((!otherObject.getHitbox().contains(bottomRight)) && ((!otherObject.getHitbox().contains(bottomMiddleRight))))
                    && ((otherObject.getHitbox().contains(bottomLeft)) && (otherObject.getHitbox().contains(bottomMiddleLeft)))))
            {
                this.setHealth(-1);
                return true;

                // if either the three rightmost or leftmost points are stil onto of the ledge, move back on to avoid glitching
            } else if (((otherObject.getHitbox().contains(bottomLeft)) && (otherObject.getHitbox().contains(bottomMiddleRight)))
                    || ((otherObject.getHitbox().contains(bottomRight)) && (otherObject.getHitbox().contains(bottomMiddleLeft)))) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * inSight
     * Primative ray tracing used to check if a player is within direct sight of an enemy
     * @param avatar The target avatar
     * @param proximity Surrounding obejcts
     * @return Boolean of if there is something in its way
     */
    public boolean inSight(Avatar avatar, ArrayList<GameObject> proximity) {
        Line2D line = new Line2D.Double(this.getHitbox().getIntX(), this.getHitbox().getIntY(), avatar.getHitbox().getIntX(), avatar.getHitbox().getIntY());

        for (GameObject object : proximity) {
            Rectangle2D objectBoundingBox = object.getHitbox().getBoundingBox();
            if (line.intersects(objectBoundingBox)) {
                return false;
            }
        }
        return true;
    }

    /*
    ====================================================================
    getters and setters
    ====================================================================
     */
    public double getCollideDamage() {
        return collideDamage;
    }
    public Mood getMood() {
        return mood;
    }
    public void setMood(Mood mood) {
        this.mood = mood;
    }


}



