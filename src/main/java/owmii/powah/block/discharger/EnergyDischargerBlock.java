package owmii.powah.block.discharger;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;
import owmii.powah.Powah;
import owmii.powah.block.Tier;
import owmii.powah.inventory.DischargerMenu;
import owmii.powah.lib.block.PowahBaseBlockEntity;
import owmii.powah.lib.block.PowahBaseEnergyBlock;
import owmii.powah.lib.item.EnergyBlockItem;
import owmii.powah.lib.logistics.Transfer;
import owmii.powah.lib.logistics.inventory.BaseMenu;

public class EnergyDischargerBlock extends PowahBaseEnergyBlock<EnergyDischargerBlock> {
    public EnergyDischargerBlock(Properties properties, Tier tier) {
        var config = Powah.config().devices.dischargers;
        super(properties, tier, () -> config.getTransfer(tier), () -> config.getCapacity(tier));
    }

    @Override
    public EnergyBlockItem getBlockItem(Item.Properties properties, @Nullable ResourceKey<CreativeModeTab> group) {
        return super.getBlockItem(properties.stacksTo(1), group);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyDischargerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends PowahBaseBlockEntity> BaseMenu getContainer(int id, Inventory inventory, PowahBaseBlockEntity te,
            BlockHitResult result) {
        if (te instanceof EnergyDischargerBlockEntity) {
            return new DischargerMenu(id, inventory, (EnergyDischargerBlockEntity) te);
        }
        return null;
    }

    @Override
    public Transfer getTransferType() {
        return Transfer.EXTRACT;
    }

    @Override
    protected Facing getFacing() {
        return Facing.HORIZONTAL;
    }
}
