package com.kontrol.websockets;

import com.kontrol.events.model.EventDTO;
import com.kontrol.websockets.encoders.NotificationEncoder;
import com.kontrol.websockets.model.Notification;
import io.quarkus.vertx.ConsumeEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.time.LocalDateTime;
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
        Set<Session> ss = new HashSet<>();
        ss.add(session);
        sessions.put(username, ss);
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessions.get(username).remove(session);
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        sessions.get(username).remove(session);
        System.out.println("Error for session: " + session + " cause: " + throwable.getMessage());
    }

    @ConsumeEvent("new-event")
    public void broadcast(EventDTO eventDTO) {
        Notification notification = new Notification();
        notification.triggeredAt = LocalDateTime.now();
        notification.message = "New Candidate Arrived";
        notification.payload = eventDTO;

        sessions.forEach((orgName, sessions) -> {
            for (Session session : sessions) {
                session.getAsyncRemote().sendObject(notification);
            }
        });
    }
}
