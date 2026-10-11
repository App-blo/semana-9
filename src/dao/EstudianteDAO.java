package dao;

import modelo.Estudiante;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase EstudianteDAO con las operaciones CRUD de la tabla estudiantes
 */
public class EstudianteDAO implements CrudDAO<Estudiante> {

    private static final String SELECT_BASE = "SELECT id, nombre, rut, curso, correo FROM estudiantes";

    /**
     * Crea un objeto Estudiante a partir de la fila actual del ResultSet
     */
    private Estudiante mapear(ResultSet resultado) throws SQLException {
        return new Estudiante(
                resultado.getInt("id"),
                resultado.getString("nombre"),
                resultado.getString("rut"),
                resultado.getString("curso"),
                resultado.getString("correo"));
    }

    @Override
    public void create(Estudiante estudiante) throws SQLException {
        PreparedStatement sentencia = null;
        ResultSet claves = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement(
                    "INSERT INTO estudiantes (nombre, rut, curso, correo) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, estudiante.getNombre());
            sentencia.setString(2, estudiante.getRut());
            sentencia.setString(3, estudiante.getCurso());
            sentencia.setString(4, estudiante.getCorreo());
            sentencia.executeUpdate();

            claves = sentencia.getGeneratedKeys();
            if (claves.next()) {
                estudiante.setId(claves.getInt(1)); /* Guardar el id generado */
            }
        } finally {
            DatabaseConnection.cerrar(claves, sentencia);
        }
    }

    @Override
    public List<Estudiante> readAll() throws SQLException {
        List<Estudiante> estudiantes = new ArrayList<>();
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement(SELECT_BASE + " ORDER BY id");
            resultado = sentencia.executeQuery();

            while (resultado.next()) {
                estudiantes.add(mapear(resultado));
            }
        } finally {
            DatabaseConnection.cerrar(resultado, sentencia);
        }

        return estudiantes;
    }

    @Override
    public boolean update(Estudiante estudiante) throws SQLException {
        PreparedStatement sentencia = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement(
                    "UPDATE estudiantes SET nombre = ?, rut = ?, curso = ?, correo = ? WHERE id = ?");
            sentencia.setString(1, estudiante.getNombre());
            sentencia.setString(2, estudiante.getRut());
            sentencia.setString(3, estudiante.getCurso());
            sentencia.setString(4, estudiante.getCorreo());
            sentencia.setInt(5, estudiante.getId());
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
            sentencia = conexion.prepareStatement("DELETE FROM estudiantes WHERE id = ?");
            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        } finally {
            DatabaseConnection.cerrar(sentencia);
        }
    }

    /**
     * Busca al estudiante con ese RUT, se usa para vincular un usuario con su ficha
     * @return El estudiante, o null si no existe
     */
    public Estudiante readByRut(String rut) throws SQLException {
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement(SELECT_BASE + " WHERE rut = ?");
            sentencia.setString(1, rut);
            resultado = sentencia.executeQuery();

            if (resultado.next()) {
                return mapear(resultado);
            }
            return null;
        } finally {
            DatabaseConnection.cerrar(resultado, sentencia);
        }
    }
}
