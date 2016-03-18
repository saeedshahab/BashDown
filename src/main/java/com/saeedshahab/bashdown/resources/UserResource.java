package com.saeedshahab.bashdown.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.saeedshahab.bashdown.models.User;
import com.saeedshahab.bashdown.repositories.UserRepository;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static com.saeedshahab.bashdown.models.User.Roles.ADMIN;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Inject
    public UserResource(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @POST
    @Path("/create")
    @RolesAllowed(ADMIN)
    public Response createUser(@Valid User user) throws JsonProcessingException {
        Optional<User> userOut = userRepository.create(user);
        if (userOut.isPresent()) {
            return Response.status(Response.Status.CREATED).entity(objectMapper.writeValueAsString(userOut)).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Collections.singletonMap("error", "Sorry, Failed to create user. Please try again in some time"))
                    .build();
        }
    }

    @GET
    @Path("/{displayName}")
    @RolesAllowed(ADMIN)
    public Response lookupUser(@PathParam("displayName") String displayName) throws JsonProcessingException {
        Optional<User> userOut = userRepository.searchOne(Collections.singletonMap("displayName", displayName));
        if (userOut.isPresent()) {
            return Response.ok(objectMapper.writeValueAsString(userOut.get())).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
