package owmii.powah.lib.logistics.energy;

import static owmii.powah.lib.logistics.Transfer.ALL;
import static owmii.powah.lib.logistics.Transfer.NONE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import owmii.powah.lib.block.PowahBaseEnergyStorageBlockEntity;
import owmii.powah.lib.logistics.Transfer;

public class SideConfig {
    private final Transfer[] transfers = new Transfer[6];
    private final PowahBaseEnergyStorageBlockEntity storage;
    private boolean isSetFromNBT;

    public SideConfig(PowahBaseEnergyStorageBlockEntity storage) {
        this.storage = storage;
        Arrays.fill(this.transfers, NONE);
    }

    public void init() {
        if (!this.isSetFromNBT) {
            for (Direction side : Direction.values()) {
                setType(side, this.storage.getTransferType());
            }
        }
    }

    public void read(ValueInput input) {
        input.getIntArray("side_transfer_type").ifPresent(arr -> {
            for (int i = 0; i < arr.length; i++) {
                this.transfers[i] = Transfer.values()[arr[i]];
            }
            this.isSetFromNBT = true;
        });
    }

    public void write(ValueOutput output) {
        int[] list = new int[this.transfers.length];
        for (int i = 0; i < this.transfers.length; i++) {
            list[i] = this.transfers[i].ordinal();
        }
        output.putIntArray("side_transfer_type", list);
    }

    public void nextTypeAll() {
        if (isAllEquals()) {
            for (Direction side : Direction.values()) {
                nextType(side);
            }
        } else {
            for (Direction side : Direction.values()) {
                setType(side, ALL);
            }
        }
    }

    public boolean isAllEquals() {
        boolean flag = true;
        int first = -1;
        for (int i = 1; i < 6; i++) {
            if (this.storage.isEnergyPresent(Direction.from3DDataValue(i))) {
                if (first < 0) {
                    first = this.transfers[i].ordinal();
                } else if (this.transfers[i].ordinal() != first) {
                    flag = false;
                }
            }
        }
        return flag;
    }

    public void nextType(@Nullable Direction side) {
        setType(side, getType(side).next(this.storage.getTransferType()));
    }

    public Transfer getType(@Nullable Direction side) {
        if (side != null) {
            return this.transfers[side.get3DDataValue()];
        }
        return NONE;
    }

    public void setType(@Nullable Direction side, Transfer type) {
        if (side == null || this.storage.getTransferType().equals(NONE))
            return;
        if (!this.storage.isEnergyPresent(side))
            return;
        this.transfers[side.get3DDataValue()] = type;
    }
}
