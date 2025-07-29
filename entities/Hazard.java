package entities;

/**
 * [Hazard.java]
 * Represents a hazardous object in the game.
 * Extends the Interactable class and includes a damage property.
 * Hazards can inflict damage on avatars that interact with them.
 * @author Dilen De Silva
 * @version 1.0, January 22, 2024
 */
public class Hazard extends Interactable {
    private int damage;

    /**
     * Constructor for Hazard.
     * Initializes a hazard with a specified hitbox, ID, and damage value.
     * @param ID The unique identifier for this hazard.
     * @param hitbox The hitbox representing the hazard's area.
     * @param damage The damage value that the hazard can inflict.
     */
    public Hazard(String ID, Hitbox hitbox, int damage) {
        super(hitbox, ID);
        this.damage = damage;
    }

    /*
    =========================================
    getters and setters
    =========================================
     */

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
