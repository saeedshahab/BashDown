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
import java.util.Map;

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
        Bash bashOut = bashRepository.create(bash);
        return Response.status(Response.Status.CREATED).entity(objectMapper.writeValueAsString(bashOut)).build();
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
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Collections.singletonMap("error", "No active bash found"))
                    .build();
        }
    }

    @DELETE
    @Path("/id/{id}")
    @RolesAllowed(ADMIN)
    public Response deleteBashWithId(@PathParam("id") String id) {
        Map<String, Object> response = bashRepository.delete(id);
        return Response.ok(response).build();

    }

}
