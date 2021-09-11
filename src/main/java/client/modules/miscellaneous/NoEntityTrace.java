package client.modules.miscellaneous;

import client.gui.impl.setting.Setting;
import client.modules.Module;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class NoEntityTrace extends Module {
    private static NoEntityTrace INSTANCE = new NoEntityTrace();
    public Setting<Boolean> pickaxe = register(new Setting<>("Pickaxe", true));
    public Setting<Boolean> gapple = register(new Setting<>("Gapple", false));

    public NoEntityTrace() {
        super("NoEntityTrace", "Prevents you from hitting things.", Category.MISC);
        setInstance();
    }

    public static NoEntityTrace getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoEntityTrace();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}


