package websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kontrol.courses.model.RetiredCourseDTO;
import com.kontrol.users.model.UserDTO;
import com.kontrol.websockets.ProofKeyService;
import com.kontrol.websockets.model.Notification;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import websockets.test_helper.WebsocketClient;

import javax.inject.Inject;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

@QuarkusTest
public class WebsocketResourceTest {

    private static final String username = "kontrol";

    @TestHTTPResource("/websockets/" + username)
    URI websocketEndpoint;
    @Inject EventBus eventBus;
    @Inject WebsocketClient websocketClient;

    @InjectMock
    ProofKeyService proofKeyService;

    @AfterEach
    public void tearDown() {
        websocketClient.setNotification(null);
    }

    @Test
    public void testNewUserNotification() throws Exception{
        UUID code = UUID.randomUUID();
        Mockito.when(proofKeyService.generateCode(username)).thenReturn(code);
        Mockito.when(proofKeyService.removeCode(code)).thenReturn(username);

        UserDTO userDTO = new UserDTO();
        userDTO.name = "kaniel";
        userDTO.createdAt = LocalDateTime.now();
        userDTO.source = username;

        URI uri = UriBuilder.fromUri(websocketEndpoint)
                .queryParam("code", code)
                .build();
        try (Session session = ContainerProvider.getWebSocketContainer()
                .connectToServer(websocketClient, uri)) {
            delayFor(250); //Wait 1/4 secs for handshake to complete
            eventBus.publish("ws-new-user", userDTO);
            delayFor(250); //Wait 1/4 secs bcos server endpoint sends msg in async fashion
            Notification notification = websocketClient.getNotification();
            Assertions.assertEquals(
                    "New applicant " + userDTO.name + " applied",
                    notification.message);
            Assertions.assertEquals("kontrol", notification.source);
            Assertions.assertEquals(Notification.NotificationType.NEW_CANDIDATE, notification.type);
            var payload = new ObjectMapper().writeValueAsString(notification.payload);
            UserDTO clientResponse = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(payload, UserDTO.class);
            Assertions.assertEquals(userDTO.name, clientResponse.name);
            Assertions.assertEquals(userDTO.createdAt, clientResponse.createdAt);
        }
    }

    @Test
    public void testNewUserNotification_shouldNotCreateSessionForInvalidCode() throws Exception{
        UserDTO userDTO = new UserDTO();
        userDTO.name = "Person";
        userDTO.createdAt = LocalDateTime.now();
        userDTO.source = username;

        URI uri = UriBuilder.fromUri(websocketEndpoint)
                .queryParam("code", UUID.randomUUID())
                .build();
        try (Session session = ContainerProvider.getWebSocketContainer()
                .connectToServer(websocketClient, uri)) {
            delayFor(250); //Wait 1/4 secs for handshake to complete
            eventBus.publish("ws-new-user", userDTO);
            delayFor(250); //Wait 1/4 sec bcos server endpoint sends msg in async fashion

            Assertions.assertNull(websocketClient.getNotification());
        }
    }

    @Test
    public void testRetiredCourseNotification() throws Exception{
        UUID code = UUID.randomUUID();
        Mockito.when(proofKeyService.generateCode(username)).thenReturn(code);
        Mockito.when(proofKeyService.removeCode(code)).thenReturn(username);

        UserDTO userDTO = new UserDTO();
        userDTO.name = "Mudi Lukman";
        userDTO.createdAt = LocalDateTime.now().minusYears(3);
        userDTO.source = username;

        RetiredCourseDTO course = new RetiredCourseDTO();
        course.source = userDTO.source;
        course.retiredAt = LocalDateTime.now();
        course.name = "CS 6006";
        course.id = 1;
        course.retiredBy = userDTO;

        URI uri = UriBuilder.fromUri(websocketEndpoint)
                .queryParam("code", code)
                .build();
        try (Session session = ContainerProvider.getWebSocketContainer()
                .connectToServer(websocketClient, uri)) {
            delayFor(250); //Wait 1/4 secs for handshake to complete
            eventBus.publish("ws-retire-course", course);
            delayFor(250); //Wait 1/4 sec bcos server endpoint sends msg in async fashion
            Notification notification = websocketClient.getNotification();
            var payload = new ObjectMapper().writeValueAsString(notification.payload);
            RetiredCourseDTO clientResponse = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(payload, RetiredCourseDTO.class);
            UserDTO userResponse = clientResponse.retiredBy;
            Assertions.assertEquals(
                    course.name + " has been retired by " + userResponse.name,
                    notification.message);
            Assertions.assertEquals(course.source, notification.source);
            Assertions.assertEquals(Notification.NotificationType.RETIRE_COURSE, notification.type);
            Assertions.assertEquals(course.name, clientResponse.name);
            Assertions.assertEquals(course.source, clientResponse.source);
            Assertions.assertEquals(course.id, clientResponse.id);
            Assertions.assertEquals(course.retiredAt, clientResponse.retiredAt);
            Assertions.assertEquals(userDTO.name, userResponse.name);
            Assertions.assertEquals(userDTO.source, userResponse.source);
            Assertions.assertEquals(userDTO.createdAt, userResponse.createdAt);
            Assertions.assertEquals(userDTO.userId, userResponse.userId);
        }
    }

    @Test
    public void testRetiredCourseNotification_shouldNotCreateSessionForInvalidCode() throws Exception{
        UserDTO userDTO = new UserDTO();
        userDTO.name = "Demo";
        userDTO.createdAt = LocalDateTime.now().minusYears(3);
        userDTO.source = username;

        RetiredCourseDTO course = new RetiredCourseDTO();
        course.source = userDTO.source;
        course.retiredAt = LocalDateTime.now();
        course.name = "CS 6006";
        course.id = 1;
        course.retiredBy = userDTO;

        URI uri = UriBuilder.fromUri(websocketEndpoint)
                .queryParam("code", UUID.randomUUID())
                .build();
        try (Session session = ContainerProvider.getWebSocketContainer()
                .connectToServer(websocketClient, uri)) {
            delayFor(250); //Wait 1/4 secs for handshake to complete
            eventBus.publish("ws-retire-course", course);
            delayFor(250); //Wait 1/4 sec bcos server endpoint sends msg in async fashion

            Assertions.assertNull(websocketClient.getNotification());
        }
    }

    private void delayFor(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }

}
