package owmii.powah.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import owmii.powah.block.Tier;
import owmii.powah.config.v2.types.GeneratorConfig;
import owmii.powah.lib.logistics.Transfer;

public class PowahBaseGeneratorBlockEntity<B extends PowahBaseEnergyBlock<GeneratorConfig, B>> extends PowahBaseEnergyStorageBlockEntity<GeneratorConfig, B> {
    public PowahBaseGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public PowahBaseGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Tier variant) {
        super(type, pos, state, variant);
    }

    public long getGeneration() {
        return getBlock().getConfig().generation_rates.get(getVariant());
    }

    @Override
    public Transfer getTransferType() {
        return Transfer.EXTRACT;
    }
}
