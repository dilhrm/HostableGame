package entities;

import constants.AttackConstants;

/**
 * [Attacks.java]
 * This abstract class represents a generic attack in the game.
 * It extends the GameObject class, providing functionality for handling damage.
 * @author  Michael Khart
 * @version 1.0, January 22, 2024
 */
public abstract class Attacks extends GameObject implements AttackConstants {
    private int damage;

    /**
     * Constructor for Attacks.
     * Initializes an attack with a specified hitbox, ID, and damage.
     * @param hitbox The hitbox representing the attack's area.
     * @param ID The unique identifier for this attack.
     * @param damage The damage value of the attack.
     */
    public Attacks(Hitbox hitbox, String ID, int damage) {
        super(hitbox, ID);
        this.damage = damage;
    }

    /**
     * getDamage
     * Retrieves the damage value of the attack.
     * @return int, the damage value of the attack.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * setDamage
     * Sets the damage value of the attack.
     * @param damage The damage value to be set for the attack.
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * getCreatorID
     * Retrieves the ID of the creator of the attack.
     * @return String, the ID of the creator of the attack.
     */
    public String getCreatorID() {
        return (this.getIDWithoutPrefix());
    }
}
