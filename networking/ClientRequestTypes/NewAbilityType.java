package networking.ClientRequestTypes;

import java.io.Serializable;

public class NewAbilityType extends RequestType{
    private NewAbilitySubtype subType;

    public NewAbilityType(NewAbilitySubtype subType) {
        this.subType = subType;
    }

    public enum NewAbilitySubtype implements Subtype, Serializable {
        JUMP, DASH, LAUNCH;
    }

    @Override
    public Subtype getSubtype() {
        return subType;
    }
}