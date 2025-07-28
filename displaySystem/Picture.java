package displaySystem;


import entities.Hitbox;

import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.*;

/**
 * [Picture.java]
 * This class is how all the pictures are stored and displayed
 * @author Dilen De Silva
 * @version 1.0 January 22, 2024
 */
public class Picture {
    public int x;
    public int y;
    public int width, height;
    String imageDirectory;
    public Image image;

    /**
     * Picture
     * Constructor
     * @param x
     * @param y
     * @param width
     * @param height
     * @param imageDirectory
     */
    public Picture(int x, int y, int width, int height, String imageDirectory) {

        this.imageDirectory = imageDirectory;
        this.x = x;
        this.y = y;

        //Attempts to render image
        try {
            this.image = ImageIO.read(new File(imageDirectory));
        } catch (IOException ex) {
            System.out.println("Image doesn't exist. " + imageDirectory);
        }

        this.width = width;
        this.height = height;
    }

    /**
     * isMouseInPicture
     * takes any point(should be a mouse point) and checks if the image contains the point
     * @param point
     * @return boolean isPointOnPicture
     */
    public boolean isMouseInPicture(Point point) {
        if (point == null){
            return false;
        }
        return (point.x > this.x && point.x < this.x + this.width && point.y > this.y && point.y < this.y + this.height);
    }

    /**
     * draw
     * draws the picture
     * @param g
     */
    public void draw(Graphics g) {
        g.drawImage(this.image, this.x, this.y, this.width, this.height, null);
    }

    /**
     * resize
     * can take the picture and multiply its size value by the scale given.
     * @param scale
     */
    public void resize(double scale) {
        this.width = (int) (this.width * scale);
        this.height = (int) (this.height * scale);
        this.image = image.getScaledInstance(this.width, this.height, Image.SCALE_SMOOTH);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

