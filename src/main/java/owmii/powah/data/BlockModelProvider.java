package owmii.powah.data;

import static net.minecraft.client.data.models.BlockModelGenerators.NOP;
import static net.minecraft.client.data.models.BlockModelGenerators.ROTATION_FACING;
import static net.minecraft.client.data.models.BlockModelGenerators.ROTATION_HORIZONTAL_FACING;
import static net.minecraft.client.data.models.BlockModelGenerators.X_ROT_180;
import static net.minecraft.client.data.models.BlockModelGenerators.X_ROT_270;
import static net.minecraft.client.data.models.BlockModelGenerators.X_ROT_90;
import static net.minecraft.client.data.models.BlockModelGenerators.Y_ROT_180;
import static net.minecraft.client.data.models.BlockModelGenerators.Y_ROT_270;
import static net.minecraft.client.data.models.BlockModelGenerators.Y_ROT_90;
import static net.minecraft.client.data.models.BlockModelGenerators.createBooleanModelDispatch;
import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import owmii.powah.Powah;
import owmii.powah.block.Blcks;
import owmii.powah.block.cable.CableBlock;
import owmii.powah.block.solar.SolarBlock;
import owmii.powah.block.transmitter.PlayerTransmitterBlock;
import owmii.powah.lib.registry.TieredBlockReg;

public class BlockModelProvider extends ModelSubProvider {
    public BlockModelProvider(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        super(blockModels, itemModels);
    }

    @Override
    protected void register() {

        simpleBlockAndItem(Blcks.ENERGY_CELL);
        simpleBlockAndItem(Blcks.ENDER_CELL);
        Blcks.ENERGY_CABLE.getAll().forEach(this::energyCable);
        Blcks.ENDER_GATE.getAll().forEach(this::simpleFacingBlockAndItem);
        Blcks.ENERGIZING_ROD.getAll().forEach(this::simpleDownFacingBlockAndItem);
        Blcks.FURNATOR.getAll().forEach(this::simpleFacingBlockAndItem);
        Blcks.MAGMATOR.getAll().forEach(this::magmator);
        simpleBlockAndItem(Blcks.THERMO_GENERATOR);
        Blcks.SOLAR_PANEL.getAll().forEach(this::solarPanel);
        simpleBlockAndItem(Blcks.REACTOR);
        Blcks.PLAYER_TRANSMITTER.getAll().forEach(this::playerTransmitter);
        Blcks.ENERGY_HOPPER.getAll().forEach(this::simpleFacingBlockAndItem);
        Blcks.ENERGY_DISCHARGER.getAll().forEach(this::simpleFacingBlockAndItem);

        simpleDownFacingBlockAndItem(Blcks.ENERGIZING_ORB.get());
        simpleBlockAndItem(Blcks.ENERGIZED_STEEL.get());
        simpleBlockAndItem(Blcks.BLAZING_CRYSTAL.get());
        simpleBlockAndItem(Blcks.NIOTIC_CRYSTAL.get());
        simpleBlockAndItem(Blcks.SPIRITED_CRYSTAL.get());
        simpleBlockAndItem(Blcks.NITRO_CRYSTAL.get());
        simpleBlockAndItem(Blcks.URANINITE.get());
        simpleBlockAndItem(Blcks.DEEPSLATE_URANINITE_ORE_POOR.get());
        simpleBlockAndItem(Blcks.DEEPSLATE_URANINITE_ORE.get());
        simpleBlockAndItem(Blcks.DEEPSLATE_URANINITE_ORE_DENSE.get());
        simpleBlockAndItem(Blcks.URANINITE_ORE_POOR.get());
        simpleBlockAndItem(Blcks.URANINITE_ORE.get());
        simpleBlockAndItem(Blcks.URANINITE_ORE_DENSE.get());
        simpleBlockAndItem(Blcks.DRY_ICE.get());
    }

    private void simpleFacingBlockAndItem(Block block) {
        var base = plainVariant(ModelLocationUtils.getModelLocation(block));
        if (block.getStateDefinition().getProperties().contains(BlockStateProperties.FACING)) {
            blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, base).with(ROTATION_FACING));
        } else if (block.getStateDefinition().getProperties().contains(BlockStateProperties.HORIZONTAL_FACING)) {
            blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, base).with(ROTATION_HORIZONTAL_FACING));
        } else {
            throw new IllegalStateException("Block must either have FACING or HORIZONTAL_FACING");
        }
    }

    private void simpleDownFacingBlockAndItem(Block block) {
        var base = plainVariant(ModelLocationUtils.getModelLocation(block));
        var rotationDispatch = PropertyDispatch.modify(BlockStateProperties.FACING)
                .select(Direction.DOWN, NOP)
                .select(Direction.UP, X_ROT_180)
                .select(Direction.NORTH, X_ROT_90.then(Y_ROT_180))
                .select(Direction.SOUTH, X_ROT_90)
                .select(Direction.WEST, X_ROT_90.then(Y_ROT_90))
                .select(Direction.EAST, X_ROT_90.then(Y_ROT_270));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, base)
                .with(rotationDispatch));
    }

    private void energyCable(Block block) {
        var baseModel = ModelLocationUtils.getModelLocation(block);
        var multipartModel = ModelLocationUtils.getModelLocation(block, "_multipart");

        blockStateOutput.accept(
                MultiPartGenerator.multiPart(block)
                        .with(plainVariant(baseModel))
                        .with(new ConditionBuilder().term(CableBlock.NORTH, true), plainVariant(multipartModel))
                        .with(new ConditionBuilder().term(CableBlock.EAST, true), plainVariant(multipartModel).with(Y_ROT_90))
                        .with(new ConditionBuilder().term(CableBlock.SOUTH, true), plainVariant(multipartModel).with(X_ROT_180))
                        .with(new ConditionBuilder().term(CableBlock.WEST, true), plainVariant(multipartModel).with(Y_ROT_270))
                        .with(new ConditionBuilder().term(CableBlock.UP, true), plainVariant(multipartModel).with(X_ROT_270))
                        .with(new ConditionBuilder().term(CableBlock.DOWN, true), plainVariant(multipartModel).with(X_ROT_90)));
    }

    private void magmator(Block block) {
        var normalModel = plainVariant(ModelLocationUtils.getModelLocation(block));
        var litModel = plainVariant(ModelLocationUtils.getModelLocation(block, "_on"));

        blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(createBooleanModelDispatch(BlockStateProperties.LIT, litModel, normalModel))
                        .with(ROTATION_HORIZONTAL_FACING));
    }

    private void playerTransmitter(Block block) {
        var normalModel = plainVariant(ModelLocationUtils.getModelLocation(block));
        var topOn = plainVariant(ModelLocationUtils.getModelLocation(block, "_top"));

        blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(createBooleanModelDispatch(PlayerTransmitterBlock.TOP, topOn, normalModel)));
    }

    private void solarPanel(Block block) {
        var baseModel = ModelLocationUtils.getModelLocation(block);
        var frameModel = Powah.id("block/solar_panel_frame_");

        blockStateOutput.accept(
                MultiPartGenerator.multiPart(block)
                        .with(plainVariant(baseModel))
                        .with(new ConditionBuilder().term(SolarBlock.NORTH, true), plainVariant(frameModel))
                        .with(new ConditionBuilder().term(SolarBlock.EAST, true), plainVariant(frameModel).with(Y_ROT_90))
                        .with(new ConditionBuilder().term(SolarBlock.SOUTH, true), plainVariant(frameModel).with(Y_ROT_180))
                        .with(new ConditionBuilder().term(SolarBlock.WEST, true), plainVariant(frameModel).with(Y_ROT_270)));
    }

    private void simpleBlockAndItem(TieredBlockReg tieredBlock) {
        for (var block : tieredBlock.getAll()) {
            simpleBlockAndItem(block);
        }
    }
}
