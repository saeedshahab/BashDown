package com.saeedshahab.bashdown.service.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.saeedshahab.bashdown.service.DatabaseConnection;
import fr.javatic.mongo.jacksonCodec.JacksonCodecProvider;
import fr.javatic.mongo.jacksonCodec.ObjectMapperFactory;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.Collections;
import java.util.Objects;

public class MongoConnection implements DatabaseConnection<MongoDatabase> {

    private final String databaseHost;
    private final Integer databasePort;
    private final String databaseName;

    private MongoConnection(String databaseHost, Integer databasePort, String databaseName) {
        this.databaseHost = databaseHost;
        this.databasePort = databasePort;
        this.databaseName = databaseName;
    }

    public static MongoConnection newConnection(String databaseHost, Integer databasePort, String databaseName) {
        return new MongoConnection(databaseHost, databasePort, databaseName);
    }

    private static MongoDatabase database;

    private void connect() {
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(new JacksonCodecProvider(ObjectMapperFactory.createObjectMapper()))
        );
        MongoClientOptions clientOptions = MongoClientOptions.builder()
                .codecRegistry(codecRegistry)
                .build();
        MongoClient mongo = new MongoClient(
                new ServerAddress(databaseHost, databasePort),
                Collections.singletonList(MongoCredential.createCredential(databaseName, databaseName, databaseName.toCharArray())),
                clientOptions);
        database = mongo.getDatabase(databaseName);
    }

    @Override
    public MongoDatabase getConnection() {
        if (Objects.isNull(database)) {
            connect();
        }

        return database;
    }
}
