package entities;
import constants.MovementConstants;

/**
 * [Alive.java]
 * Represents a living game object in the game, such as avatars or enemies.
 * Extends GameObject and provides additional properties like health, speed, and collision handling.
 * Serves as a base class for entities that have health and can move.
 * @author Michael Khart
 * @version 1.0, January 22, 2024
 */
public abstract class Alive extends GameObject implements MovementConstants {
    private int health;
    private int maxHealth;
    private Vector speed;
    private double defence;
    private double damageMultiplier;
    private double defenceMultiplier;

    public Alive() {
        super();
    }

    public Alive(Hitbox hitbox, String ID, int maxHealth, double defence, double damageMultiplier, double defenceMultiplier) {
        super(hitbox, ID);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.defence = defence;
        this.speed = new Vector();
        this.damageMultiplier = damageMultiplier;
        this.defenceMultiplier = defenceMultiplier;
    }
    /**
     * handleCollision
     * Abstract method to handle collisions with other game objects.
     * Specific behavior to be defined in subclasses.
     * @param otherObj The other game object involved in the collision.
     */
    public abstract void handleCollision(GameObject otherObj);

    /**
     * getKnockedBack
     * Applies a knockback effect to the entity based on collision with another object.
     * Calculates the direction and intensity of the knockback.
     * @param otherObject The object that the entity collides with.
     */
    protected void getKnockedBack(GameObject otherObject) {
        double XSpeed, YSpeed, XDistance;

        // If a projectile or explosion hits us, we want the knockback to be more severe
        if ((otherObject instanceof Projectile) || otherObject instanceof Explosion) {
            Projectile projectile = ((Projectile) otherObject);

            XSpeed = projectile.getSpeed().getIntX();
            YSpeed = KNOCKBACK_SPEED;

        } else {
            XDistance = (int) (this.getHitbox().getX() - otherObject.getHitbox().getX());

            if (XDistance > 0) {
                XSpeed = ((Math.abs(this.speed.getIntX())));
            } else {
                XSpeed = ((Math.abs(this.speed.getIntX())) * -1);
            }

            YSpeed = KNOCKBACK_SPEED;
        }

        this.speed.setIntX((int) XSpeed);
        this.speed.setIntX((int) YSpeed);
    }

    /*
    ====================================================================
    getters and setters
    ====================================================================
     */
    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public int getMaxHealth() {
        return maxHealth;
    }
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
    public double getDefence() {
        return defence;
    }
    public Vector getSpeed() {
        return speed;
    }
    public void setSpeed(Vector speed) {
        this.speed = speed;
    }
    public double getDamageMultiplier() {
        return damageMultiplier;
    }
    public void setDamageMultiplier(double damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }
    public double getDefenceMultiplier() {
        return defenceMultiplier;
    }
    public void setDefenceMultiplier(double defenceMultiplier) {
        this.defenceMultiplier = defenceMultiplier;
    }
}
