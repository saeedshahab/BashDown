package com.saeedshahab.bashdown.wrappers.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.saeedshahab.bashdown.annotations.CommonName;
import com.saeedshahab.bashdown.core.BashConfiguration;
import com.saeedshahab.bashdown.service.mongo.MongoConnection;
import com.saeedshahab.bashdown.wrappers.DatabaseWrapper;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

public class MongoWrapper implements DatabaseWrapper<MongoDatabase> {

    private static final Logger logger = LoggerFactory.getLogger(MongoWrapper.class);

    private static MongoConnection mongoConnection;

    private final BashConfiguration configuration;

    @Inject
    public MongoWrapper(BashConfiguration bashConfiguration) {
        this.configuration = bashConfiguration;
    }

    @Override
    public MongoDatabase getDatabase() {
        if (Objects.isNull(mongoConnection)) {
            mongoConnection = MongoConnection.newConnection(
                    configuration.getDatabaseHost(),
                    configuration.getDatabasePort(),
                    configuration.getDatabaseName());
        }
        return mongoConnection.getConnection();
    }

    private <T> MongoCollection<T> getCollection(Class<T> type) {
        return getDatabase().getCollection(type.getAnnotation(CommonName.class).value(), type);
    }

    @Override
    public <T> T create(T t, Class<T> type) {
        logger.debug("Create for type: {}, bean: {}", type, t);

        getCollection(type).insertOne(t);

        logger.debug("Create for type: {}, bean: {}, result: {}", type, t, true);

        return t;
    }

    @Override
    public <T> T getById(String id, Class<T> type) {
        logger.debug("Find for type: {}, id: {}", type, id);

        T result = getCollection(type).find(new Document("id", id)).first();

        logger.debug("Find for type: {}, id: {}, result: {}", type, id, result);

        return result;
    }

    @Override
    public <T> List<T> search(Map<String, Object> map, Class<T> type) {
        logger.debug("Search for type: {}, query: {}", type, map);

        List<T> elements = new ArrayList<>();
        getCollection(type).find(new Document(map)).iterator().forEachRemaining(elements::add);

        logger.debug("Search for type: {}, query: {}, result: {}", type, map, elements);

        return elements;
    }

    @Override
    public <T> Long findAndUpdate(Map<String, Object> query, Map<String, Object> updateFields, Class<T> type) {
        logger.debug("Update for type: {}, query: {}, updateFields: {}", type, query, updateFields);

        UpdateResult result = getCollection(type).updateOne(
                new Document(query),
                new Document(Collections.singletonMap("$set", updateFields)));
        Long count = result.getMatchedCount();

        logger.debug("Update for type: {}, query: {}, updateFields: {}, countUpdated: {}", type, query, updateFields, count);

        return count;
    }

    @Override
    public <T> T deleteById(String id, Class<T> type) {
        logger.debug("Delete for type: {}, id: {}", type, id);

        T result = getCollection(type).findOneAndDelete(new Document("id", id));

        logger.debug("Delete for type: {}, id: {}, result: {}", type, id, result);

        return result;
    }

    @Override
    public boolean health() {
        try {
            getDatabase().runCommand(new Document("ping", 1));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public <T> void dropDatabase(Class<T> type) {
        getCollection(type).drop();
    }
}
