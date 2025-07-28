package networking.ClientRequestTypes;

import java.io.Serializable;

public abstract class RequestType implements Serializable {
    private static final long serialVersionUID = 123123121;
    public abstract Subtype getSubtype();

}
