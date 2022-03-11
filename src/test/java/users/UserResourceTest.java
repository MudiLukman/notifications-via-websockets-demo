package users;

import com.kontrol.users.model.UserDTO;
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
public class UserResourceTest {

    @InjectMock
    WebsocketResource websocketResource;

    @Test
    public void testCreateEvent() {
        Mockito.doNothing().when(websocketResource).consumeNewUser(any(UserDTO.class));

        UserDTO userDTO = new UserDTO();
        userDTO.name = "Here you go new event!";
        userDTO.createdAt = LocalDateTime.now();
        userDTO.source = "kontrol";

        given()
             .contentType(MediaType.APPLICATION_JSON)
             .body(userDTO)
             .when()
             .post("/users")
             .then()
             .statusCode(Response.Status.CREATED.getStatusCode());
    }
}
