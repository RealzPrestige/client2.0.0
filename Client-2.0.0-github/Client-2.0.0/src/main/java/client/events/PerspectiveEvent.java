package client.events;


public class PerspectiveEvent extends EventProcessor {
    private float aspect;

    public PerspectiveEvent(final float aspect) {
        this.aspect = aspect;
    }

    public float getAspect() {
        return this.aspect;
    }

    public void setAspect(final float aspect) {
        this.aspect = aspect;
    }
}