package dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz que define las operaciones CRUD que debe tener cada DAO
 */
public interface CrudDAO<T> {

    /**
     * Inserta la entidad y le asigna el id generado por la base de datos
     */
    void create(T entidad) throws SQLException;

    /**
     * Lee todas las entidades de la tabla
     */
    List<T> readAll() throws SQLException;

    /**
     * Actualiza una entidad existente, devuelve false si el id no existe
     */
    boolean update(T entidad) throws SQLException;

    /**
     * Elimina la entidad con el id indicado, devuelve false si el id no existe
     */
    boolean delete(int id) throws SQLException;
}
