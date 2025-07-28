package constants;

import java.awt.*;

/**
 * [GameConstants.java]
 * Interface housing various general game constants.
 * Includes values for gravity, window sizes, proximity radii, and other game-related settings.
 * Centralizes important game parameters for easy access and modification.
 * @author Michael Khart
 * @version 1.0, January 22, 2024
 */
public interface GameConstants {
    int GRAVITY = 6;
    Dimension windowSize = new Dimension(1080, 800);
    int PROXIMITY_RADIUS = 500;
    long INVINCIBILITY_DURATION = 1500L;
    long POWERUP_RESPAWN_DURATION = 20000;
}
