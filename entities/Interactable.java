package entities;

/**
 * [Interactable.java]
 * Represents a generic interactable object in the game.
 * Extends the GameObject class, providing basic properties for an object that can be interacted with.
 * This class serves as a base for more specific interactable objects like walls or hazards.
 * @author  Michael Khart
 * @version 1.0, January 22, 2024
 */
public class Interactable extends GameObject {

    /**
     * Constructor for Interactable.
     * Initializes an interactable object with a specified hitbox and ID.
     * @param hitbox The hitbox representing the area of the interactable object.
     * @param ID The unique identifier for this interactable object.
     */
    public Interactable(Hitbox hitbox, String ID) {
        super(hitbox, ID);
    }
}
