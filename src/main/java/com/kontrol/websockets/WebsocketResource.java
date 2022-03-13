package com.kontrol.websockets;

import com.kontrol.courses.model.RetiredCourseDTO;
import com.kontrol.users.model.UserDTO;
import com.kontrol.websockets.codecs.NotificationEncoder;
import com.kontrol.websockets.model.Notification;
import com.kontrol.websockets.security.WebsocketFilter;
import io.quarkus.vertx.ConsumeEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@ServerEndpoint(value = "/websockets/{username}", encoders = NotificationEncoder.class,
configurator = WebsocketFilter.class)
public class WebsocketResource {

    private final Map<String, Set<Session>> sessions = new ConcurrentHashMap<>();
    @Inject ProofKeyService proofKeyService;

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig, @PathParam("username") String username) {
        var code = (String) endpointConfig.getUserProperties().get("code");
        UUID accessCode = UUID.fromString(code);
        if (proofKeyService.removeCode(accessCode) == null) {
            System.out.println("Invalid code: " + accessCode); //silently ignore
            return;
        }
        addSession(session, username);
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        System.out.println("Closing session: " + session.getId());
        endSession(username, session);
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        System.out.println("Error for session: " + session.getId() + " cause: " + throwable.getMessage());
        endSession(username, session);
    }

    @ConsumeEvent("ws-new-user")
    public void consumeNewUser(UserDTO userDTO) {
        Notification notification = new Notification();
        notification.type = Notification.NotificationType.NEW_CANDIDATE;
        notification.message = "New applicant " + userDTO.name + " applied";
        notification.payload = userDTO;
        notification.source = userDTO.source;

        broadcast(notification);
    }

    @ConsumeEvent("ws-retire-course")
    public void consumeRetiredCourse(RetiredCourseDTO course) {
        Notification notification = new Notification();
        notification.type = Notification.NotificationType.RETIRE_COURSE;
        notification.message = course.name + " has been retired by " + course.retiredBy.name;
        notification.payload = course;
        notification.source = course.source;

        broadcast(notification);
    }

    private void broadcast(Notification notification) {
        sessions.getOrDefault(notification.source, new HashSet<>())
                .forEach(session -> session.getAsyncRemote().sendObject(notification));
    }

    private void endSession(String username, Session session) {
        sessions.getOrDefault(username, new HashSet<>()).remove(session);
    }

    private void addSession(Session session, String username) {
        Set<Session> newSessions = new HashSet<>();
        newSessions.add(session);
        sessions.merge(username, newSessions, (existingSess, newSess) -> {
            existingSess.addAll(newSess);
            return existingSess;
        });
    }
}
