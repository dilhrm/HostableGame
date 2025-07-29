package entities;

import constants.AttackConstants;

/**
 * [Bullet.java]
 * Represents a bullet projectile in the game.
 * It extends the Projectile class and includes specific behavior like gravity effect on the bullet.
 * @author  Dilen De Silva
 * @version 1.0, January 22, 2024
 */
public class Bullet extends Projectile implements AttackConstants {

    /**
     * Constructor for Bullet.
     * Initializes a bullet with specified hitbox, ID, damage, and speed.
     * @param ID The unique identifier for this bullet.
     * @param hitbox The hitbox representing the bullet's area.
     * @param damage The damage value of the bullet.
     * @param speed The speed and direction of the bullet.
     */
    public Bullet(String ID, Hitbox hitbox, int damage, Vector speed) {
        super(hitbox, ID, speed, damage);
    }

    /**
     * update
     * Updates the position and behavior of the bullet each frame.
     * Applies gravity to the bullet's trajectory.
     */
    @Override
    public void update() {
        this.getHitbox().translateUsingSpeed(this.getSpeed());
        this.getSpeed().setIntY(this.getSpeed().getIntY() + BULLET_DROP);
    }
}
