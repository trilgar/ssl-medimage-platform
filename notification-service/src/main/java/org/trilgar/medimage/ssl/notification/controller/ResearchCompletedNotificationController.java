package org.trilgar.medimage.ssl.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.trilgar.medimage.ssl.model.ResearchCompletedNotificationEvent;
import org.trilgar.medimage.ssl.notification.service.api.NotificationSubscriptionService;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class ResearchCompletedNotificationController {
    private final NotificationSubscriptionService<SseEmitter, ResearchCompletedNotificationEvent> subscriptionService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications() {
        return subscriptionService.subscribe();
    }
}
