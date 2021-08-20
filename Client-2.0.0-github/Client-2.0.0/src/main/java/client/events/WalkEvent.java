package  client.events;

public final class WalkEvent extends EventProcessor {
    private double motionX;
    private double motionY;
    private double motionZ;

    public WalkEvent(final double motionX, final double motionY, final double motionZ) {
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    public final double getMotionX() {
        return this.motionX;
    }

    public final double getMotionY() {
        return this.motionY;
    }

    public final double getMotionZ() {
        return this.motionZ;
    }

    public void setMotionX(final double motionX) {
        this.motionX = motionX;
    }

    public void setMotionY(final double motionY) {
        this.motionY = motionY;
    }

    public void setMotionZ(final double motionZ) {
        this.motionZ = motionZ;
    }
}