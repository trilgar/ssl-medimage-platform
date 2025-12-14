package org.trilgar.medimage.ssl.notification.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.trilgar.medimage.ssl.model.ResearchCompletedNotificationEvent;
import org.trilgar.medimage.ssl.notification.config.RabbitConfig;
import org.trilgar.medimage.ssl.notification.service.api.NotificationSubscriptionService;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResearchCompletedNotificationListener {

    private final NotificationSubscriptionService<SseEmitter, ResearchCompletedNotificationEvent> subscriptionService;

    @RabbitListener(queues = RabbitConfig.NOTIFICATION_QUEUE)
    public void handleNotification(ResearchCompletedNotificationEvent event) {
        log.info("Received event type: {}. Sending to all subscribers.", event.getType());
        subscriptionService.sendNotification(event);
    }
}
