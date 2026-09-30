package owmii.powah.client.guide;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public final class GuideBookClient {
    private GuideBookClient() {
    }

    public static void open() {
        Minecraft.getInstance().setScreenAndShow(new GuideScreen());
    }
}
