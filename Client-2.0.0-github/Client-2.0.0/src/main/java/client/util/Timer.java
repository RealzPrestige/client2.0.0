package client.util;


import net.minecraft.client.Minecraft;

public class Timer implements Util{
    private long time = -1L;
    public static final String tickLength = isObfuscated() ? "field_194149_e" : "tickLength";
    public static final String timer = isObfuscated() ? "field_71428_T" : "timer";

    private long currentMS = 0L;
    private long lastMS = -1L;
    public void setCurrentMS() {
        currentMS = System.nanoTime() / 1000000;
    }

    public boolean hasDelayRun(long time) {
        return (currentMS - lastMS) >= time;
    }

    public void setLastMS() {
        lastMS = System.nanoTime() / 1000000;
    }

    public boolean passedS(double s) {
        return this.passedMs((long) s * 1000L);
    }

    public boolean passedDms(double dms) {
        return this.passedMs((long) dms * 10L);
    }

    public boolean passedDs(double ds) {
        return this.passedMs((long) ds * 100L);
    }

    public boolean passedMs(long ms) {
        return this.passedNS(this.convertToNS(ms));
    }

    public void setMs(long ms) {
        this.time = System.nanoTime() - this.convertToNS(ms);
    }

    public boolean passedNS(long ns) {
        return System.nanoTime() - this.time >= ns;
    }

    public long getPassedTimeMs() {
        return this.getMs(System.nanoTime() - this.time);
    }


    public long getMs(long time) {
        return time / 1000000L;
    }

    public long convertToNS(long time) {
        return time * 1000000L;
    }
    public static boolean isObfuscated() {
        try {
            return Minecraft.class.getDeclaredField("instance") == null;
        } catch (Exception var1) {
            return true;
        }
    }
    private long current;

    public Timer() {
        current = -1;
    }

    public final boolean hasReached(final long delay) {
        return System.currentTimeMillis() - this.current >= delay;
    }

    public boolean hasReached(final long delay, boolean reset) {
        if (reset)
            reset();
        return System.currentTimeMillis() - this.current >= delay;
    }


    public long time() {
        return System.nanoTime() / 1000000L - current;
    }
    public Timer reset() {
        this.time = System.nanoTime();
        return this;
    }
    public boolean passed(final long time, final Format format) {
        switch (format) {
            default: {
                return this.getMS(System.nanoTime() - this.time) >= time;
            }
            case TICKS: {
                return mc.player.ticksExisted % (int)time == 0;
            }
        }
    }
    public enum Format
    {
        SYSTEM,
        TICKS;
    }
    public long getMS(final long time) {
        return time / 1000000L;
    }

    public boolean passed(final long delay) {
        return System.currentTimeMillis() - this.current >= delay;
    }
}

