package owmii.powah.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import owmii.powah.client.guide.GuideBookClient;
import owmii.powah.lib.item.PowahBaseItem;

public class GuideBookItem extends PowahBaseItem {
    public GuideBookItem(Properties properties) {
        super(properties.rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            GuideBookClient.open();
        }
        return InteractionResult.SUCCESS;
    }
}
