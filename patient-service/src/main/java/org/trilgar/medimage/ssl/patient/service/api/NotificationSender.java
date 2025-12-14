package org.trilgar.medimage.ssl.patient.service.api;

public interface NotificationSender<T> {
    void sendCompletionNotification(T result);
}
