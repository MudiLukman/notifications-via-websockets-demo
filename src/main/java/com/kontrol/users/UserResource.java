package com.kontrol.users;

import com.kontrol.users.model.LoginRequest;
import com.kontrol.users.model.UserDTO;
import com.kontrol.websockets.ProofKeyService;
import io.quarkus.security.UnauthorizedException;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject EventBus eventBus;
    @Inject
    ProofKeyService proofKeyService;
    @ConfigProperty(name = "MOCK_USERNAME")
    String username;
    @ConfigProperty(name = "MOCK_PASSWORD")
    String password;

    @POST
    public Response createEvent(@Valid UserDTO user) {
        eventBus.publish("ws-new-user", user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @POST
    @Path("login")
    public Response getToken(@Valid LoginRequest loginRequest) {
        if (loginRequest.username.equals(username) && loginRequest.password.equals(password)) {
            return Response.ok(proofKeyService.generateCode(loginRequest.source)).build();
        } else {
            throw new UnauthorizedException("Invalid username or password");
        }
    }
}
