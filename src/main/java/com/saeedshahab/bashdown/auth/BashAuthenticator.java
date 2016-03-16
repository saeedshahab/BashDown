package com.saeedshahab.bashdown.auth;

import com.google.common.base.Optional;
import com.saeedshahab.bashdown.models.User;
import com.saeedshahab.bashdown.repositories.UserRepository;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class BashAuthenticator implements Authenticator<BasicCredentials, User> {

    private static final Logger logger = LoggerFactory.getLogger(BashAuthenticator.class);

    @Inject
    private UserRepository userRepository;

    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        logger.debug("Authenticate credentials: {}", credentials);

        Map<String, Object> map = new HashMap<>();
        map.put("displayName", credentials.getUsername());
        map.put("password", User.sha1Hash(credentials.getPassword()));
        try {
            Optional<User> user = userRepository.search(map);
            logger.debug("Search for type: {}, query: {}, result: {}", User.class, map, user);
            return user;
        } catch (Exception e) {
            logger.error("Database operation failed to search for user with query: {}", map, e);
        }
        return Optional.absent();
    }
}
