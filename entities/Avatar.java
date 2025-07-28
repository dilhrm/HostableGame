package entities;

import constants.*;
import entities.MovementAbilities.MovementAbility;
import entities.Enemy;

import entities.MovementAbilities.*;
import entities.Holster.WeaponHolster;
import java.awt.*;
import java.io.Serializable;
import entities.Vector.Direction;

import javax.swing.plaf.synth.SynthOptionPaneUI;

import static entities.Projectile.calculateProjectileSpeed;
import static entities.Projectile.addBloom;

/**
 * [Avatar.java]
 * This abstract class represents an avatar, a character in the game.
 * It includes properties and methods common to all avatars, such as health,
 * movement abilities, and attack methods. It serves as a base class for specific avatar types.
 * @author  Michael Khart
 * @version 1.0, January 22, 2024
 */
public abstract class Avatar extends Alive implements Serializable, AvatarConstants, AttackConstants, MovementConstants, PrefixConstants, GameConstants {
    private static final long serialVersionUID = 9876544;
    private int remainingLives;
    private int jumps;
    private int maxJumps;
    private Holster weaponHolster;
    private MovementType currentMovement;
    private MovementAbility currentMovementAbility;
    private long lastDashUse;
    private long lastLaunchUse;
    private boolean ultimateCharged;


    /**
     * MovementType
     * This enum represents the current movement type of the avatar
     */
    public enum MovementType {
        IN_ABILITY, REGULAR;
    }


    /**
     * Avatar
     * Default constructor for the avatar class
     */
    public Avatar() {
        super();
    }

    /**
     * Avatar
     * Constructor for the avatar class
     */
    public Avatar(Hitbox hitbox, String ID, int maxHealth, double defence, double damageMultiplier, double defenceMultiplier, int maxJumps, Holster weaponHolster) {
        super(hitbox, ID, maxHealth, defence, damageMultiplier, defenceMultiplier);
        this.maxJumps = maxJumps;
        this.jumps = maxJumps;
        this.remainingLives = PLAYER_RESPAWNS;
        this.weaponHolster = weaponHolster;
        this.currentMovement = MovementType.REGULAR;
        this.ultimateCharged = false;
        currentMovementAbility = null;
    }

    /**
     * ultimateAbility
     * Abstract method that needs to be implemented in subclasses.
     * It defines the avatar's ultimate ability.
     * @return Attacks, the specific type of attack based on the avatar's ultimate ability.
     */
    public abstract Attacks ultimateAbility();

    /**
     * handleCollision
     * Handles the collision of the avatar with other game objects.
     * Depending on the type of the colliding object, it performs different actions like taking damage or getting a power-up.
     * @param otherObject The game object that this avatar collides with.
     */
    public void handleCollision(GameObject otherObject) {
        CollisionType collisionType = this.collides(otherObject);
        boolean notInvincible = (!(collisionType.equals(CollisionType.FIRST_INVINCIBLE)) && !(collisionType.equals(CollisionType.BOTH_INVINCIBLE)));

        if ((otherObject instanceof Enemy) || (otherObject instanceof Hazard)) {
            this.getKnockedBack(otherObject);
            if (notInvincible) {
                this.takeDamage(otherObject);
            }
        } else if (otherObject instanceof Interactable) {
            if (otherObject instanceof PowerUp) {
                ((PowerUp) otherObject).boostAvatar(this);
            } else {
                correctPositioning((Interactable) otherObject);
            }
        } else if (otherObject instanceof Attacks) {
            this.getKnockedBack(otherObject);
            if (notInvincible) {
                this.takeDamage(otherObject);
            }
        }
    }

    /**
     * takeDamage
     * Calculates and applies damage to the avatar based on the attacking object.
     * The damage is adjusted by the avatar's defense and defense multiplier.
     * @param otherObject The object inflicting damage upon this avatar.
     */
    private void takeDamage(GameObject otherObject) {
        if (otherObject instanceof Attacks) {
            Attacks attack = ((Attacks) otherObject);
            this.setHealth((int) (this.getHealth() - (attack.getDamage() / (this.getDefence() * this.getDefenceMultiplier()))));
        } else if (otherObject instanceof Enemy) {
            Enemy enemy = ((Enemy) otherObject);
            this.setHealth((int) (this.getHealth() - (enemy.getCollideDamage() / (this.getDefence() * this.getDefenceMultiplier()))));
        } else if (otherObject instanceof Hazard) {
            Hazard hazard = ((Hazard) otherObject);
            this.setHealth((int) (this.getHealth() - (hazard.getDamage() / (this.getDefence() * this.getDefenceMultiplier()))));
        }

        this.getHitbox().setLastCollided(System.currentTimeMillis());
        otherObject.getHitbox().setLastCollided(System.currentTimeMillis());
    }

    /**
     * correctPositioning
     * Corrects the avatar's position upon collision with an interactable object.
     * Adjusts the avatar's position to prevent overlapping or going through the object.
     * @param interactable The interactable object the avatar has collided with.
     */
    private void correctPositioning(Interactable interactable) {
        double avatarBottom = this.getHitbox().getY() + this.getHitbox().getHeight();
        double avatarRight = this.getHitbox().getX() + this.getHitbox().getWidth();
        double avatarLeft = this.getHitbox().getX();
        double avatarTop = this.getHitbox().getY();

        double prevAvatarBottom = avatarBottom - this.getSpeed().getIntY();
        double prevAvatarRight = avatarRight - this.getSpeed().getIntX();
        double prevAvatarLeft = avatarLeft - this.getSpeed().getIntX();
        double prevAvatarTop = avatarTop - this.getSpeed().getIntY();

        double interactableBottom = interactable.getHitbox().getY() + interactable.getHitbox().getHeight();
        double interactableRight = interactable.getHitbox().getX() + interactable.getHitbox().getWidth();
        double interactableLeft = interactable.getHitbox().getX();
        double interactableTop = interactable.getHitbox().getY();

        // If the avatar lands on something
        if ((avatarBottom >= interactableTop) && (prevAvatarBottom < interactableTop)) {
            this.getHitbox().setIntY((int) (interactableTop - this.getHitbox().getHeight()));
            this.getSpeed().setIntY(0);
            this.jumps = this.maxJumps;
            // If the avatar hits their head on something
        } else if ((avatarTop <= interactableBottom) && (prevAvatarTop > interactableBottom)) {
            this.getHitbox().setIntY((int) interactableBottom);
            this.getSpeed().setIntY((this.getSpeed().getIntY() / -2));
            // If the avatar hits something to their right
        } else if ((avatarRight >= interactableLeft) && (prevAvatarRight < interactableLeft)) {
            this.getHitbox().setIntX((int) (interactableLeft - this.getHitbox().getWidth() - 10));
            this.getSpeed().setIntX((this.getSpeed().getIntX() * -2));
            if (this.getSpeed().getIntY() > 0) {
                this.getSpeed().setIntY(this.getSpeed().getIntY() - 5);
            }
            // If the avatar hits something to their left
            System.out.println("hit the RIGHT of something ");

        } else if ((avatarLeft <= interactableRight) && (prevAvatarLeft > interactableRight)) {
            this.getHitbox().setIntX((int) interactableRight + 10);
            this.getSpeed().setIntX((this.getSpeed().getIntX() * -2));
            if (this.getSpeed().getIntY() > 0) {
                this.getSpeed().setIntY(this.getSpeed().getIntY() - 5);
            }
            System.out.println("hit the LEFT of something ");
        } else {
            if (this.getHitbox().getCenterX() > interactable.getHitbox().getCenterX()) {
                this.getHitbox().setLocation((int) interactableRight, (int) avatarTop);
            } else if (this.getHitbox().getCenterX() < interactable.getHitbox().getCenterX()) {
                this.getHitbox().setLocation((int) (interactableLeft - this.getHitbox().getIntWidth()), (int) avatarTop);
            }
        }
    }


    /**
     * attack
     * Generates an attack based on the avatar's current weapon.
     * The type of attack varies depending on the weapon the avatar is holding.
     * @param point The target point for the attack.
     * @return Attacks, the attack object generated.
     */
    public Attacks attack(Vector point) {
        WeaponHolster currentWeapon = this.getWeaponHolster().getCurrentWeapon();

        if (currentWeapon.equals(Holster.WeaponHolster.MELEE)) {
            Hitbox meleeHitbox = (Hitbox) this.getHitbox().clone();
            if (this.getDirection().equals(Direction.LEFT)) {
                meleeHitbox.setIntX(meleeHitbox.getIntX() - 20);
            } else  {
                meleeHitbox.setIntX(meleeHitbox.getIntX() + meleeHitbox.getIntWidth() + 20);
            }
            return (new Melee((MELEE_ID_PREFIX + this.getIDWithoutPrefix()), (meleeHitbox), (int) (MELEE_DAMAGE * this.getDamageMultiplier())));
        } else if (currentWeapon.equals(Holster.WeaponHolster.GUN)) {
            Vector speed = calculateProjectileSpeed(this.getHitbox().getCenter(), point);
            speed = addBloom(speed);
            return (new Rocket((ROCKET_ID_PREFIX + this.getIDWithoutPrefix()), (new Hitbox(this.getHitbox().getCenter(), ROCKET_DIMENSION)), (int) (BULLET_DAMAGE * this.getDamageMultiplier()), (speed)));
        } else {
            Vector speed = calculateProjectileSpeed(this.getHitbox().getCenter(), point);
            // no bloom
            return (new Rocket((ROCKET_ID_PREFIX + this.getIDWithoutPrefix()), (new Hitbox(this.getHitbox().getCenter(), ROCKET_DIMENSION)), (int) (ROCKET_DAMAGE * this.getDamageMultiplier()), (speed)));
        }
    }

    /**
     * move
     * Moves the avatar based on its current speed and applies gravity.
     * Also handles avatar movement if it is in the middle of a movement ability like dash or launch.
     */
    public void move() {
        if (currentMovement.equals(MovementType.REGULAR)) {
            this.getHitbox().translateUsingSpeed(this.getSpeed());
            this.getSpeed().setIntY(this.getSpeed().getIntY() + GRAVITY); // applies gravity

            int currentSpeed = this.getSpeed().getIntX();
            if (currentSpeed != 0) {
                int deceleration = (int) Math.ceil(Math.abs(currentSpeed) / 5.0);
                deceleration *= Integer.signum(currentSpeed); // ensures that speed deceleration works for both directions
                this.getSpeed().setIntX(currentSpeed - deceleration);
            }

        } else if (currentMovement.equals(MovementType.IN_ABILITY)) {
            continueAbility();
        }
    }

    /**
     * continueAbility
     * Continues the movement ability currently in progress.
     * Handles the movement for abilities like dash and launch.
     */
    private void continueAbility() {
        if (currentMovementAbility instanceof Dash) {
            if (currentMovementAbility.getDistanceTraveled() < DASH_DISTANCE) {
                currentMovementAbility.setDistanceTraveled(currentMovementAbility.getDistanceTraveled() + Math.abs(currentMovementAbility.getSpeed().getIntX()));
                this.getHitbox().translateUsingSpeed(currentMovementAbility.getSpeed());
            } else {
                currentMovementAbility = null;
                currentMovement = MovementType.REGULAR;
            }
        } else if (currentMovementAbility instanceof Launch) {
            if (currentMovementAbility.getDistanceTraveled() < LAUNCH_DISTANCE) {
                currentMovementAbility.setDistanceTraveled(currentMovementAbility.getDistanceTraveled() - currentMovementAbility.getSpeed().getIntY());
                this.getHitbox().translateUsingSpeed(currentMovementAbility.getSpeed());
            } else {
                currentMovementAbility = null;
                currentMovement = MovementType.REGULAR;
            }
        }
    }

    /**
     * switchWeapon
     * Switches the avatar's current weapon to the specified weapon holster.
     * @param weapon The weapon holster to switch to.
     */
    public void switchWeapon(Holster.WeaponHolster weapon) {
        this.weaponHolster.switchWeapon(weapon);
    }

    /**
     * speedUp
     * Increases the avatar's horizontal speed in the specified direction.
     * @param direction The direction to speed up towards.
     */
    public void speedUp(Direction direction) {
        if (direction.equals(Direction.RIGHT)) {
            this.getSpeed().setIntX(this.getSpeed().getIntX() + PLAYER_SPEED_ADDITION);
        } else {
            this.getSpeed().setIntX(this.getSpeed().getIntX() - PLAYER_SPEED_ADDITION);
        }
    }

    /**
     * dash
     * Initiates a dash movement if the avatar is in a regular state and the cooldown has elapsed.
     */
    public void dash() {
        if (this.currentMovement.equals(MovementType.REGULAR)) {
            if (System.currentTimeMillis() - lastDashUse > DASH_COOLDOWN) {
                initiateDash();
            }
        }
    }

    /**
     * initiateDash
     * Sets up the avatar's movement type to a dash ability.
     */
    private void initiateDash() {
        this.currentMovementAbility = new Dash(this.getDirection());
        this.currentMovement = MovementType.IN_ABILITY;
        this.lastDashUse = System.currentTimeMillis();
    }

    /**
     * launch
     * Initiates a launch movement if the avatar is in a regular state and the cooldown has elapsed.
     */
    public void launch() {
        if (this.currentMovement.equals(MovementType.REGULAR)) {
            if (System.currentTimeMillis() - lastLaunchUse > LAUNCH_COOLDOWN) {
                initiateLaunch();
            }
        }
    }

    /**
     * initiateLaunch
     * Sets up the avatar's movement type to a launch ability.
     */
    private void initiateLaunch() {
        this.currentMovement = MovementType.IN_ABILITY;
        this.currentMovementAbility = new Launch(this.getDirection());
        this.lastLaunchUse = System.currentTimeMillis();
    }

    /**
     * jump
     * Causes the avatar to jump, adjusting its vertical speed.
     * Can only be performed if the avatar has remaining jumps.
     */
    public void jump() {
        if (this.jumps > 0) {
            this.getSpeed().setIntY(this.getSpeed().getIntY() + JUMP_SPEED_ADDITION);
            jumps--;
        }
    }

    /**
     * respawn
     * Respawns the avatar at a specified point after losing a life.
     * Resets health, speed, and weapon, and applies invincibility temporarily.
     * @param respawnPoint The point where the avatar will respawn.
     */
    public void respawn(Point respawnPoint) {
        this.remainingLives--;
        this.setHealth(this.getMaxHealth());
        this.setSpeed(new Vector(0, 0));
        this.getWeaponHolster().setCurrentWeapon(Holster.WeaponHolster.MELEE);
        this.getHitbox().setLocation(respawnPoint);
        this.getHitbox().setLastCollided(System.currentTimeMillis() + RESPAWN_INVINCIBILITY_TIMER);
    }

    /**
     * returns the direction that this object is moving
     * @return Direction OBject
     */
    public Vector.Direction getDirection() {
        if (this.getSpeed().getIntX() >= 0) {
            return Direction.RIGHT;
        } else {
            return Direction.LEFT;
        }
    }


    /*
    ========================================================================================================
    getters and setters
    ========================================================================================================
     */

    public Holster getWeaponHolster() {
        return weaponHolster;
    }


    public int getRemainingLives() {
        return remainingLives;
    }

    public void setRemainingLives(int remainingLives) {
        this.remainingLives = remainingLives;
    }

    public int getMaxJumps() {
        return maxJumps;
    }

    public void setMaxJumps(int maxJumps) {
        this.maxJumps = maxJumps;
    }

    public boolean isUltimateCharged() {
        return ultimateCharged;
    }

    public void setUltimateCharged(boolean ultimateCharged) {
        this.ultimateCharged = ultimateCharged;
    }
}
