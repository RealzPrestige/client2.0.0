package client.events;


public class NewPopEvent extends EventProcessor {
    private final String name;
    private final int popCount;
    private final int entId;

    public NewPopEvent(final String name, final int count, final int entId) {
        this.name = name;
        this.popCount = count;
        this.entId = entId;
    }

    public String getName() {
        return this.name;
    }

    public int getPopCount() {
        return this.popCount;
    }

    public int getEntityId() {
        return this.entId;
    }
}
