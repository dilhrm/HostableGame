package constants;

import entities.Vector;
import java.awt.Dimension;

/**
 * [EnemyConstants.java]
 * Interface containing constants specifically related to enemy entities in the game.
 * Defines values for dimensions, health, speed, defense, and other enemy attributes.
 * Facilitates a unified approach to setting enemy characteristics.
 * @author Michael Khart
 * @version 1.0, January 22, 2024
 */

public interface EnemyConstants {
    long THUNDERGUARD_COOLDOWN_DURATION = 500L;
    long THUNDERGUARD_CHARGEUP_DURATION = 1000;
    Dimension THUNDERGUARD_DIMENSIONS = new Dimension(75, 75);
    int THUNDERGUARD_MAX_HEALTH = 75;
    double THUNDERGUARD_DEFENCE = 1;
    double THUNDERGUARD_DAMAGE_MULTIPLIER = 2.0;
    double THUNDERGUARD_DEFENCE_MULTIPLIER = 0.5;
    double THUNDERGUARD_COLLIDE_DAMAGE = 20;
    Vector THUNDERGUARD_PASSIVE_SPEED = new Vector(5, 0);
    Vector THUNDERGUARD_AGGRESSIVE_SPEED = new Vector(30, 0);
    Vector THUNDERGUARD_CHARGEUP_SPEED = new Vector(-5, 0);
}
