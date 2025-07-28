package networking.ClientRequestTypes;

import entities.Avatar;

import java.io.Serializable;

public class GameWindowType extends RequestType  {

    private GameWindowSubtype subtype;
    private Class<? extends Avatar> avatarClass;

    public GameWindowType() {
        this.subtype = GameWindowSubtype.READY;
    }

    public GameWindowType(Class<? extends Avatar> avatarChoiceClass) {
        this.subtype = GameWindowSubtype.CHARACTER_CHOSEN;
        this.avatarClass = avatarChoiceClass;
    }

    public enum GameWindowSubtype implements Subtype, Serializable {
        READY, GAME_ENDED, CHARACTER_CHOSEN;
    }

    @Override
    public Subtype getSubtype() {
        return subtype;
    }

    public Class<? extends Avatar> getAvatarClass() {
        return avatarClass;
    }

}
