package constants;

import entities.Vector;

/**
 * [MovementConstants.java]
 * Interface containing constants related to movement mechanics in the game.
 * Includes values for speeds, distances, and cooldowns of different movement abilities.
 * Aids in defining standardized movement behaviors for game entities.
 * @author Michael Khart
 * @version 1.0, January 22, 2024
 */
public interface MovementConstants {
    Vector DASH_SPEED = new Vector(40, 0);
    Vector LAUNCH_SPEED = new Vector(0, -50);
    int DASH_DISTANCE = 150;
    int LAUNCH_DISTANCE = 300;
    long DASH_COOLDOWN = 2000L;
    long LAUNCH_COOLDOWN = 5000L;

    int JUMP_SPEED_ADDITION = (-50);
    int KNOCKBACK_SPEED = -30;
}
