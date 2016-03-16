package com.saeedshahab.bashdown.wrappers;

import java.util.List;
import java.util.Map;

public interface DatabaseWrapper<U> {

    U getDatabase();

    <T> T create(T t, Class<T> type);

    <T> T getById(String id, Class<T> type);

    <T> List<T> search(Map<String, Object> query, Class<T> type);

    <T> Long findAndUpdate(Map<String, Object> query, Map<String, Object> updateFields, Class<T> type);
}
