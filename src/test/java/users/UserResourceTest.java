package users;

import com.kontrol.users.model.LoginRequest;
import com.kontrol.users.model.UserDTO;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class UserResourceTest {

    @ConfigProperty(name = "MOCK_USERNAME")
    String username;
    @ConfigProperty(name = "MOCK_PASSWORD")
    String password;

    @Test
    public void testCreateEvent() {
        UserDTO userDTO = new UserDTO();
        userDTO.name = "Here you go new event!";
        userDTO.createdAt = LocalDateTime.now();
        userDTO.source = "kontrol";
        userDTO.departmentId = "test";

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
        loginRequest.source = "test_org";

        String code = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginRequest)
                .when()
                .post("/users/login")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract().body().toString();

        Assertions.assertNotNull(code);
    }
}
