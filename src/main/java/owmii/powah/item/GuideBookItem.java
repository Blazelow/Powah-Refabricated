package owmii.powah.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import owmii.powah.client.guide.GuideBookClient;
import owmii.powah.lib.item.ItemBase;

public class GuideBookItem extends ItemBase {
    public GuideBookItem(Properties properties) {
        super(properties.rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            GuideBookClient.open();
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
