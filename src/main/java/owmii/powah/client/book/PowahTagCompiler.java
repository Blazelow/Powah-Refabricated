package owmii.powah.client.book;

import guideme.compiler.PageCompiler;
import guideme.compiler.TagCompiler;
import guideme.compiler.tags.MdxAttrs;
import guideme.document.flow.LytFlowParent;
import guideme.libs.mdast.mdx.model.MdxJsxTextElement;
import java.util.Set;
import net.minecraft.network.chat.Component;
import owmii.powah.lib.block.PowahBaseGeneratorBlock;
import owmii.powah.lib.item.EnergyBlockItem;
import owmii.powah.lib.item.EnergyItem;
import owmii.powah.lib.item.PowahBlockItem;
import owmii.powah.util.Util;

public class PowahTagCompiler implements TagCompiler {
    @Override
    public Set<String> getTagNames() {
        return Set.of("powah:EnergyCapacity", "powah:EnergyMaxIO", "powah:EnergyGeneration");
    }

    @Override
    public void compileFlowContext(PageCompiler compiler, LytFlowParent parent, MdxJsxTextElement el) {
        var item = MdxAttrs.getRequiredItem(compiler, parent, el, "id");
        if (item == null) {
            return;
        }

        switch (el.name()) {
        case "powah:EnergyCapacity" -> {
            long capacity = 0L;
            if (item instanceof EnergyItem energyItem) {
                capacity = energyItem.getEnergyInfo().capacity();
            } else if (item instanceof EnergyBlockItem<?> energyBlockItem) {
                capacity = energyBlockItem.getBlock().getEnergyCapacity();
            }
            parent.appendComponent(Component.translatable("info.lollipop.fe", Util.addCommas(capacity)));
        }
        case "powah:EnergyMaxIO" -> {
            long maxIo = 0L;
            if (item instanceof EnergyBlockItem<?> energyBlockItem) {
                maxIo = energyBlockItem.getBlock().getEnergyTransfer();
            } else if (item instanceof EnergyItem energyItem) {
                maxIo = Math.max(energyItem.getEnergyInfo().maxExtract(), energyItem.getEnergyInfo().maxExtract());
            }
            parent.appendComponent(Component.translatable("info.lollipop.fe.pet.tick", Util.addCommas(maxIo)));
        }
        case "powah:EnergyGeneration" -> {
            long generation = 0L;
            if (item instanceof PowahBlockItem<?> blockItem && blockItem.getBlock() instanceof PowahBaseGeneratorBlock<?> generatorBlock) {
                generation = generatorBlock.getEnergyGeneration();
            }
            parent.appendComponent(Component.translatable("info.lollipop.fe.pet.tick", Util.addCommas(generation)));
        }
        default -> throw new IllegalStateException("Unexpected value: " + el.name());
        }
    }
}
