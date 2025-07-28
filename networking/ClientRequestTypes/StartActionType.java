package networking.ClientRequestTypes;

import java.io.Serializable;

public class StartActionType extends RequestType{

    private StartActionSubtype subType;

    public StartActionType(StartActionSubtype subType) {
        this.subType = subType;
    }

    public enum StartActionSubtype implements Subtype, Serializable {
        START_MOVING_LEFT, START_MOVING_RIGHT;
    }

    @Override
    public Subtype getSubtype() {
        return subType;
    }

    public void setSubType(StartActionSubtype subType) {
        this.subType = subType;
    }
}
