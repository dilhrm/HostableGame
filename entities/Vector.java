package entities;
import constants.*;

import java.awt.*;
import java.io.Serializable;
/**
 * [Vector.java]
 * Represents a 2D vector or point in the game.
 * Useful for defining positions, speeds, and directions. Implements Serializable for object serializatoin.
 * Includes an inner enum for findin general directions.
 * @author  Michael Khart
 * @version 1.0, January 22, 2024
 */
public class Vector implements Serializable {
    private static final long serialVersionUID = 123123123;
    /**
     * [Direction.java]
     * Enum representing simple directional values.
     * Used for defining basic left and right directions.
     */
    public enum Direction implements Serializable {
        LEFT, RIGHT;
    }
    private Point point;

    /**
     * Default constructor for Vector.
     * Initializes the vector at (0,0).
     */
    public Vector() {
        this.point = new Point(0, 0);
    }

    /**
     * Constructor for Vector with specific coordinates.
     * Initializes the vector with specified x and y values.
     * @param x The x-coordinate of the vector.
     * @param y The y-coordinate of the vector.
     */
    public Vector(int x, int y) {
        this.point = new Point(x, y);
    }


    /*
    ====================================================================
    getters and setters
    ====================================================================
     */

    public int getIntX() {
        return point.x;
    }
    public int getIntY() {
        return point.y;
    }
    public void setIntX(int x) {
        this.point.x = x;
    }
    public void setIntY(int y) {
        this.point.y = y;
    }
    @Override
    public String toString() {
        return "VECTOR WITH X" + point.x + " WITH Y " + point.y;
    }
}
