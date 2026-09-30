package owmii.powah.lib.item;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import team.reborn.energy.api.EnergyStorage;
import owmii.powah.block.Tier;
import owmii.powah.components.PowahComponents;
import owmii.powah.lib.client.util.Text;
import owmii.powah.util.Util;

public abstract class EnergyItem extends PowahBaseItem
        implements IEnergyItemProvider, IEnergyContainingItem {

    private final Tier tier;

    public owmii.powah.block.Tier getTier() {
        return this.tier;
    }

    public EnergyItem(Properties properties, Tier tier) {
        super(properties);
        this.tier = tier;
    }

    public abstract long getEnergyCapacity();

    public abstract long getEnergyTransfer();

    @Override
    public Info getEnergyInfo() {
        return new Info(getEnergyCapacity(), getEnergyTransfer(), getEnergyTransfer());
    }

    @Override
    public boolean isChargeable(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder,
            TooltipFlag tooltipFlag) {
        var energy = EnergyStorage.ITEM.find(itemStack, ContainerItemContext.withConstant(itemStack));
        if (energy != null) {
            var capacity = getEnergyCapacity();
            builder.accept(Component.translatable("info.lollipop.stored").withStyle(ChatFormatting.GRAY).append(Text.COLON)
                    .append(Component
                            .translatable("info.lollipop.fe.stored", Util.addCommas(energy.getAmount()), Util.numFormat(capacity))
                            .withStyle(ChatFormatting.DARK_GRAY)));
            var maxExtract = getEnergyTransfer();
            builder.accept(Component.translatable("info.lollipop.max.io").withStyle(ChatFormatting.GRAY).append(Text.COLON).append(Component
                    .translatable("info.lollipop.fe.pet.tick", Util.numFormat(maxExtract)).withStyle(ChatFormatting.DARK_GRAY)));
        }
    }

    @Override
    public boolean allowComponentsUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return !ItemStack.matchesIgnoringComponents(oldStack, newStack, dct -> dct == PowahComponents.ENERGY_STORED);
    }
}
