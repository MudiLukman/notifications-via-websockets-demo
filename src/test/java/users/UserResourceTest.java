package users;

import com.kontrol.users.model.LoginRequest;
import com.kontrol.users.model.UserDTO;
import com.kontrol.websockets.WebsocketResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
public class UserResourceTest {

    @InjectMock
    WebsocketResource websocketResource;

    @ConfigProperty(name = "MOCK_USERNAME")
    String username;
    @ConfigProperty(name = "MOCK_PASSWORD")
    String password;

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

    @Test
    public void testLogin() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.username = username;
        loginRequest.password = password;

        UUID code = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginRequest)
                .when()
                .post("/users/login")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().as(UUID.class);

        Assertions.assertNotNull(code);
    }
}
