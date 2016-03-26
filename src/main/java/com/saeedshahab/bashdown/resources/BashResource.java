package com.saeedshahab.bashdown.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.saeedshahab.bashdown.models.Bash;
import com.saeedshahab.bashdown.repositories.BashRepository;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

import static com.saeedshahab.bashdown.models.User.Roles.ADMIN;

@Path("/bash")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BashResource {

    private final BashRepository bashRepository;
    private final ObjectMapper objectMapper;

    @Inject
    public BashResource(BashRepository bashRepository, ObjectMapper objectMapper) {
        this.bashRepository = bashRepository;
        this.objectMapper = objectMapper;
    }

    @POST
    @Path("/create")
    @RolesAllowed(ADMIN)
    public Response createBash(@Valid Bash bash) throws JsonProcessingException {
        Optional<Bash> bashOut = bashRepository.create(bash);

        if (bashOut.isPresent()){
            return Response.status(Response.Status.CREATED).entity(objectMapper.writeValueAsString(bashOut)).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Collections.singletonMap("error", "Sorry, Failed to create Bash. Please try again in some time"))
                    .build();
        }
    }

    @POST
    @Path("/id/{id}/update")
    @RolesAllowed(ADMIN)
    public Response updateBash(@PathParam("id") String id, @Valid Bash bash) throws JsonProcessingException {
        Long response = bashRepository.update(id, bash);

        if (response > 0L) {
            return Response.ok(Collections.singletonMap("success", String.format("Updated bash with id %s", id))).build();
        } else if (response == 0L) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Collections.singletonMap("error", String.format("No bash found with id %s", id)))
                    .build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Collections.singletonMap("error", String.format("Sorry, Failed to delete bash with id: %s. Please try again in some time", id)))
                    .build();
        }
    }

    @GET
    @Path("/id/{id}")
    public Response getBash(@PathParam("id") String id) throws JsonProcessingException {
        Optional<Bash> bashOut = bashRepository.getById(id);
        if (bashOut.isPresent()) {
            return Response.ok(objectMapper.writeValueAsString(bashOut)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Collections.singletonMap("error", String.format("Bash with id %s not found", id)))
                    .build();
        }
    }

    @GET
    @Path("/find/{key}")
    public Response findBash(@PathParam("key") String key) throws JsonProcessingException {
        List<Bash> bashOuts;
        switch (key) {
            case "all":
                bashOuts = bashRepository.search(Collections.emptyMap());
                break;
            default:
                bashOuts = bashRepository.search(Collections.singletonMap("active", true));
        }

        if (bashOuts.size() > 0) {
            return Response.ok(objectMapper.writeValueAsString(bashOuts)).build();
        } else {
            return Response.status(Response.Status.NO_CONTENT)
                    .entity(Collections.singletonMap("error", "No active bash found"))
                    .build();
        }
    }

    @DELETE
    @Path("/id/{id}")
    @RolesAllowed(ADMIN)
    public Response deleteBashWithId(@PathParam("id") String id) {
        Long response = bashRepository.delete(id);

        if (response > 0L) {
            return Response.ok(Collections.singletonMap("success", String.format("Deleted bash with id %s", id))).build();
        } else if (response == 0L) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Collections.singletonMap("error", String.format("No bash found with id %s", id)))
                    .build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Collections.singletonMap("error", String.format("Sorry, Failed to delete bash with id: %s. Please try again in some time", id)))
                    .build();
        }
    }
}
