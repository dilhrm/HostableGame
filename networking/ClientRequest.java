package networking;
import networking.ClientRequestTypes.RequestType;

import java.awt.*;
import java.io.Serializable;

/**
 * [ClientRequest.java]
 * Represents a client request in the networking context of the game.
 * Encapsulates a request type and client ID for communication purposes.
 * Implements Serializable for object serialization across network operations.
 * @author Dilen De Silva
 * @version 1.0, January 22, 2024
 */
public class ClientRequest implements Serializable {
    private static final long serialVersionUID = 11001100L;
    private String ID;
    private RequestType requestType;

    /**
     * Constructor for ClientRequest.
     * Initializes a client request with the client ID and specific request type.
     * @param ID The client's unique identifier.
     * @param requestType The type of request being made by the client.
     */
    public ClientRequest(String ID, RequestType requestType) {
        this.ID = ID;
        this.requestType = requestType;
    }


    /*
    ====================================================================
    getters and setters
    ====================================================================
     */


    public String getID() {
        return ID;
    }
    public String getIDWithoutPrefix() {
        return (this.getID().split("\\-"))[1];
    }

    public RequestType getRequestType() {
        return requestType;
    }

    @Override
    public String toString() {
        StringBuilder toStr = new StringBuilder(this.ID);
        toStr.append(" [ " + this.getRequestType());

        return (toStr.toString());
    }
}

