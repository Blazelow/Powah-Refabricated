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
import owmii.powah.block.energizing.EnergizingRodBlockEntity;
import owmii.powah.lib.client.util.Draw;
import owmii.powah.util.Util;

public class EnergizingRodHudRenderer implements BlockHudRenderer {
    @Override
    public boolean renderHud(GuiGraphics gui, BlockState state, Level world, BlockPos pos, Player player, BlockHitResult result,
            @Nullable BlockEntity te) {
        if (te instanceof EnergizingRodBlockEntity rod) {
            RenderSystem.getModelViewStack().pushMatrix();
            // TODO 26.1 RenderSystem.enableBlend();
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;
            int x = mc.getWindow().getGuiScaledWidth() / 2;
            int y = mc.getWindow().getGuiScaledHeight();
            String s = ChatFormatting.GRAY + I18n.get("info.lollipop.stored") + ": " + I18n.get("info.lollipop.fe.stored",
                    Util.addCommas(rod.getEnergy().getEnergyStored()), Util.numFormat(rod.getEnergy().getCapacity()));
            // TODO 26.1 RenderSystem.setShader(GameRenderer::getPositionTexShader);
            // TODO 26.1 RenderSystem.setShaderTexture(0, Identifier.fromNamespaceAndPath("lollipop", "textures/gui/ov_energy.png"));
            Draw.drawTexturedModalRect(gui, x - 37 - 1, y - 80, 0, 0, 74, 9, 0);
            Draw.gaugeH(x - 37, y - 79, 72, 16, 0, 9, ((EnergizingRodBlockEntity) te).getEnergy());
            gui.drawString(font, s, Math.round(x - (font.width(s) / 2.0f)), y - 67, 0xffffff);
            // TODO 26.1 RenderSystem.disableBlend();
            RenderSystem.getModelViewStack().popMatrix();
        }
        return true;
    }
}
