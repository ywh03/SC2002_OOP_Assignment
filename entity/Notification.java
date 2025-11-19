package entity;

import java.io.Serializable;  // for Serializable
import java.util.Date;        // for timestamp

/**
 * Represents a single notification event.
 * <p>
 * A notification contains a message, a timestamp of creation, and a read status.
 * It is immutable once created, except for the read status.
 * </p>
 */
public class Notification implements Serializable {

    private final String message;
    private final Date timestamp;
    private boolean read;

    /**
     * Constructs a new Notification with the given message.
     * The timestamp is set to the current date and time, and the status is set to unread.
     *
     * @param message The content of the notification.
     */
    public Notification(String message) {
        this.message = message;
        this.timestamp = new Date();
        this.read = false;  // default = unread
    }

    public String getMessage() { return message; }

    public Date getTimestamp() { return timestamp; }

    public boolean isRead() { return read; }

    public void markAsRead() { this.read = true; }
}
