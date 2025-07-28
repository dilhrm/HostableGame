package entities;

/**
 * [Holster.java]
 * Represents a holster for managing the weapons of an avatar.
 * Provides functionality to switch between different weapons and check their availability.
 * Includes the WeaponHolster enum to define different types of weapons.
 * @author  Michael Khart
 * @version 1.0, January 22, 2024
 */
public class Holster {

    private WeaponHolster currentWeapon;

    /**
     * Constructor for Holster.
     * Initializes the holster with available weapons based on the provided flags.
     * @param meleeUnlocked Indicates if the melee weapon is unlocked.
     * @param gunUnlocked Indicates if the gun weapon is unlocked.
     * @param rpgUnlocked Indicates if the RPG weapon is unlocked.
     */
    public Holster(boolean meleeUnlocked, boolean gunUnlocked, boolean rpgUnlocked) {
        WeaponHolster.MELEE.setCanHolster(meleeUnlocked);
        WeaponHolster.GUN.setCanHolster(gunUnlocked);
        WeaponHolster.RPG.setCanHolster(rpgUnlocked);
        this.currentWeapon = WeaponHolster.MELEE;
    }

    /**
     * [WeaponHolster.java]
     * Enum representing different types of weapons in the holster.
     * Defines the weapon types and their availability for use by an avatar.
     * Each weapon type has a boolean flag indicating whether it can be holstered or not.
     */
    public enum WeaponHolster {
        MELEE(true),
        GUN(true),
        RPG(false);
        private boolean canHolster;

        WeaponHolster(boolean canHolster) {
            this.canHolster = canHolster;
        }

        public boolean isCanHolster() {
            return canHolster;
        }
        public boolean canHolsterWeapon(WeaponHolster weapon) {
            switch (weapon) {
                case MELEE:
                    return WeaponHolster.MELEE.isCanHolster();
                case GUN:
                    return WeaponHolster.GUN.isCanHolster();
                case RPG:
                    return WeaponHolster.RPG.isCanHolster();
                default:
                    return false;
            }
        }

        public void setCanHolster(boolean bool) {
            this.canHolster = bool;
        }
    }

    /*
    ====================================================================
    getters and setters
    ====================================================================
     */

    public void switchWeapon(WeaponHolster weapon) {
        currentWeapon = weapon;
    }
    public WeaponHolster getCurrentWeapon() {
        return currentWeapon;
    }
    public void setCurrentWeapon(WeaponHolster currentWeapon) {
        this.currentWeapon = currentWeapon;
    }
}
