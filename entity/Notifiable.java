package entity;

import java.util.List;

/**
 * Represents an entity that is capable of receiving and managing notifications.
 * <p>
 * Classes implementing this interface must provide methods to accept a new
 * notification and to retrieve their current list of notifications.
 * </p>
 */
public interface Notifiable {
    /**
     * Processes and stores a new notification for the entity.
     *
     * @param notification The {@link Notification} object to be received.
     */
    void receiveNotification(Notification notification);
    
    /**
     * Retrieves the current list of notifications for the entity.
     *
     * @return A {@code List} of {@link Notification} objects.
     */
    List<Notification> getNotifications();
}
