package manager;

import java.util.*;
import entity.Notification;

/**
 * Manages all notifications for different users using the Singleton design pattern.
 * <p>
 * This class stores a collection of notifications for each user, allowing for
 * sending, retrieval, and status management (read/unread).
 * </p>
 */
public class NotificationManager {

    private static NotificationManager instance;

    /**
     * Stores notifications where the key is the user ID and the value is a list of
     * that user's notifications (inbox).
     */
    private HashMap<String, ArrayList<Notification>> userNotifications;

    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes the internal HashMap.
     */
    public NotificationManager() {
        this.userNotifications = new HashMap<>();
    }

    /**
     * Gets the single instance of the NotificationManager.
     *
     * @return The single instance of NotificationManager.
     */
    public static NotificationManager getInstance() {
        if (instance == null) instance = new NotificationManager();
        return instance;
    }


    /**
     * Creates and sends a new, unread notification to the specified user.
     *
     * @param userId The ID of the user to receive the notification.
     * @param message The content of the notification message.
     */
    public void sendNotification(String userId, String message) {
        ArrayList<Notification> inbox =
                userNotifications.computeIfAbsent(userId, k -> new ArrayList<>());

        inbox.add(new Notification(message));
    }

    /**
     * Retrieves all notifications for a user, sorted with unread notifications appearing
     * first, and then by timestamp (newest first within each read status group).
     *
     * @param userId The ID of the user whose notifications are to be retrieved.
     * @return A sorted list of {@link Notification} objects. Returns an empty list
     * if the user has no notifications.
     */
    public List<Notification> getNotifications(String userId) {
        List<Notification> inbox = userNotifications.getOrDefault(userId, new ArrayList<>());
    
        List<Notification> sorted = new ArrayList<>(inbox);
    
        sorted.sort((a, b) -> {
    
            // unread first
            if (a.isRead() && !b.isRead()) return 1;
            if (!a.isRead() && b.isRead()) return -1;
    
            // then sort by timestamp (newest first)
            return b.getTimestamp().compareTo(a.getTimestamp());
        });
        return sorted;
    }
    

    /**
     * Retrieves only the unread notifications for a specified user.
     *
     * @param userId The ID of the user.
     * @return A list of unread {@link Notification} objects. Returns an empty list
     * if the user has no unread notifications.
     */
    public List<Notification> getUnreadNotifications(String userId) {
        List<Notification> inbox = userNotifications.get(userId);
        if (inbox == null) return new ArrayList<>();

        List<Notification> unread = new ArrayList<>();
        for (Notification n : inbox) {
            if (!n.isRead()) unread.add(n);
        }
        return unread;
    }

   /**
     * Marks all notifications in the user's inbox as read.
     *
     * @param userId The ID of the user whose notifications should be marked as read.
     */
    public void markAllAsRead(String userId) {
        List<Notification> inbox = userNotifications.get(userId);
        if (inbox == null) return;

        for (Notification n : inbox) {
            n.markAsRead();
        }
    }

    /**
     * Removes all notifications associated with the specified user.
     *
     * @param userId The ID of the user whose notifications are to be cleared.
     */
    public void clearNotifications(String userId) {
        userNotifications.remove(userId);
    }

    /**
     * Checks if the specified user has any unread notifications.
     *
     * @param userId The ID of the user.
     * @return {@code true} if the user has at least one unread notification, {@code false} otherwise.
     */
    public boolean hasUnread(String userId) {
        List<Notification> inbox = userNotifications.get(userId);
        if (inbox == null) return false;

        for (Notification n : inbox) {
            if (!n.isRead()) return true;
        }
        return false;
    }
}
