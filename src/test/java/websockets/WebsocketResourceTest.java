package websockets;

import com.kontrol.users.model.UserDTO;
import com.kontrol.websockets.ProofKeyService;
import com.kontrol.websockets.model.Notification;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.apache.commons.lang3.RandomStringUtils;
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
        String code = RandomStringUtils.randomAlphanumeric(10);
        Mockito.when(proofKeyService.removeCode(code)).thenReturn(username);

        UserDTO userDTO = new UserDTO();
        userDTO.name = "kaniel";
        userDTO.createdAt = LocalDateTime.now();
        userDTO.source = username;
        userDTO.departmentId = "test";

        URI uri = UriBuilder.fromUri(websocketEndpoint)
                .queryParam("code", code)
                .build();
        try (Session session = ContainerProvider.getWebSocketContainer()
                .connectToServer(websocketClient, uri)) {
            eventBus.publish("ws-new-user", userDTO);
            Thread.sleep(1500); //Wait 1.5 secs bcos server endpoint sends msg in async fashion
            Notification notification = websocketClient.getNotification();
            Assertions.assertEquals(username, notification.source);
            Assertions.assertEquals(Notification.NotificationType.NEW_CANDIDATE, notification.type);
        }
    }

    @Test
    public void testNewUserNotification_shouldReceiveBufferedNotification() throws Exception {
        String code = RandomStringUtils.randomAlphanumeric(10);
        Mockito.when(proofKeyService.removeCode(code)).thenReturn(username);

        UserDTO userDTO = new UserDTO();
        userDTO.name = "Outis";
        userDTO.createdAt = LocalDateTime.now();
        userDTO.source = username;
        userDTO.departmentId = "test";

        URI uri = UriBuilder.fromUri(websocketEndpoint)
                .queryParam("code", code)
                .build();
        try (Session session = ContainerProvider.getWebSocketContainer()
                .connectToServer(websocketClient, uri)) {
            eventBus.publish("ws-new-user", userDTO);

            /*At this point, daily summary (every 1s) has been computed and buffered
            5.1 seconds gives enough time for a new summary to be computed (5 summaries)
            and enough time for websockets' retry cron (every 5s) to execute
             */
            Thread.sleep(5100);
            Notification notification = websocketClient.getNotification();
            Assertions.assertEquals(username, notification.source);
            Assertions.assertEquals(Notification.NotificationType.NEW_CANDIDATE, notification.type);
        }
    }

    @Test
    public void testNewUserNotification_shouldNotCreateSessionForInvalidCode() throws Exception{
        UserDTO userDTO = new UserDTO();
        userDTO.name = "Person";
        userDTO.createdAt = LocalDateTime.now();
        userDTO.source = username;
        userDTO.departmentId = "test";

        URI uri = UriBuilder.fromUri(websocketEndpoint)
                .queryParam("code", UUID.randomUUID())
                .build();
        try (Session session = ContainerProvider.getWebSocketContainer()
                .connectToServer(websocketClient, uri)) {
            eventBus.publish("ws-new-user", userDTO);
            Thread.sleep(1500); //Wait 1.5 secs bcos server endpoint sends msg in async fashion

            Assertions.assertNull(websocketClient.getNotification());
        }
    }

}
