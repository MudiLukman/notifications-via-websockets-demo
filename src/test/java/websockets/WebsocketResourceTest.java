package websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kontrol.courses.model.RetiredCourseDTO;
import com.kontrol.users.model.UserDTO;
import com.kontrol.websockets.model.Notification;
import com.kontrol.websockets.model.NotificationType;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import websockets.test_helper.WebsocketClient;

import javax.inject.Inject;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import java.net.URI;
import java.time.LocalDateTime;

@QuarkusTest
public class WebsocketResourceTest {

    @TestHTTPResource("/websockets/kontrol")
    URI websocketEndpoint;
    @Inject EventBus eventBus;
    @Inject WebsocketClient websocketClient;

    @Test
    public void testConsumeNewUser() throws Exception{
        UserDTO userDTO = new UserDTO();
        userDTO.name = "New User: Mudi Lukman";
        userDTO.createdAt = LocalDateTime.now();
        userDTO.source = "kontrol";

        try (Session session = ContainerProvider.getWebSocketContainer()
                .connectToServer(websocketClient, websocketEndpoint)) {
            eventBus.publish("new-user", userDTO);
            Thread.sleep(1000); //Delay for at least 1 sec bcos server endpoint sends in an async fashion
            Assertions.assertEquals(
                    "New applicant " + userDTO.name + " applied",
                    websocketClient.getNotification().message);
            Assertions.assertEquals("kontrol", websocketClient.getNotification().source);
            Assertions.assertEquals(NotificationType.NEW_CANDIDATE, websocketClient.getNotification().type);
            var payload = new ObjectMapper().writeValueAsString(websocketClient.getNotification().payload);
            UserDTO clientResponse = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(payload, UserDTO.class);
            Assertions.assertEquals(userDTO.name, clientResponse.name);
            Assertions.assertEquals(userDTO.createdAt, clientResponse.createdAt);
        }
    }

    @Test
    public void testConsumeRetiredCourse() throws Exception{
        UserDTO userDTO = new UserDTO();
        userDTO.name = "Mudi Lukman";
        userDTO.createdAt = LocalDateTime.now().minusYears(3);
        userDTO.source = "kontrol";

        RetiredCourseDTO course = new RetiredCourseDTO();
        course.source = userDTO.source;
        course.retiredAt = LocalDateTime.now();
        course.name = "CS 6006";
        course.id = 1;
        course.retiredBy = userDTO;

        try (Session session = ContainerProvider.getWebSocketContainer()
                .connectToServer(websocketClient, websocketEndpoint)) {
            eventBus.publish("retire-course", course);
            Thread.sleep(1000); //Delay for at least 1 sec bcos server endpoint sends in an async fashion
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
            Assertions.assertEquals(NotificationType.RETIRE_COURSE, notification.type);
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

}
