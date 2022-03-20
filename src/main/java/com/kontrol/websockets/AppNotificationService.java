package com.kontrol.websockets;

import com.kontrol.users.model.UserDTO;
import com.kontrol.websockets.model.ApplicantSummary;
import com.kontrol.websockets.model.Notification;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.vertx.ConsumeEvent;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class AppNotificationService {

    private final Map<String, Map<String, AtomicLong>> aggregates = new ConcurrentHashMap<>(); //Map<orgName, Map<applicationId, count>>

    @ConsumeEvent("ws-new-user")
    public void consumeNewUser(UserDTO userDTO) {
        Map<String, AtomicLong> summary = new HashMap<>();
        summary.put(userDTO.departmentId, new AtomicLong(1));

        aggregates.merge(userDTO.source, summary, (m1, m2) -> {
            m2.forEach((departmentId, count) -> {
                m1.merge(departmentId, count, (v1, v2) -> new AtomicLong(v1.longValue() + v2.longValue()));
            });
            return m1;
        });
    }

    @Scheduled(every = "24h")
    public void computeSummary() {
        aggregates.forEach((source, summary) -> {
            Set<Notification> notifications = new HashSet<>();
            summary.forEach((department, aggregate) -> {
                long total = aggregate.get();
                if (total > 0) {
                    aggregate.set(0); //reset aggregate
                    Notification notification = new Notification();
                    notification.type = Notification.NotificationType.NEW_CANDIDATE;
                    notification.source = source;
                    notification.payload = new ApplicantSummary(total, department);
                    notifications.add(notification);
                }
            });
            notifications.forEach(notification -> WebsocketEndpoint.send(source, notification));
        });
        aggregates.clear();
    }
}
