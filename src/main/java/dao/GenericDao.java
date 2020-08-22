package dao;

import java.util.List;

public interface GenericDao<T> {

    T findOne(int id);
    List<T> findAll();
    void create(T t);
    void update(T t);
    void delete(T t);
    void deleteById(int id);
}
