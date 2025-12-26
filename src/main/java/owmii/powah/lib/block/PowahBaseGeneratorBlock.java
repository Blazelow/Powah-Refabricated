package owmii.powah.lib.block;

import java.util.function.Consumer;
import java.util.function.LongSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import owmii.powah.block.Tier;
import owmii.powah.config.v2.types.GeneratorConfig;
import owmii.powah.lib.client.util.Text;
import owmii.powah.lib.logistics.Transfer;
import owmii.powah.util.Util;

public abstract class PowahBaseGeneratorBlock<B extends PowahBaseGeneratorBlock<B>> extends PowahBaseEnergyBlock<B> {
    private final LongSupplier generationSupplier;

    public PowahBaseGeneratorBlock(Properties properties, Tier tier, LongSupplier capacitySupplier, LongSupplier transferSupplier,
            LongSupplier generationSupplier) {
        super(properties, tier, capacitySupplier, transferSupplier);
        this.generationSupplier = generationSupplier;
    }

    public PowahBaseGeneratorBlock(Properties properties, GeneratorConfig config, Tier tier) {
        this(
                properties,
                tier,
                () -> config.getCapacity(tier),
                () -> config.getTransfer(tier),
                () -> config.getGeneration(tier));
    }

    public final long getEnergyGeneration() {
        return generationSupplier.getAsLong();
    }

    @Override
    public void additionalEnergyInfo(ItemStack stack, EnergyHandler energy, Consumer<Component> tooltip) {
        tooltip.accept(Component.translatable("info.lollipop.generates").withStyle(ChatFormatting.GRAY).append(Text.COLON)
                .append(Component.translatable("info.lollipop.fe.pet.tick", Util.numFormat(getEnergyGeneration()))
                        .withStyle(ChatFormatting.DARK_GRAY)));
    }

    @Override
    public Transfer getTransferType() {
        return Transfer.EXTRACT;
    }
}
