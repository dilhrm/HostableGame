package entities;

/**
 * [Rocket.java]
 * Represents a rocket projectile in the game.
 * It extends the Projectile class and includes functionality to start an explosion.
 * @author Dilen De Silva
 * @version 1.0, January 22, 2024
 */
public class Rocket extends Projectile{

    /**
     * Constructor for Rocket.
     * Initializes a rocket with specified hitbox, ID, damage, and speed.
     * @param ID The unique identifier for this rocket.
     * @param hitbox The hitbox representing the rocket's area.
     * @param damage The damage value of the rocket.
     * @param speed The speed and direction of the rocket.
     */
    public Rocket(String ID, Hitbox hitbox, int damage, Vector speed) {
        super(hitbox, ID, speed, damage);
    }

    /**
     * startExplosion
     * Initiates an explosion at the rocket's current location.
     * @return Explosion, the explosion created by the rocket.
     */
    public Explosion startExplosion() {
        return (new Explosion((EXPLOSION_ID_PREFIX + this.getCreatorID()), this.getHitbox().getCenter(), getDamage()));
    }

    /**
     * update
     * Updates the position of the rocket each frame.
     */
    @Override
    public void update() {
        this.getHitbox().translateUsingSpeed(this.getSpeed());
    }
}
