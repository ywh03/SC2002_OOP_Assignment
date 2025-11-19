package entity;

import java.io.Serializable;  // for Serializable
import java.util.Date;        // for timestamp

public class Notification implements Serializable {

    private final String message;
    private final Date timestamp;
    private boolean read;

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
