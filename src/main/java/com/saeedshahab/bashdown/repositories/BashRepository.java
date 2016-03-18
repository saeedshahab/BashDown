package com.saeedshahab.bashdown.repositories;

import com.google.common.base.Optional;
import com.saeedshahab.bashdown.models.Bash;
import com.saeedshahab.bashdown.wrappers.DatabaseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

public class BashRepository {

    private static final Logger logger = LoggerFactory.getLogger(BashRepository.class);

    private final DatabaseWrapper<?> databaseWrapper;

    @Inject
    public BashRepository(DatabaseWrapper databaseWrapper) {
        this.databaseWrapper = databaseWrapper;
    }

    public Optional<Bash> create(Bash bash) {
        bash.setId(UUID.randomUUID().toString());
        bash.setActive(true);
        try {
            databaseWrapper.create(bash, Bash.class);
            return Optional.of(bash);
        } catch (Exception e) {
            logger.error("Database operation failed to create bash: {}", bash, e);
        }
        return Optional.absent();
    }

    public Optional<Bash> getById(String id) {
        try {
            return Optional.of(databaseWrapper.getById(id, Bash.class));
        } catch (Exception e) {
            logger.error("Database operation failed to get bash by id: {}", id, e);
        }
        return Optional.absent();
    }

    public List<Bash> search(Map<String, Object> map) {
        try {
            return databaseWrapper.search(map, Bash.class);
        } catch (Exception e) {
            logger.error("Database operation failed to search for bash with query: {}", map, e);
        }
        return Collections.emptyList();
    }

    public Long delete(String id) {
        Long count;

        Map<String, Object> query = Collections.singletonMap("id", id);
        Map<String, Object> update = Collections.singletonMap("active", false);

        try {
            count = databaseWrapper.findAndUpdate(query, update, Bash.class);
        } catch (Exception e) {
            count = -1L;
            logger.error("Database operation failed to delete bash with query: {}, update: {}", query, update, e);
        }

        return count;
    }
}
