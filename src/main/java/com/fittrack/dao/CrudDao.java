package com.fittrack.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Basic operations every table-backed DAO supports.
 * T is the model class the DAO works with.
 */
public interface CrudDao<T> {

    List<T> findAll() throws SQLException;

    T findById(int id) throws SQLException;

    /** inserts the row and copies the generated id back into the object */
    boolean save(T item) throws SQLException;

    boolean update(T item) throws SQLException;

    boolean delete(int id) throws SQLException;
}
