package networking.ClientRequestTypes;

import java.io.Serializable;

public class OtherType extends RequestType{
    private OtherSubtype subType;

    public OtherType(OtherSubtype subType) {
        this.subType = subType;
    }

    public enum OtherSubtype implements Subtype, Serializable {
        KEY_1, KEY_2, KEY_3, KEY_4;
    }

    @Override
    public Subtype getSubtype() {
        return subType;
    }
}
