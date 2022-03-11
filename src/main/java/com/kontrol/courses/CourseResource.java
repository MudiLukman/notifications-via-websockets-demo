package com.kontrol.courses;

import com.kontrol.courses.model.RetiredCourseDTO;
import io.vertx.mutiny.core.eventbus.EventBus;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/courses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

    @Inject EventBus eventBus;

    @PUT
    @Path("{id}")
    public Response retire(@PathParam("id") int id, @Valid RetiredCourseDTO course) {
        //Claim to retire course but do nothing
        course.id = id;
        eventBus.publish("retire-course", course);
        return Response.ok(course).build();
    }
}
