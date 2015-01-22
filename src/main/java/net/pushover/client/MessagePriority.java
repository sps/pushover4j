package net.pushover.client;

/**
 * Enumeration to represent the priority of a message
 */
public enum MessagePriority {

    LOWEST(-2), LOW(-1), QUIET(-1), NORMAL(0), HIGH(1), EMERGENCY(2);

    MessagePriority(int priority) {
        this.priority = priority;
    }

    private final int priority;

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return String.valueOf(this.priority);
    }
}
