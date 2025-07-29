package constants;
import entities.Vector;
import java.awt.Dimension;
/**
 * [AttackConstants.java]
 * Interface containing constants related to various attack mechanics in the game.
 * Defines values such as durations, damages, dimensions, and speeds for different attack types.
 * @author Dilen De Silva
 * @version 1.0, January 22, 2024
 */
public interface AttackConstants {

    long MELEE_DURATION = 1000L;
    long MELEE_DAMAGE = 20;

    int BULLET_DROP = 1;
    double BULLET_BLOOM = 0.1;
    int BULLET_DAMAGE = 30;
    Vector MAX_BULLET_SPEED = new Vector(35, 35);
    Dimension BULLET_DIMENSION = new Dimension(10, 20);

    int MAX_EXPLOSION_RADIUS = 50;
    int INITIAL_EXPLOSION_RADIUS = 5;
    int SHRAPNEL_DAMAGE = 20;
    int EXPLOSION_DAMAGE = 80;
    int ROCKET_DAMAGE = 30;
    Dimension ROCKET_DIMENSION = new Dimension(20, 40);
}
