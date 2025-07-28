package displaySystem;

import entities.Hitbox;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class DisplayObject {
    private String ID;
    private Hitbox updatedHitbox;
    private BufferedImage image;
    private ArrayList<BufferedImage> frames = new ArrayList<>();

    public DisplayObject(String ID, Hitbox updatedHitbox) {
        this.ID = ID;
        this.updatedHitbox = updatedHitbox;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Hitbox getUpdatedHitbox() {
        return updatedHitbox;
    }

    public void setUpdatedHitbox(Hitbox updatedHitbox) {
        this.updatedHitbox = updatedHitbox;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public ArrayList<BufferedImage> getFrames() {
        return frames;
    }

    public void setFrames(ArrayList<BufferedImage> frames) {
        this.frames = frames;
    }
}
