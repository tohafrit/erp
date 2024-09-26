package ru.korundm.dao;

import java.util.List;

public interface CommonService<T> {

    List<T> getAll();
    List<T> getAllById(List<Long> idList);
    T save(T object);
    List<T> saveAll(List<T> objectList);
    T read(long id);
    void delete(T object);
    void deleteById(long id);
}