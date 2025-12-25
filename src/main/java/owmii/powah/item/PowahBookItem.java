package owmii.powah.item;

import guideme.GuidesCommon;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import owmii.powah.Powah;
import owmii.powah.lib.item.PowahBaseItem;

public class PowahBookItem extends PowahBaseItem {
    public static final Identifier GUIDE_ID = Powah.id("book");

    public PowahBookItem(Properties properties) {
        super(properties.rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        GuidesCommon.openGuide(playerIn, GUIDE_ID);
        return InteractionResult.SUCCESS;
    }
}
