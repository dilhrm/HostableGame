package entities;

import entities.Vector.Direction;
import java.awt.*;

/**
 * [Projectile.java]
 * This abstract class represents a projectile in the game.
 * It extends the Attacks class, providing common functionality for projectile-based attacks,
 * including speed and direction handling.
 * @author  Michael Khart
 * @version 1.0, January 22, 2024
 */
public abstract class Projectile extends Attacks {
    private Vector speed;

    /**
     * Constructor for Projectile.
     * Initializes a projectile attack with a specified hitbox, ID, speed, and damage.
     * @param hitbox The hitbox representing the projectile's area.
     * @param ID The unique identifier for this projectile.
     * @param speed The speed and direction of the projectile.
     * @param damage The damage value of the projectile.
     */
    public Projectile(Hitbox hitbox, String ID, Vector speed, int damage) {
        super(hitbox, ID, damage);
        this.speed = speed;
    }

    /**
     * update
     * Abstract method that needs to be implemented in subclasses to define projectile behavior per frame.
     */
    public abstract void update();

    /**
     * calculateProjectileSpeed
     * Calculates the speed vector for a projectile based on its origin and target points.
     * @param origin The starting point of the projectile.
     * @param target The target point of the projectile.
     * @return Vector, the speed vector for the projectile.
     */
    public static Vector calculateProjectileSpeed(Vector origin, Vector target) {
        // calculate direction vector components
        double speedX = target.getIntX() - origin.getIntX();
        double speedY = target.getIntY() - origin.getIntY();
        if ((speedX == 0) || (speedY == 0)) {
            return new Vector();
        }

        double magnitude = Math.sqrt(speedX * speedX + speedY * speedY); // calculate magnitude of the direction
        // scale to the constant speed
        int xSpeed = (int) (speedX / magnitude * MAX_BULLET_SPEED.getIntX());
        int ySpeed = (int) (speedY / magnitude * MAX_BULLET_SPEED.getIntX());

        return new Vector(xSpeed, ySpeed);
    }

    /**
     * addBloom
     * Adjusts the speed vector of the projectile to simulate bloom (inaccuracy).
     * @param speed The original speed vector of the projectile.
     * @return Vector, the adjusted speed vector with bloom applied.
     */
    public static Vector addBloom(Vector speed) {
        double angleAdjustment = Math.random() * BULLET_BLOOM; // bloom amount

        // Apply the adjustment to the current vector
        double newX = speed.getIntX() * Math.cos(angleAdjustment) - speed.getIntY() * Math.sin(angleAdjustment);
        double newY = speed.getIntX() * Math.sin(angleAdjustment) + speed.getIntY() * Math.cos(angleAdjustment);

        speed.setIntX((int) newX);
        speed.setIntY((int) newY);

        return speed;
    }

    /**
     * getDirection
     * Retrieves the direction of the projectile based on its speed.
     * @return Direction, the direction the projectile is moving in.
     */
    public Direction getDirection() {
        if (this.speed.getIntX() >= 0) {
            return Vector.Direction.RIGHT;
        } else {
            return Vector.Direction.LEFT;
        }
    }

    /**
     * getSpeed
     * Retrieves the current speed vector of the projectile.
     * @return Vector, the current speed of the projectile.
     */
    public Vector getSpeed() {
        return speed;
    }


}