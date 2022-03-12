package com.kontrol.users;

import com.kontrol.users.model.UserDTO;
import io.vertx.mutiny.core.eventbus.EventBus;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject EventBus eventBus;

    @POST
    public Response createEvent(@Valid UserDTO user) {
        eventBus.publish("ws-new-user", user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }
}
