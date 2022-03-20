package courses;

import com.kontrol.websockets.WebsocketEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
public class CoursesResourceTest {

    @InjectMock
    WebsocketEndpoint websocketEndpoint;

    /*@Test
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
    }*/

}
