package networking;

import entities.*;

import java.awt.*;
import java.io.Serializable;
/**
 * [ServerMessage.java]
 * This class is the main form of communication for the server
 * @author Dilen De Silva
 * @version 1.0 January 22, 2024
 */
public class ServerMessage implements Serializable {
    private static final long serialVersionUID = 11001100L;
    private String ID;
    private Hitbox updatedHitbox;
    private MessageType messageType;

    /**
     * ServerMessage
     * Constructor
     * @param ID
     * @param updatedHitbox
     * @param messageType
     */
    public ServerMessage(String ID, Hitbox updatedHitbox, MessageType messageType) {
        this.ID = ID;
        this.updatedHitbox = updatedHitbox;
        this.messageType = messageType;
    }

    //enum to allow others to know what type of message is being sent
    public enum MessageType implements Serializable{
        WHISPER,
        BROADCAST;
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

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
