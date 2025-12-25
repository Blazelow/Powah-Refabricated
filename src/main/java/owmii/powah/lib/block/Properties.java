package owmii.powah.lib.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class Properties {

    public static Block.Properties rock(Block.Properties props, float hardness, float resistance) {
        return props
                .mapColor(MapColor.STONE)
                .sound(SoundType.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(hardness, resistance).requiresCorrectToolForDrops();
    }

    public static Block.Properties deepslate(Block.Properties props) {
        return props
                .mapColor(MapColor.DEEPSLATE)
                .sound(SoundType.DEEPSLATE)
                .strength(4.5f, 3.0f)
                .requiresCorrectToolForDrops();
    }

    public static Block.Properties wood(Block.Properties props, float hardness, float resistance) {
        return props
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .ignitedByLava()
                .instrument(NoteBlockInstrument.BASS)
                .strength(hardness, resistance);
    }

    public static Block.Properties metal(Block.Properties props, float hardness, float resistance) {
        return props
                .mapColor(MapColor.METAL)
                .sound(SoundType.METAL)
                .strength(hardness, resistance)
                .requiresCorrectToolForDrops();
    }

    public static Block.Properties metalNoSolid(Block.Properties props, float hardness, float resistance) {
        return metal(props, hardness, resistance).noOcclusion();
    }
}
