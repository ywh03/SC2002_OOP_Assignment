package manager;

import java.util.*;
import entity.Notification;

public class NotificationManager {

    private static NotificationManager instance;

    // userId → inbox (list of notifications)
    private HashMap<String, ArrayList<Notification>> userNotifications;

    private NotificationManager() {
        this.userNotifications = new HashMap<>();
    }

    public static NotificationManager getInstance() {
        if (instance == null) instance = new NotificationManager();
        return instance;
    }

    /** Send notification (default unread) */
    public void sendNotification(String userId, String message) {
        ArrayList<Notification> inbox =
                userNotifications.computeIfAbsent(userId, k -> new ArrayList<>());

        inbox.add(new Notification(message));
    }

    /** Get all notifications, sorted by timestamp (optional) */
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
    
    /** Get only unread notifications */
    public List<Notification> getUnreadNotifications(String userId) {
        List<Notification> inbox = userNotifications.get(userId);
        if (inbox == null) return new ArrayList<>();

        List<Notification> unread = new ArrayList<>();
        for (Notification n : inbox) {
            if (!n.isRead()) unread.add(n);
        }
        return unread;
    }

    /** Mark all notifications in inbox as read */
    public void markAllAsRead(String userId) {
        List<Notification> inbox = userNotifications.get(userId);
        if (inbox == null) return;

        for (Notification n : inbox) {
            n.markAsRead();
        }
    }

    /** Remove every notification for a user */
    public void clearNotifications(String userId) {
        userNotifications.remove(userId);
    }

    /** Check whether user has at least one unread notification */
    public boolean hasUnread(String userId) {
        List<Notification> inbox = userNotifications.get(userId);
        if (inbox == null) return false;

        for (Notification n : inbox) {
            if (!n.isRead()) return true;
        }
        return false;
    }
}
