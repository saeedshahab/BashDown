package com.saeedshahab.bashdown.repositories;

import com.google.common.base.Optional;
import com.saeedshahab.bashdown.models.User;
import com.saeedshahab.bashdown.wrappers.DatabaseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

public class UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    private final DatabaseWrapper<?> databaseWrapper;

    @Inject
    public UserRepository(DatabaseWrapper databaseWrapper) {
        this.databaseWrapper = databaseWrapper;
    }

    public User create(User user) {
        user.setId(UUID.randomUUID().toString());
        try {
            databaseWrapper.create(user, User.class);
        } catch (Exception e) {
            logger.error("Database operation failed to create user: {}", user, e);
        }
        user.setPassword(null);
        return user;
    }

    public Optional<User> search(Map<String, Object> map) {
        try {
            List<User> userList = databaseWrapper.search(map, User.class);
            if (userList.size() > 0) {
                User user = userList.get(0);
                user.setPassword(null);

                if (Objects.isNull(user.getRoles())) {
                    user.setRoles(Collections.emptyList());
                }
                return Optional.of(user);
            }
        } catch (Exception e) {
            logger.error("Database operation failed to find user with query: {}", map, e);
        }
        return Optional.absent();
    }

//    public User addRolesToUser(List<String> roles) {
//        return null;
//    }
}
