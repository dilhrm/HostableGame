package networking.ClientRequestTypes;
import entities.*;

import java.awt.*;
import java.io.Serializable;

public class NewActionType extends RequestType{

    private NewActionSubtype subtype;
    private Vector mousePoint;

    public NewActionType(NewActionSubtype subtype, Vector mousePoint) {
        this.subtype = subtype;
        this.mousePoint = mousePoint;
    }

    public enum NewActionSubtype implements Subtype, Serializable {
        NEW_ATTACK, ULTIMATE_ABILITY;
    }

    @Override
    public Subtype getSubtype() {
        return subtype;
    }

    public Vector getMousePoint() {
        return mousePoint;
    }
}
