package courses;

import com.kontrol.courses.model.RetiredCourseDTO;
import com.kontrol.users.model.UserDTO;
import com.kontrol.websockets.WebsocketResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
public class CoursesResourceTest {

    @InjectMock WebsocketResource websocketResource;

    @Test
    public void testRetireCourse() {
        Mockito.doNothing().when(websocketResource).consumeRetiredCourse(any(RetiredCourseDTO.class));

        RetiredCourseDTO retiredCourseDTO = new RetiredCourseDTO();
        retiredCourseDTO.name = "CS 6006";
        retiredCourseDTO.retiredBy = new UserDTO();
        retiredCourseDTO.source = "kontrol";

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(retiredCourseDTO)
                .when()
                .put("/courses/1")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

}
