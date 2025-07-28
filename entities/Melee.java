package entities;

/**
 * [Melee.java]
 * Represents a melee attack in the game.
 * It extends the Attacks class and includes timing functionality to manage the attack's duration.
 * @author  Michael Khart
 * @version 1.0, January 22, 2024
 */
public class Melee extends Attacks {

    private Long startTime;

    /**
     * Constructor for Melee attack.
     * Initializes a melee attack with specified hitbox, ID, and damage.
     * @param ID The unique identifier for this melee attack.
     * @param hitbox The hitbox representing the attack's area.
     * @param damage The damage value of the attack.
     */
    public Melee(String ID, Hitbox hitbox, int damage) {
        super(hitbox, ID, damage);
        this.startTime = System.currentTimeMillis();
    }

    /**
     * checkTimer
     * Checks if the melee attack has exceeded its duration.
     * @return boolean, true if the attack is still valid, false if the duration has expired.
     */
    public boolean checkTimer() {
        if ((System.currentTimeMillis() - this.startTime) > MELEE_DURATION) {
            return false;
        }
        return true;
    }

    /**
     * getStartTime
     * Retrieves the start time of the melee attack.
     * @return Long, the start time of the attack.
     */
    public Long getStartTime() {
        return startTime;
    }

    /**
     * setStartTime
     * Sets the start time of the melee attack.
     * @param startTime The start time to set for the attack.
     */
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
}
