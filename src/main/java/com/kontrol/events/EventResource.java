package com.kontrol.events;

import com.kontrol.events.model.EventDTO;
import io.vertx.mutiny.core.eventbus.EventBus;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("events")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject EventBus eventBus;

    @POST
    public Response createEvent(@Valid EventDTO event) {
        eventBus.send("new-event", event);
        return Response.status(Response.Status.CREATED).entity(event).build();
    }
}
