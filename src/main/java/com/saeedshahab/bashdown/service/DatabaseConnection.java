package com.saeedshahab.bashdown.service;

public interface DatabaseConnection<T> {

    void connect();

    T connection();
}
