package owmii.powah.lib.block;

import java.util.function.Consumer;
import java.util.function.LongSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jspecify.annotations.Nullable;
import owmii.powah.api.energy.IEnergyConnector;
import owmii.powah.block.Tier;
import owmii.powah.lib.client.util.Text;
import owmii.powah.lib.item.EnergyBlockItem;
import owmii.powah.lib.item.IEnergyItemProvider;
import owmii.powah.lib.logistics.Transfer;
import owmii.powah.util.EnergyUtil;
import owmii.powah.util.Util;

public abstract class PowahBaseEnergyBlock<B extends PowahBaseEnergyBlock<B>> extends PowahBaseBlock<B> implements IEnergyItemProvider {

    private final Tier tier;
    private final LongSupplier capacitySupplier;
    private final LongSupplier transferSupplier;

    public PowahBaseEnergyBlock(Properties properties, Tier tier, LongSupplier capacitySupplier, LongSupplier transferSupplier) {
        super(properties);
        this.tier = tier;
        this.capacitySupplier = capacitySupplier;
        this.transferSupplier = transferSupplier;
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    @SuppressWarnings("unchecked")
    public EnergyBlockItem<B> getBlockItem(Item.Properties properties, @Nullable ResourceKey<CreativeModeTab> group) {
        return new EnergyBlockItem<>((B) this, properties, group);
    }

    public final long getEnergyCapacity() {
        return capacitySupplier.getAsLong();
    }

    public final long getEnergyTransfer() {
        return transferSupplier.getAsLong();
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction side) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof PowahBaseEnergyStorageBlockEntity) {
            return ((PowahBaseEnergyStorageBlockEntity<B>) tile).getEnergy().toComparatorPower();
        }
        return super.getAnalogOutputSignal(state, world, pos, side);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (checkValidEnergySideProperty() && getFacing() != Facing.NONE) {
            var property = getFacing() == Facing.ALL ? BlockStateProperties.FACING : BlockStateProperties.HORIZONTAL_FACING;
            Direction side = state.getValue(property);
            BlockPos pos1 = pos.relative(side);
            return world.getBlockState(pos1).getBlock() instanceof IEnergyConnector ||
                    world instanceof Level level && EnergyUtil.hasEnergy(level, pos1, side.getOpposite());
        }
        return super.canSurvive(state, world, pos);
    }

    protected boolean checkValidEnergySideProperty() {
        return false;
    }

    @Override
    public boolean isChargeable(ItemStack stack) {
        return getTransferType().canReceive;
    }

    public Transfer getTransferType() {
        return Transfer.ALL;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder,
            TooltipFlag tooltipFlag) {
        var energy = ItemAccess.forStack(itemStack).getCapability(Capabilities.Energy.ITEM);
        if (energy != null) {
            addEnergyInfo(itemStack, energy, builder);
            addEnergyTransferInfo(itemStack, energy, builder);
            additionalEnergyInfo(itemStack, energy, builder);
        }
    }

    public void addEnergyInfo(ItemStack stack, EnergyHandler storage, Consumer<Component> tooltip) {
        if (storage.getCapacityAsLong() > 0)
            tooltip.accept(Component.translatable("info.lollipop.stored").withStyle(ChatFormatting.GRAY).append(Text.COLON)
                    .append(Component
                            .translatable("info.lollipop.fe.stored", Util.addCommas(storage.getAmountAsLong()),
                                    Util.numFormat(storage.getCapacityAsLong()))
                            .withStyle(ChatFormatting.DARK_GRAY)));
    }

    public void addEnergyTransferInfo(ItemStack stack, EnergyHandler storage, Consumer<Component> tooltip) {
        long ext = getTransferType().canExtract ? getEnergyTransfer() : 0;
        long re = getTransferType().canReceive ? getEnergyTransfer() : 0;
        if (ext + re > 0) {
            if (ext == re) {
                tooltip.accept(Component.translatable("info.lollipop.max.io").withStyle(ChatFormatting.GRAY).append(Text.COLON)
                        .append(Component.translatable("info.lollipop.fe.pet.tick", Util.numFormat(ext))
                                .withStyle(ChatFormatting.DARK_GRAY)));
            } else {
                if (ext > 0)
                    tooltip.accept(Component.translatable("info.lollipop.max.extract").withStyle(ChatFormatting.GRAY).append(Text.COLON)
                            .append(Component.translatable("info.lollipop.fe.pet.tick", Util.numFormat(ext))
                                    .withStyle(ChatFormatting.DARK_GRAY)));
                if (re > 0)
                    tooltip.accept(Component.translatable("info.lollipop.max.receive").withStyle(ChatFormatting.GRAY).append(Text.COLON)
                            .append(Component.translatable("info.lollipop.fe.pet.tick", Util.numFormat(re))
                                    .withStyle(ChatFormatting.DARK_GRAY)));
            }
        }
    }

    public void additionalEnergyInfo(ItemStack stack, EnergyHandler energy, Consumer<Component> tooltip) {
    }
}
