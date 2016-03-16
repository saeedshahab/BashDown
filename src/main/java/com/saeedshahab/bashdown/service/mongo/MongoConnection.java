package com.saeedshahab.bashdown.service.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoDatabase;
import com.saeedshahab.bashdown.service.DatabaseConnection;
import fr.javatic.mongo.jacksonCodec.JacksonCodecProvider;
import fr.javatic.mongo.jacksonCodec.ObjectMapperFactory;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.Objects;

public class MongoConnection implements DatabaseConnection<MongoDatabase> {

    private final String databaseHost;
    private final Integer databasePort;
    private final String databaseName;

    public MongoConnection(String databaseHost, Integer databasePort, String databaseName) {
        this.databaseHost = databaseHost;
        this.databasePort = databasePort;
        this.databaseName = databaseName;
    }

    private static MongoDatabase database;

    @Override
    public void connect() {
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(new JacksonCodecProvider(ObjectMapperFactory.createObjectMapper()))
        );
        MongoClientOptions clientOptions = MongoClientOptions.builder()
                .codecRegistry(codecRegistry)
                .build();
        MongoClient mongo = new MongoClient(databaseHost + ":" + databasePort, clientOptions);
        database = mongo.getDatabase(databaseName);
    }

    @Override
    public MongoDatabase connection() {
        if (Objects.isNull(database)) {
            connect();
        }

        return database;
    }
}
