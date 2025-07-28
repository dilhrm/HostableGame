package entities.MovementAbilities;
import entities.Vector.Direction;
import entities.Avatar;
import entities.MovementAbilities.MovementAbility;

/**
 * [Dash.java]
 * Represents the dash movement ability for an avatar.
 * Extends the MovementAbility class, defining a specific behavior for dashing.
 * Allows an avatar to quickly move in a direction for a short distance.
 * @author  Michael Khart
 * @version 1.0, January 22, 2024
 */
public class Dash extends MovementAbility {

    /**
     * Constructor for Dash.
     * Initializes the dash movement ability in a specific direction.
     * @param abilityDirection The direction in which the dash is performed.
     */
    public Dash(Direction abilityDirection) {
        super(DASH_SPEED, abilityDirection);
    }
}
