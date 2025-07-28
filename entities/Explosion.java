package entities;

import java.awt.*;
import java.util.LinkedList;
import java.util.Random;
import static entities.Projectile.calculateProjectileSpeed;

/**
 * [Explosion.java]
 * Represents an explosion attack in the game.
 * It extends the Attacks class and includes functionality for expanding radius and creating shrapnel.
 * @author  Michael Khart
 * @version 1.0, January 22, 2024
 */
public class Explosion extends Attacks {
    private int radius;
    private int shrapnelNum;

    /**
     * Constructor for Explosion.
     * Initializes an explosion with a specified location and damage.
     * @param ID The unique identifier for this explosion.
     * @param point The central point of the explosion.
     * @param damage The damage value of the explosion.
     */
    public Explosion(String ID, Vector point , int damage) {
        super(new Hitbox(point, new Dimension(10, 10)), ID, damage);
        this.radius = INITIAL_EXPLOSION_RADIUS;
        setupValues();
    }

    /**
     * setupValues
     * Sets up initial values for the explosion, including the number of shrapnel.
     */
    private void setupValues() {
        Random random = new Random();
        this.shrapnelNum = random.nextInt(20) + 5;
    }

    /**
     * startShrapnel
     * Initiates shrapnel projectiles from the explosion.
     * @return LinkedList<Bullet>, a list of bullet objects representing shrapnel.
     */
    private LinkedList<Bullet> startShrapnel() {
        Random random = new Random();
        int randomX;
        int randomY;
        LinkedList<Bullet> shrapnel = new LinkedList<>();

        while (shrapnelNum > 0){
            randomX = random.nextInt(200)-100;
            randomY = random.nextInt(200)-100;

            Vector speed = calculateProjectileSpeed(new
                            Vector((int) this.getHitbox().getCenterX(), (int) this.getHitbox().getY()),
                            new Vector(randomX, randomY)
            );

            shrapnel.add(new Bullet(
                        (BULLET_ID_PREFIX + this.getCreatorID()),
                        new Hitbox(((int) this.getHitbox().getCenterX()), ((int) this.getHitbox().getCenterY()),
                        20, 20), SHRAPNEL_DAMAGE, speed
            ));
        }

        return shrapnel;
    }

    /**
     * update
     * Updates the expansion of the explosion each frame.
     */
    public void update() {
        radius = ((MAX_EXPLOSION_RADIUS - radius) / 6) + 1;
        this.getHitbox().translate(-radius, -radius);
        this.getHitbox().width = this.getHitbox().width + (radius * 2);
        this.getHitbox().height = this.getHitbox().height + (radius * 2);
    }


    /**
     * checkSize
     * Checks if the explosion has reached its maximum radius.
     * @return boolean, true if the explosion has reached maximum size, false otherwise.
     */
    public boolean checkSize() {
        if (radius >= MAX_EXPLOSION_RADIUS) {
            return true;
        }
        return false;
    }

    /*
    ==================================================================================
    getters and setters
    ==================================================================================
     */

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
