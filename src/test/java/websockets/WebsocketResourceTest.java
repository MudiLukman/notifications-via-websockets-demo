package websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kontrol.events.model.EventDTO;
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
    public void testWebsocket() throws Exception{
        var now = LocalDateTime.now();

        EventDTO eventDTO = new EventDTO();
        eventDTO.name = "New User: Mudi Lukman";
        eventDTO.source = "Test Class";
        eventDTO.createdAt = now;

        try (Session session = ContainerProvider.getWebSocketContainer()
                .connectToServer(websocketClient, websocketEndpoint)) {
            eventBus.publish("new-event", eventDTO);
            Thread.sleep(1000); //Delay for at least 1 sec bcos server endpoint sends in an async fashion
            Assertions.assertEquals("New Candidate Arrived", websocketClient.getNotification().message);
            var payload = new ObjectMapper().writeValueAsString(websocketClient.getNotification().payload);
            EventDTO clientResponse = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .readValue(payload, EventDTO.class);
            Assertions.assertEquals(eventDTO.name, clientResponse.name);
            Assertions.assertEquals(eventDTO.source, clientResponse.source);
        }
    }

}
