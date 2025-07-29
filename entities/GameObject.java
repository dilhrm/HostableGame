package entities;
import constants.*;

/**
 * [GameObject.java]
 * Represents a basic game object in the game.
 * Provides a foundational structure for game entities, including hitbox and ID management.
 * Includes an inner enum for defining collision types.
 * @author  Dilen De Silva
 * @version 1.0, January 22, 2024
 */
public abstract class GameObject implements PrefixConstants, GameConstants {

    private Hitbox hitbox;
    private String ID;

    /**
     * [CollisionType.java]
     * Enum representing different types of collisions between game objects.
     * Used for collision handling.
     */
    public enum CollisionType {
        BOTH_INVINCIBLE, FIRST_INVINCIBLE, SECOND_INVINCIBLE, NONE_INVINCIBLE
    }

    public GameObject() {}

    public GameObject(Hitbox hitbox, String ID) {
        this.hitbox = hitbox;
        this.ID = ID;
    }

    /**
     * collides
     * Checks for and categorizes the type of collision with another game object, which will be
     * used to determine how damage is distributed when a collision occurs.
     * @param otherObj The other game object involved in the collision.
     * @return CollisionType, the type of collision that has occurred
     */
    public CollisionType collides(GameObject otherObj) {
        CollisionType collisionType = CollisionType.NONE_INVINCIBLE;
        long currTime = System.currentTimeMillis();

        if ((this.hitbox.getLastCollided() + INVINCIBILITY_DURATION) > currTime) {
            collisionType = CollisionType.FIRST_INVINCIBLE;
        }

        if ((otherObj.hitbox.getLastCollided() + INVINCIBILITY_DURATION) > currTime) {
            if (collisionType == CollisionType.FIRST_INVINCIBLE) {
                collisionType = CollisionType.BOTH_INVINCIBLE;
            } else {
                collisionType = CollisionType.SECOND_INVINCIBLE;
            }
        }

        this.hitbox.setLastCollided(currTime);
        otherObj.hitbox.setLastCollided(currTime);

        return collisionType;
    }

    /*
    ====================================================================
    getters and setters
    ====================================================================
     */
    public Hitbox getHitbox() {
        return hitbox;
    }
    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }
    public String getIDWithoutPrefix() {
        return (this.getID().split("\\-"))[1];
    }
}
