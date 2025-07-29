package entities;

import java.awt.*;

/**
 * [Norman.java]
 * This class represents a 'Norman', which is a type of Avatar in the game.
 * It extends the Avatar class, providing specific functionalities for a Norman character,
 * including its ultimate ability and custom constructor.
 * @author Dilen De Silva
 * @version 1.0, January 22, 2024
 */
public class Norman extends Avatar {

    /**
     * Default constructor for Norman.
     * Creates a Norman with default settings.
     */
    public Norman() {}

    /**
     * Constructor for Norman with a specific ID.
     * Initializes a Norman avatar with specified characteristics and invokes the superclass constructor.
     * @param ID A unique identifier for this Norman.
     */
    public Norman(String ID) {
        super(new Hitbox(new Vector(), NORMAN_DIMENSIONS), (NORMAN_ID_PREFIX + ID), NORMAN_MAX_HEALTH, NORMAN_DEFENCE, NORMAN_DAMAGE_MULTIPLIER, NORMAN_DEFENCE_MULTIPLIER, NORMAN_MAX_JUMPS, NORMAN_HOLSTER);
    }

    /**
     * ultimateAbility
     * This method activates the Norman's ultimate ability.
     * It increments the Norman's remaining lives and resets the ultimate charge.
     * If the ultimate is not charged, it returns null.
     * @return Attacks, specifically an Explosion attack if ultimate is charged, null otherwise.
     */
    @Override
    public Attacks ultimateAbility() {
        if (isUltimateCharged()) {
            this.setRemainingLives(this.getRemainingLives() + 1);
            this.setUltimateCharged(false);
            return (new Explosion(this.getIDWithoutPrefix(), this.getHitbox().getCenter(), EXPLOSION_DAMAGE));
        }
        return null;
    }

    /**
     * toString
     * Converts the Norman object's data to a string format.
     * Includes the ID, coordinates, and speed of the Norman.
     * @return String representing the Norman's data.
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("ID = " + this.getID() + " THESE CORIDNATSE ARE :" + this.getHitbox().getCenter()  + " W SPEED :" +  this.getSpeed().toString());
        return str.toString();
    }
}
