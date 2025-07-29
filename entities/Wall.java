package entities;

/**
 * [Wall.java]
 * Represents a wall in the game.
 * Extends the Interactable class, providing basic properties for a wall entity.
 * @author Dilen De Silva
 * @version 1.0, January 22, 2024
 */
public class Wall extends Interactable {

    /**
     * Constructor for Wall.
     * Initializes a wall with a specified hitbox and ID.
     * @param ID The unique identifier for this wall.
     * @param hitbox The hitbox representing the wall's area.
     */
    public Wall(String ID, Hitbox hitbox) {
        super(hitbox, ID);
    }
}
