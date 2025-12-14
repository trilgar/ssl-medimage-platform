package org.trilgar.medimage.ssl.notification.service.api;

public interface NotificationSubscriptionService<T, N> {
    T subscribe();

    void sendNotification(N event);
}
