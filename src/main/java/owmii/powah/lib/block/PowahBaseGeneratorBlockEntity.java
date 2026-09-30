package owmii.powah.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import owmii.powah.lib.logistics.Transfer;

public class PowahBaseGeneratorBlockEntity<B extends PowahBaseGeneratorBlock<B>>
        extends PowahBaseEnergyStorageBlockEntity<B> {
    public PowahBaseGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public final long getEnergyGeneration() {
        return getBlock().getEnergyGeneration();
    }

    @Override
    public Transfer getTransferType() {
        return Transfer.EXTRACT;
    }
}
