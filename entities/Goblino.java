package entities;

/**
 * [Goblino.java]
 * This class represents a 'Goblino', which is a type of Avatar in the game.
 * @author  Dilen De Silva
 * @version 1.0, January 22, 2024
 */
public class Goblino extends Avatar {

    /**
     * Default constructor for Goblino.
     * Creates a Goblino with default settings.
     */
    public Goblino() {}

    /**
     * Constructor for Goblino with a specific ID.
     * Initializes a Goblino avatar with specified characteristics and invokes the superclass constructor.
     * @param ID A unique identifier for this Goblino.
     */
    public Goblino(String ID) {
        super(new Hitbox(new Vector(), GOBLINO_DIMENSIONS), (GOBLINO_ID_PREFIX + ID), GOBLINO_MAX_HEALTH, GOBLINO_DEFENCE, GOBLINO_DAMAGE_MULTIPLIER, GOBLINO_DEFENCE_MULTIPLIER, GOBLINO_MAX_JUMPS, GOBLINO_HOLSTER);
    }

    /**
     * ultimateAbility
     * This method activates the Goblino's ultimate ability.
     * It increases the Goblino's jumps, health, damage, and defense multipliers,
     * and creates an explosion attack. If the ultimate is not charged, it returns null.
     * @return Attacks, specifically an Explosion attack if ultimate is charged, null otherwise.
     */
    @Override
    public Attacks ultimateAbility() {
        if (isUltimateCharged()) {
            // Enhance the Goblino's abilities
            this.setMaxJumps(this.getMaxJumps() + 1);
            this.setHealth(this.getMaxHealth());
            this.setDamageMultiplier(this.getDamageMultiplier() + 0.5);
            this.setDefenceMultiplier(this.getDefenceMultiplier() + 0.1);

            // Reset ultimate charge
            this.setUltimateCharged(false);

            // Create and return a new Explosion attack
            return (new Explosion(this.getIDWithoutPrefix(), this.getHitbox().getCenter(), EXPLOSION_DAMAGE));
        }
        return null;
    }

    /**
     * toString
     * Converts the Goblino object's data to a string format.
     * Includes the ID and speed of the Goblino.
     * @return String representing the Goblino's data.
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("ID = " + this.getID() + this.getSpeed().toString());
        return str.toString();
    }
}
