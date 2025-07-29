package constants;
import entities.Holster;
import java.awt.Dimension;

/**
 * [AvatarConstants.java]
 * Interface defining constants for avatar entities in the game.
 * Specifies dimensions, health, defense, damage multipliers, and more for various avatar types.
 * Serves as a centralized repository for avatar-related properties.
 * @author Dilen De Silva
 * @version 1.0, January 22, 2024
 */
public interface AvatarConstants {
    int MAX_PLAYER_SPEED = 10;
    int PLAYER_SPEED_ADDITION = 2;
    long RESPAWN_INVINCIBILITY_TIMER = 2000L;
    int PLAYER_RESPAWNS = 3;


    // NORMAN
    Dimension NORMAN_DIMENSIONS = new Dimension(75, 128);
    int NORMAN_MAX_HEALTH = 100;
    double NORMAN_DEFENCE = 1.1;
    double NORMAN_DAMAGE_MULTIPLIER = 1.1;
    double NORMAN_DEFENCE_MULTIPLIER = 1.1;
    int NORMAN_MAX_JUMPS = 2;
    Holster NORMAN_HOLSTER = new Holster(true, true, true);

    // TITAN
    Dimension TITAN_DIMENSIONS = new Dimension(105, 144);
    int TITAN_MAX_HEALTH = 200;
    double TITAN_DEFENCE = 1.5;
    double TITAN_DAMAGE_MULTIPLIER = 1.5;
    double TITAN_DEFENCE_MULTIPLIER = 1.0;
    int TITAN_MAX_JUMPS = 1;
    Holster TITAN_HOLSTER = new Holster(true, true, false);

    // GOBLINO
    Dimension GOBLINO_DIMENSIONS = new Dimension(78, 96);
    int GOBLINO_MAX_HEALTH = 75;
    double GOBLINO_DEFENCE = 1;
    double GOBLINO_DAMAGE_MULTIPLIER = 2.0;
    double GOBLINO_DEFENCE_MULTIPLIER = 0.5;
    int GOBLINO_MAX_JUMPS = 3;
    Holster GOBLINO_HOLSTER = new Holster(true, true, false);
}
