package entities;

/**
 * [Titan.java]
 * This class represents a 'Titan', which is a type of Avatar in the game.
 * It extends the Avatar class, providing specific functionalities for a Titan character,
 * including its ultimate ability and custom constructor.
 * @author  Michael Khart
 * @version 1.0, January 22, 2024
 */
public class Titan extends Avatar {

    /**
     * Default constructor for Titan.
     * Creates a Titan with default settings.
     */
    public Titan() {}

    /**
     * Constructor for Titan with a specific ID.
     * Initializes a Titan avatar with specified characteristics and invokes the superclass constructor.
     * @param ID A unique identifier for this Titan.
     */
    public Titan(String ID) {
        super(new Hitbox(new Vector(), TITAN_DIMENSIONS), (TITAN_ID_PREFIX + ID), TITAN_MAX_HEALTH, TITAN_DEFENCE, TITAN_DAMAGE_MULTIPLIER, TITAN_DEFENCE_MULTIPLIER, TITAN_MAX_JUMPS, TITAN_HOLSTER);
    }

    /**
     * ultimateAbility
     * This method activates the Titan's ultimate ability.
     * It enhances the Titan's health, damage, and defense multipliers,
     * and resets the ultimate charge. If the ultimate is not charged, it returns null.
     * @return Attacks, specifically a halved-damage Explosion attack if ultimate is charged, null otherwise.
     */
    @Override
    public Attacks ultimateAbility() {
        if (this.isUltimateCharged()) {
            this.setMaxHealth(this.getMaxHealth() * 2);
            this.setHealth(this.getMaxHealth());
            this.setDamageMultiplier(this.getDamageMultiplier() + 0.1);
            this.setDefenceMultiplier(this.getDefenceMultiplier() + 0.5);
            this.setUltimateCharged(false);
            return (new Explosion(this.getIDWithoutPrefix(), this.getHitbox().getCenter(), (EXPLOSION_DAMAGE / 2)));
        }
        return null;
    }

    /**
     * toString
     * Converts the Titan object's data to a string format.
     * Includes the ID and speed of the Titan.
     * @return String representing the Titan's data.
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("ID = " + this.getID() + this.getSpeed().toString());
        return str.toString();
    }
}
