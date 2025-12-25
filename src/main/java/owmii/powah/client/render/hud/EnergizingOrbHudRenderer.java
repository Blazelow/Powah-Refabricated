package owmii.powah.client.render.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;
import owmii.powah.block.energizing.EnergizingOrbBlockEntity;
import owmii.powah.util.Util;

public class EnergizingOrbHudRenderer implements BlockHudRenderer {
    @Override
    public boolean renderHud(GuiGraphics gui, BlockState state, Level world, BlockPos pos, Player player, BlockHitResult result,
                             @Nullable BlockEntity te) {
        if (te instanceof EnergizingOrbBlockEntity orb) {
            if (orb.getBuffer().getCapacity() > 0) {
                RenderSystem.getModelViewStack().pushMatrix();
                // TODO 26.1 RenderSystem.enableBlend();
                Minecraft mc = Minecraft.getInstance();
                Font font = mc.font;
                int x = mc.getWindow().getGuiScaledWidth() / 2;
                int y = mc.getWindow().getGuiScaledHeight();
                String s = "" + ChatFormatting.GREEN + orb.getBuffer().getPercent() + "%";
                String s1 = ChatFormatting.GRAY + I18n.get("info.lollipop.fe.stored", Util.addCommas(orb.getBuffer().getEnergyStored()),
                        Util.numFormat(orb.getBuffer().getCapacity()));
                gui.drawString(font, s, Math.round(x - (font.width(s) / 2.0f)), y - 90, 0xffffff);
                gui.drawString(font, s1, Math.round(x - (font.width(s1) / 2.0f)), y - 75, 0xffffff);
                // TODO 26.1 RenderSystem.disableBlend();
                RenderSystem.getModelViewStack().popMatrix();
            }
        }
        return true;
    }
}
