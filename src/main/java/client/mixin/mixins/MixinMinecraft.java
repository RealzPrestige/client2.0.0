package client.mixin.mixins;

import client.Client;
import client.gui.impl.background.MainMenuScreen;
import client.gui.impl.background.MenuToggler;
import client.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = {Minecraft.class}, priority=0x7FFFFFFE)
public abstract class MixinMinecraft {
    @Inject(method = {"shutdownMinecraftApplet"}, at = {@At(value = "HEAD")})
    private void stopClient(CallbackInfo callbackInfo) {
        this.unload();
    }

    @Redirect(method = {"run"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"))
    public void displayCrashReport(Minecraft minecraft, CrashReport crashReport) {
        this.unload();
    }

    private void unload() {
        Client.onUnload();
    }

    @Inject(method = {"runTick()V"}, at = {@At(value = "RETURN")})
    private void runTick(CallbackInfo callbackInfo) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu) {
            if(MenuToggler.getInstance().isOn()) {
                Minecraft.getMinecraft().displayGuiScreen(new MainMenuScreen());
            }
        }
    }

    @Shadow
    public abstract void displayGuiScreen(@Nullable GuiScreen var1);

    @Inject(method = {"displayGuiScreen"}, at = {@At(value = "HEAD")})
    private void displayGuiScreen(GuiScreen screen, CallbackInfo ci) {
        if (screen instanceof GuiMainMenu) {
            this.displayGuiScreen(new MainMenuScreen());
        }
    }
}

