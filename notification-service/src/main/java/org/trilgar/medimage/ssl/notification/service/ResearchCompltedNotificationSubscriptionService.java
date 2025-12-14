package org.trilgar.medimage.ssl.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.trilgar.medimage.ssl.model.ResearchCompletedNotificationEvent;
import org.trilgar.medimage.ssl.notification.service.api.NotificationSubscriptionService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class ResearchCompltedNotificationSubscriptionService implements NotificationSubscriptionService<SseEmitter, ResearchCompletedNotificationEvent> {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @Override
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitters.add(emitter);
        log.info("Client connected. Total clients: {}", emitters.size());
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        return emitter;
    }

    @Override
    public void sendNotification(ResearchCompletedNotificationEvent event) {
        log.info("Broadcasting notification for patient {}", event.getPatientId());

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(event));
            } catch (IOException e) {
                log.error("Error while sending notification for patient {}. Unsubscribing failed subscription", event.getPatientId(), e);
                emitters.remove(emitter);
            }
        }
    }


}
