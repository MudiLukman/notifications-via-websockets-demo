package com.kontrol.websockets;

import com.kontrol.courses.model.RetiredCourseDTO;
import com.kontrol.users.model.UserDTO;
import com.kontrol.websockets.encoders.NotificationEncoder;
import com.kontrol.websockets.model.Notification;
import com.kontrol.websockets.model.NotificationType;
import io.quarkus.vertx.ConsumeEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@ServerEndpoint(value = "/websockets/{username}", encoders = NotificationEncoder.class)
public class WebsocketResource {

    private static final Map<String, Set<Session>> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        System.out.println("Connected to: " + username);
        Set<Session> newSessions = new HashSet<>();
        newSessions.add(session);
        sessions.merge(username, newSessions, (existingSess, newSess) -> {
            existingSess.addAll(newSess);
            return existingSess;
        });
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

    @ConsumeEvent("new-user")
    public void consumeNewUser(UserDTO userDTO) {
        Notification notification = new Notification();
        notification.type = NotificationType.NEW_CANDIDATE;
        notification.message = "New applicant " + userDTO.name + " applied";
        notification.payload = userDTO;
        notification.source = userDTO.source;

        broadcast(notification);
    }

    @ConsumeEvent("retire-course")
    public void consumeRetiredCourse(RetiredCourseDTO course) {
        Notification notification = new Notification();
        notification.type = NotificationType.RETIRE_COURSE;
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
}
