package websockets;

import com.kontrol.events.model.EventDTO;
import com.kontrol.websockets.WebsocketResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
public class EventResourceTest {

    @InjectMock
    WebsocketResource websocketResource;

    @Test
    public void testCreateEvent() {
        Mockito.doNothing().when(websocketResource).broadcast(any(EventDTO.class));

        EventDTO eventDTO = new EventDTO();
        eventDTO.source = "Test Class";
        eventDTO.name = "Here you go new event!";
        eventDTO.createdAt = LocalDateTime.now();

        given()
             .contentType(MediaType.APPLICATION_JSON)
             .body(eventDTO)
             .when()
             .post("/events")
             .then()
             .statusCode(Response.Status.CREATED.getStatusCode());
    }
}
