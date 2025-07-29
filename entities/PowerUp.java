package entities;

import static constants.GameConstants.POWERUP_RESPAWN_DURATION;

/**
 * [PowerUp.java]
 * Represents a power-up in the game.
 * Extends the Interactable class and includes functionality to boost avatar abilities.
 * Power-ups can enhance avatars in various ways, such as increasing damage or defense.
 * Each power-up has a specific type and effect on avatars.
 * @author Dilen De Silva
 * @version 1.0, January 22, 2024
 */
public class PowerUp extends Interactable {

    private final BoostType boostType;
    private long lastBroken;

    /**
     * Constructor for PowerUp.
     * Initializes a power-up with specified hitbox, ID, boost type, and boost multiplier.
     * @param ID The unique identifier for this power-up.
     * @param hitbox The hitbox representing the power-up's area.
     * @param boostType The type of boost this power-up provides.
     * @param boostMultiplier The multiplier value of the boost.
     */
    public PowerUp(String ID, Hitbox hitbox, BoostType boostType, double boostMultiplier) {
        super(hitbox, ID);
        this.boostType = boostType;
        this.boostType.setBoostMultiplier(boostMultiplier);
        this.lastBroken = System.currentTimeMillis();
    }

    /**
     * boostAvatar
     * Applies the power-up's boost to an avatar.
     * The boost effect depends on the power-up's type.
     * @param avatar The avatar to apply the boost to.
     */
    public void boostAvatar(Avatar avatar) {
        if ((System.currentTimeMillis() - lastBroken) > POWERUP_RESPAWN_DURATION) {
            if (this.boostType.equals(BoostType.DAMAGE)) {
                avatar.setDamageMultiplier(avatar.getDamageMultiplier() + boostType.getBoostMultiplier());
            } else if (this.boostType.equals(BoostType.DEFENSE)) {
                avatar.setDefenceMultiplier(avatar.getDamageMultiplier() + boostType.getBoostMultiplier());
            } else {
                avatar.setUltimateCharged(true);
            }
            this.lastBroken = System.currentTimeMillis();
        }
    }


    /**
     * BoostType
     * Enum representing different types of boosts provided by power-ups
     */
    public enum BoostType {
        DAMAGE(1.1),
        DEFENSE(1.1),
        ULTIMATE(1.0);
        private double boostMultiplier;
        BoostType(double boostMultiplier) {
            this.boostMultiplier = boostMultiplier;
        }
        public double getBoostMultiplier() {
            return boostMultiplier;
        }
        public void setBoostMultiplier(double boostMultiplier) {
            this.boostMultiplier = boostMultiplier;
        }
    }
}
