package entity;

import java.util.List;

public interface Notifiable {
    void receiveNotification(Notification notification);
    List<Notification> getNotifications();
}
