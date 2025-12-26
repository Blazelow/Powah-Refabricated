package owmii.powah.block.transmitter;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import owmii.powah.block.Tiles;
import owmii.powah.components.PowahComponents;
import owmii.powah.item.BindingCardItem;
import owmii.powah.lib.block.IInventoryHolder;
import owmii.powah.lib.block.PowahBaseEnergyStorageBlockEntity;
import owmii.powah.util.ChargeUtil;

public class PlayerTransmitterBlockEntity extends PowahBaseEnergyStorageBlockEntity<PlayerTransmitterBlock>
        implements IInventoryHolder {

    public PlayerTransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(Tiles.PLAYER_TRANSMITTER.get(), pos, state);
    }

    @Override
    protected int getInternalInventorySize() {
        return 1;
    }

    @Override
    protected int postTick(Level world) {
        long extracted = 0;
        if (world instanceof ServerLevel serverLevel && checkRedstone()) {
            ItemStack stack = this.inv.getStackInSlot(0);
            if (stack.getItem() instanceof BindingCardItem card) {
                Optional<ServerPlayer> op = card.getPlayer(serverLevel, stack);
                if (op.isPresent()) {
                    ServerPlayer player = op.get();
                    if (card.isMultiDim(stack) || player.level() == world) {
                        long charging = getBlock().getChargingSpeed();
                        try (var tx = Transaction.openRoot()) {
                            extracted = ChargeUtil.chargeItemsInPlayerInv(player, charging, getEnergy().getStored(), tx);
                            energy.extractEnergy(extracted, tx);
                            tx.commit();
                        }
                    }
                }
            }
        }
        return extracted > 0 ? 5 : -1;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack) {
        return stack.has(PowahComponents.BOUND_PLAYER);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public boolean keepEnergy() {
        return true;
    }
}
