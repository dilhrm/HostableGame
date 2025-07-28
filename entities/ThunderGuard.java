package entities;
import java.util.ArrayList;

/**
 * [ThunderGuard.java]
 * Represents the ThunderGuard enemy in the game.
 * Extends the Enemy class with specific behaviors and states like mood changes and target tracking.
 * Capable of different modes of action such as passive, charging, and aggressive.
 * @author Michael Khart
 * @version 1.0, January 22, 2024
 */
public class ThunderGuard extends Enemy{

    private long lastMoodChange;
    private Avatar target;

    // used to create default enemy
    public ThunderGuard(String ID) {
        super(new Hitbox(new Vector(), THUNDERGUARD_DIMENSIONS), (THUNDERGUARD_ID_PREFIX + ID), THUNDERGUARD_MAX_HEALTH, THUNDERGUARD_DEFENCE, THUNDERGUARD_DAMAGE_MULTIPLIER,
                THUNDERGUARD_DEFENCE_MULTIPLIER, THUNDERGUARD_COLLIDE_DAMAGE);
        this.lastMoodChange = System.currentTimeMillis();
        this.target = null;
    }

    /*

     */
    public ThunderGuard(String ID, Hitbox hitbox, int maxHealth, double defence, double damageMultiplier, double defenceMultiplier, double collideDamage) {
        super(hitbox, ID, maxHealth, defence, damageMultiplier, defenceMultiplier, collideDamage);
        this.lastMoodChange = System.currentTimeMillis();
        this.target = null;
    }

    /**
     * handleCollision
     * Handles collisions with different types of game objects.
     * Determines actions based on the type of object collided with and the current mood of the ThunderGuard.
     * @param otherObject The game object that the ThunderGuard collides with.
     */
    @Override
    public void handleCollision(GameObject otherObject) {

        if (otherObject instanceof Hazard) {
            // all enemies die when they hit a hazard
            this.setHealth(-1);
        } else {
            if (otherObject instanceof Interactable) {
                correctPositioning((Interactable) otherObject);
            } else if (otherObject instanceof Attacks) {
                if (getMood().equals(Mood.AGGRESSIVE)) { // IF WE ARE NOT CHARGING AT THE PLAYER then take damage
                    Attacks attacks = ((Attacks) otherObject);
                    if (!(attacks.getCreatorID().equals(this.getIDWithoutPrefix()))) {
                        CollisionType collisionType = this.collides(otherObject);
                        boolean notInvincible = (!(collisionType.equals(CollisionType.FIRST_INVINCIBLE)) && !(collisionType.equals(CollisionType.BOTH_INVINCIBLE)));

                        this.getKnockedBack(attacks);
                        if (notInvincible) {
                            this.setHealth((int) (this.getHealth() - (attacks.getDamage() / (this.getDefence() * this.getDefenceMultiplier()))));
                        }
                    }
                }
            }
        }

        this.getHitbox().setLastCollided(System.currentTimeMillis());
        otherObject.getHitbox().setLastCollided(System.currentTimeMillis());
    }

    /**
     * correctPositioning
     * Corrects the ThunderGuard's position upon collision with an interactable object.
     * Prevents overlapping or passing through the object.
     * @param interactable The interactable object the ThunderGuard has collided with.
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

        // If the enemy lands on something
        if ((avatarBottom >= interactableTop) && (prevAvatarBottom < interactableTop)) {
            this.getHitbox().setIntY((int) (interactableTop - this.getHitbox().getHeight()));
            this.getSpeed().setIntY(0);

            if (onEdge(interactable)) {
                this.getSpeed().setIntX(this.getSpeed().getIntX() * -1);
                this.getHitbox().translate(this.getSpeed().getIntX(), this.getSpeed().getIntY());
            }
            // If the enemy hits their head on something
        } else if ((avatarTop <= interactableBottom) && (prevAvatarTop > interactableBottom)) {
            this.getHitbox().setIntY((int) interactableBottom);
            this.getSpeed().setIntY(0);
            // If the enemy hits something to their right
        } else if ((avatarRight >= interactableLeft) && (prevAvatarRight < interactableLeft)) {
            this.getHitbox().setIntX((int) (interactableLeft - this.getHitbox().getWidth()));
            this.getSpeed().setIntX(0);
            if (this.getMood().equals(Mood.AGGRESSIVE)) {
                this.setMood(Mood.COOLDOWN);
                this.startCooldown();
            }
            // If the enemy hits something to their left
        } else if ((avatarLeft <= interactableRight) && (prevAvatarLeft > interactableRight)) {
            this.getHitbox().setIntX((int) interactableRight);
            this.getSpeed().setIntX(0);
            if (this.getMood().equals(Mood.AGGRESSIVE)) {
                this.setMood(Mood.COOLDOWN);
                this.startCooldown();
            }
        }

    }


    /**
     * move
     * Defines the movement behavior of the ThunderGuard based on its current mood.
     * Handles different movement strategies for passive, charging, aggressive, and cooldown states.
     * @param proximity A list of game objects in proximity to the ThunderGuard.
     */
    @Override
    public void move(ArrayList<GameObject> proximity) {
        this.getSpeed().setIntY(this.getSpeed().getIntY() - GRAVITY); // applies gravity

        if (getMood().equals(Mood.PASSIVE)) {
            Avatar closestAvatar = playersAttackable(proximity);
            if (closestAvatar != null) {
                this.target = closestAvatar;
                startChargeUp();
            } else { // no player to attack
                this.getHitbox().translateUsingSpeed(this.getSpeed());

                if (this.getSpeed().getIntX() > THUNDERGUARD_PASSIVE_SPEED.getIntX()) {
                    this.getSpeed().setIntX(this.getSpeed().getIntX() - (this.getSpeed().getIntX() / 10));
                }
            }
        } else if (getMood().equals(Mood.CHARGING)) {
            if (System.currentTimeMillis() - this.lastMoodChange > THUNDERGUARD_CHARGEUP_DURATION) {
                startAttack();
            } else {
                this.getHitbox().translateUsingSpeed(this.getSpeed());
            }
        } else if (this.getMood().equals(Mood.AGGRESSIVE)) {
            this.getHitbox().translateUsingSpeed(this.getSpeed());
        } else if (this.getMood().equals(Mood.COOLDOWN)) {
            if (System.currentTimeMillis() - this.lastMoodChange > THUNDERGUARD_COOLDOWN_DURATION) {
                becomePassive();
            }
        }
    }

    /**
     * startChargeUp
     * Initiates the charging phase of the ThunderGuard.
     * Sets the mood to CHARGING and determines the direction of the charge.
     */
    private void startChargeUp() {
        this.setMood(Mood.CHARGING);
        this.setSpeed(THUNDERGUARD_CHARGEUP_SPEED);
        if (target.getHitbox().getCenterX() > this.getHitbox().getCenterX()) {
            this.getSpeed().setIntX(this.getSpeed().getIntX() * -1);
        }
        this.lastMoodChange = System.currentTimeMillis();
    }

    /**
     * startAttack
     * Transitions the ThunderGuard into an aggressive state for attacking.
     * Sets the mood to AGGRESSIVE and adjusts speed for the attack phase.
     */
    private void startAttack() {
        this.setMood(Mood.AGGRESSIVE);
        this.setSpeed(THUNDERGUARD_AGGRESSIVE_SPEED);
        if (target.getHitbox().getCenterX() < this.getHitbox().getCenterX()) {
            this.getSpeed().setIntX(this.getSpeed().getIntX() * -1);
        }
        this.lastMoodChange = System.currentTimeMillis();
    }

    /**
     * startCooldown
     * Initiates the cooldown phase after an attack.
     * Sets the mood to COOLDOWN and starts the cooldown timer.
     */
    private void startCooldown() {
        this.setMood(Mood.COOLDOWN);
        this.lastMoodChange = System.currentTimeMillis();
        target = null;
    }
    /**
     * becomePassive
     * Resets the ThunderGuard to a passive state.
     * Sets the mood to PASSIVE and performs necessary resets for passive behavior.
     */
    private void becomePassive() {
        this.setMood(Mood.PASSIVE);
        this.lastMoodChange = System.currentTimeMillis();
    }

    /**
     * playersAttackable
     * Identifies attackable players (avatars) within the proximity of the ThunderGuard.
     * Filters out avatars not in sight or out of range.
     * @param proximity A list of game objects in proximity to the ThunderGuard.
     * @return Avatar, the nearest attackable avatar, or null if none are in range.
     */
    private Avatar playersAttackable(ArrayList<GameObject> proximity) {
        ArrayList<Avatar> avatarsInRange = new ArrayList<>(2);
        for (GameObject object : proximity) {
            if (object instanceof Avatar) {
                avatarsInRange.add((Avatar) object);
            }
        }

        if (avatarsInRange.size() > 0) {
            avatarsInRange.removeIf(avatar -> !inSight(avatar, proximity));
            if (avatarsInRange.isEmpty()) {
                return null;
            }
        } else {
            return null;
        }

        Avatar nearestAvatar = getNearestAvatar(avatarsInRange);
        return nearestAvatar;
    }

    /**
     * getNearestAvatar
     * Determines the nearest avatar from a list of avatars.
     * Used to target the closest avatar for attacking.
     * @param avatarsInRange A list of avatars within range of the ThunderGuard.
     * @return Avatar, the nearest avatar in the list.
     */
    private Avatar getNearestAvatar(ArrayList<Avatar> avatarsInRange) {
        Avatar nearestAvatar = null;
        double smallestDistance = Double.MAX_VALUE;

        for (Avatar avatar : avatarsInRange) {
            double deltaX = avatar.getHitbox().getCenterX() - this.getHitbox().getCenterX();
            double deltaY = avatar.getHitbox().getCenterY() - this.getHitbox().getCenterY();

            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            if (distance < smallestDistance) {
                smallestDistance = distance;
                nearestAvatar = avatar;
            }
        }
        return nearestAvatar;
    }
}
