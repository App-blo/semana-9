package dao;

import modelo.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase CategoriaDAO con las operaciones CRUD de la tabla categorias
 */
public class CategoriaDAO implements CrudDAO<Categoria> {

    @Override
    public void create(Categoria categoria) throws SQLException {
        PreparedStatement sentencia = null;
        ResultSet claves = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement("INSERT INTO categorias (nombre) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, categoria.getNombre());
            sentencia.executeUpdate();

            claves = sentencia.getGeneratedKeys();
            if (claves.next()) {
                categoria.setId(claves.getInt(1)); /* Guardar el id generado */
            }
        } finally {
            DatabaseConnection.cerrar(claves, sentencia);
        }
    }

    @Override
    public List<Categoria> readAll() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement("SELECT id, nombre FROM categorias ORDER BY nombre");
            resultado = sentencia.executeQuery();

            while (resultado.next()) {
                categorias.add(new Categoria(resultado.getInt("id"), resultado.getString("nombre")));
            }
        } finally {
            DatabaseConnection.cerrar(resultado, sentencia);
        }

        return categorias;
    }

    @Override
    public boolean update(Categoria categoria) throws SQLException {
        PreparedStatement sentencia = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement("UPDATE categorias SET nombre = ? WHERE id = ?");
            sentencia.setString(1, categoria.getNombre());
            sentencia.setInt(2, categoria.getId());
            return sentencia.executeUpdate() > 0;
        } finally {
            DatabaseConnection.cerrar(sentencia);
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        PreparedStatement sentencia = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement("DELETE FROM categorias WHERE id = ?");
            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        } finally {
            DatabaseConnection.cerrar(sentencia);
        }
    }
}
