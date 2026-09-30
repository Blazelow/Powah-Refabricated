package owmii.powah.util;

import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ValueIOUtil {
    public static <T extends Collection<BlockPos>> T readPosList(ValueInput input, String key, T list) {
        var childList = input.childrenListOrEmpty(key);
        for (var child : childList) {
            list.add(readPos(child));
        }
        return list;
    }

    public static void writePosList(ValueOutput output, Collection<BlockPos> list, String key) {
        var outputList = output.childrenList(key);
        for (var pos : list) {
            writePos(outputList.addChild(), pos);
        }
    }

    public static BlockPos readPos(ValueInput input, String key) {
        return readPos(input.childOrEmpty(key));
    }

    public static void writePos(ValueOutput output, BlockPos pos, String key) {
        writePos(output.child(key), pos);
    }

    public static BlockPos readPos(ValueInput input) {
        var x = input.getIntOr("x", 0);
        var y = input.getIntOr("y", 0);
        var z = input.getIntOr("z", 0);
        if (x == 0 && y == 0 && z == 0) {
            return BlockPos.ZERO;
        }
        return new BlockPos(x, y, z);
    }

    public static void writePos(ValueOutput output, BlockPos pos) {
        output.putInt("x", pos.getX());
        output.putInt("y", pos.getY());
        output.putInt("z", pos.getZ());
    }
}
