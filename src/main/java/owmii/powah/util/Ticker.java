package owmii.powah.util;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class Ticker {
    private double maxTicks;
    private double ticks;

    public Ticker(double max) {
        this.maxTicks = max;
    }

    public static Ticker empty() {
        return new Ticker(0);
    }

    public boolean isEmpty() {
        return this.ticks <= 0;
    }

    public boolean ended() {
        return this.ticks >= this.maxTicks;
    }

    public void add(double ticks) {
        this.ticks = Math.min(Math.max(0, this.ticks + ticks), this.maxTicks);
    }

    public void onward() {
        if (this.ticks < this.maxTicks) {
            this.ticks++;
        }
    }

    public void back() {
        if (this.ticks > 0) {
            this.ticks--;
        }
    }

    public void back(double value) {
        if (this.ticks > 0) {
            this.ticks -= Math.min(this.ticks, value);
        }
    }

    public void reset() {
        this.ticks = 0;
    }

    public static boolean delayed(double delay) {
        return System.currentTimeMillis() % (delay * 5) == 0;
    }

    public void read(ValueInput input, String key) {
        this.ticks = input.getDoubleOr(key + "_ticks", 0);
        this.maxTicks = input.getDoubleOr(key + "_max_ticks", 0);
    }

    public void write(ValueOutput output, String key) {
        output.putDouble(key + "_ticks", this.ticks);
        output.putDouble(key + "_max_ticks", this.maxTicks);
    }

    public double getMax() {
        return this.maxTicks;
    }

    public void setMax(double max) {
        this.maxTicks = max;
    }

    public double getTicks() {
        return this.ticks;
    }

    public void setTicks(double ticks) {
        this.ticks = ticks;
    }

    public void setAll(double ticks) {
        this.maxTicks = ticks;
        this.ticks = ticks;
    }

    public double getEmpty() {
        return this.maxTicks - this.ticks;
    }

    public double perCent() {
        return this.maxTicks > 0 ? this.ticks * 100.0D / this.maxTicks : 0;
    }

    public float subSized() {
        return this.maxTicks > 0 ? (float) (this.ticks / this.maxTicks) : 0;
    }
}
