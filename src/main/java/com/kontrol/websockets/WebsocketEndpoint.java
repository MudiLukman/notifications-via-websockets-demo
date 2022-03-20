package com.kontrol.websockets;

import com.kontrol.websockets.codecs.NotificationEncoder;
import com.kontrol.websockets.model.Notification;
import com.kontrol.websockets.security.WebsocketFilter;
import io.quarkus.scheduler.Scheduled;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@ServerEndpoint(value = "/websockets/{username}", encoders = NotificationEncoder.class,
configurator = WebsocketFilter.class)
public class WebsocketEndpoint {

    @Inject ProofKeyService proofKeyService;
    private static final Map<String, Set<Session>> sessions = new ConcurrentHashMap<>();
    private static final Map<String, Set<Notification>> buffer = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig, @PathParam("username") String username) {
        String accessCode = (String) endpointConfig.getUserProperties().get("code");
        String source = proofKeyService.removeCode(accessCode);
        if (source == null || !source.equals(username)) {
            return; //silently ignore session request
        }
        addSession(username, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        endSession(username, session);
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        System.out.println("Error for session: " + session.getId() + " cause: " + throwable.getMessage());
        endSession(username, session);
    }

    @Scheduled(every = "5s")
    public void attemptResend() {
        buffer.forEach((source, notifications) -> {
            Set<Session> activeSessions = sessions.getOrDefault(source, new HashSet<>());
            if (!activeSessions.isEmpty()) {
                notifications.forEach(notification -> {
                    activeSessions.forEach(session -> session.getAsyncRemote().sendObject(notification));
                });
                notifications.clear();
            }
        });
    }

    public static void send(String source, Notification notification) {
        Set<Session> activeSessions = sessions.getOrDefault(source, new HashSet<>());
        if (activeSessions.isEmpty()) {
            bufferNotifications(source, notification);
        } else {
            activeSessions.forEach(session -> session.getAsyncRemote().sendObject(notification));
        }
    }

    private static void bufferNotifications(String source, Notification notification) {
        Set<Notification> notifications = new HashSet<>();
        notifications.add(notification);
        buffer.merge(source, notifications, (currentNotifications, newNotifications) -> {
            currentNotifications.addAll(newNotifications);
            return currentNotifications;
        });
    }

    private static void endSession(String username, Session session) {
        sessions.getOrDefault(username, new HashSet<>()).remove(session);
    }

    private static void addSession(String username, Session session) {
        Set<Session> newSessions = new HashSet<>();
        newSessions.add(session);
        sessions.merge(username, newSessions, (existingSess, newSess) -> {
            existingSess.addAll(newSess);
            return existingSess;
        });
    }
}
