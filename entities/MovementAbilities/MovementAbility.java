package entities.MovementAbilities;
import constants.MovementConstants;
import entities.Vector.Direction;
import entities.Vector;

/**
 * [MovementAbility.java]
 * This abstract class represents a general movement ability for an avatar.
 * It provides basic structure for specific movement abilities like Dash and Launch.
 * Includes properties such as speed and distance traveled during the ability.
 * @author  Michael Khart
 * @version 1.0, January 22, 2024
 */
public abstract class MovementAbility implements MovementConstants {
    private Vector speed;
    private int distanceTraveled;

    /**
     * Constructor for MovementAbility.
     * Initializes a movement ability with a specific speed.
     * @param speed The speed vector of the movement ability.
     */
    public MovementAbility(Vector speed, Direction direction) {
        this.speed = speed;
        this.distanceTraveled = 0;

        if (direction.equals(Direction.LEFT)) {
            this.speed.setIntX(this.speed.getIntX() * -1);
        }
    }

    /**
     * getDirection
     * Returns the Direction enum of the direction that the abiity is moving.
     * @return Direction enum of the directino of the ability.
     */
    public Vector.Direction getDirection() {
        if (this.speed.getIntX() >= 0) {
            return Vector.Direction.RIGHT;
        } else {
            return Vector.Direction.LEFT;
        }
    }

    /*
    ====================================================================
    getters and setters
    ====================================================================
     */

    public Vector getSpeed() {
        return speed;
    }

    public void setSpeed(Vector speed) {
        this.speed = speed;
    }

    public int getDistanceTraveled() {
        return distanceTraveled;
    }

    public void setDistanceTraveled(int distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }


}
