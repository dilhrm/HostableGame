package networking.ClientRequestTypes;

import java.io.Serializable;

public class NetworkingType extends RequestType {

    private NetworkingSubtype subtype;

    public NetworkingType(NetworkingSubtype subtype) {
        this.subtype = subtype;
    }

    public enum NetworkingSubtype implements Subtype, Serializable {
        DISCONNECT, UPDATE_REQUESTED;
    }

    @Override
    public Subtype getSubtype() {
        return subtype;
    }
}
