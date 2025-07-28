package entities.MovementAbilities;
import entities.Vector.Direction;
import entities.Avatar;
import entities.MovementAbilities.MovementAbility;

/**
 * [Launch.java]
 * Represents the launch movement ability for an avatar.
 * Extends the MovementAbility class, defining a specific behavior for launching.
 * Allows an avatar to quickly move upwards, simulating a jump or launch action.
 * @author  Michael Khart
 * @version 1.0, January 22, 2024
 */
public class Launch extends MovementAbility {

    /**
     * Constructor for Launch.
     * Initializes the launch movement ability in a specific direction.
     * @param abilityDirection The direction in which the launch is performed.
     */
    public Launch(Direction abilityDirection) {
        super(LAUNCH_SPEED, abilityDirection);
    }
}
