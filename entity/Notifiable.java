package entity;

public interface Notifiable {
    void receiveNotification(Notification notification);
    List<Notification> getNotifications();
}
