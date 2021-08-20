package client.modules.visual;

import client.modules.Module;

public class PrestigeChams extends Module {

    private static PrestigeChams INSTANCE = new PrestigeChams();
    public PrestigeChams(){
        super("PrestigeChams", "Makes everyone look like zPrestige", Category.VISUAL);
        this.setInstance();
    }

    public static PrestigeChams getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PrestigeChams();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

}
