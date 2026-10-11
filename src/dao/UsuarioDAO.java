package dao;

import modelo.Rol;
import modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase UsuarioDAO con las operaciones CRUD de la tabla usuarios y la
 * consulta que valida el ingreso al sistema
 */
public class UsuarioDAO implements CrudDAO<Usuario> {

    private static final String SELECT_BASE = "SELECT id, nombre, rut, correo, contraseña, rol FROM usuarios";

    /**
     * Crea un objeto Usuario a partir de la fila actual del ResultSet
     */
    private Usuario mapear(ResultSet resultado) throws SQLException {
        return new Usuario(
                resultado.getInt("id"),
                resultado.getString("nombre"),
                resultado.getString("rut"),
                resultado.getString("correo"),
                resultado.getString("contraseña"),
                Rol.desdeBD(resultado.getString("rol")));
    }

    @Override
    public void create(Usuario usuario) throws SQLException {
        PreparedStatement sentencia = null;
        ResultSet claves = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement(
                    "INSERT INTO usuarios (nombre, rut, correo, contraseña, rol) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, usuario.getNombre());
            sentencia.setString(2, usuario.getRut());
            sentencia.setString(3, usuario.getCorreo());
            sentencia.setString(4, usuario.getContrasena());
            sentencia.setString(5, usuario.getRol().paraBD());
            sentencia.executeUpdate();

            claves = sentencia.getGeneratedKeys();
            if (claves.next()) {
                usuario.setId(claves.getInt(1)); /* Guardar el id generado */
            }
        } finally {
            DatabaseConnection.cerrar(claves, sentencia);
        }
    }

    @Override
    public List<Usuario> readAll() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement(SELECT_BASE + " ORDER BY id");
            resultado = sentencia.executeQuery();

            while (resultado.next()) {
                usuarios.add(mapear(resultado));
            }
        } finally {
            DatabaseConnection.cerrar(resultado, sentencia);
        }

        return usuarios;
    }

    @Override
    public boolean update(Usuario usuario) throws SQLException {
        PreparedStatement sentencia = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement(
                    "UPDATE usuarios SET nombre = ?, rut = ?, correo = ?, contraseña = ?, rol = ? WHERE id = ?");
            sentencia.setString(1, usuario.getNombre());
            sentencia.setString(2, usuario.getRut());
            sentencia.setString(3, usuario.getCorreo());
            sentencia.setString(4, usuario.getContrasena());
            sentencia.setString(5, usuario.getRol().paraBD());
            sentencia.setInt(6, usuario.getId());
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
            sentencia = conexion.prepareStatement("DELETE FROM usuarios WHERE id = ?");
            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        } finally {
            DatabaseConnection.cerrar(sentencia);
        }
    }

    /**
     * Busca al usuario que tenga ese correo y contraseña
     * @return El usuario, o null si las credenciales no existen
     */
    public Usuario autenticar(String correo, String contrasena) throws SQLException {
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement(SELECT_BASE + " WHERE correo = ? AND contraseña = ?");
            sentencia.setString(1, correo);
            sentencia.setString(2, contrasena);
            resultado = sentencia.executeQuery();

            if (resultado.next()) {
                return mapear(resultado);
            }
            return null;
        } finally {
            DatabaseConnection.cerrar(resultado, sentencia);
        }
    }

    /**
     * Actualiza el usuario vinculado a un estudiante, identificado por su RUT original.
     * La contraseña solo se cambia si se entrega una nueva
     */
    public boolean actualizarPorRut(String rutOriginal, String nombre, String rut, String correo,
                                    String contrasenaNueva) throws SQLException {
        PreparedStatement sentencia = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();

            if (contrasenaNueva == null) {
                sentencia = conexion.prepareStatement(
                        "UPDATE usuarios SET nombre = ?, rut = ?, correo = ? WHERE rut = ?");
                sentencia.setString(4, rutOriginal);
            } else {
                sentencia = conexion.prepareStatement(
                        "UPDATE usuarios SET nombre = ?, rut = ?, correo = ?, contraseña = ? WHERE rut = ?");
                sentencia.setString(4, contrasenaNueva);
                sentencia.setString(5, rutOriginal);
            }
            sentencia.setString(1, nombre);
            sentencia.setString(2, rut);
            sentencia.setString(3, correo);

            return sentencia.executeUpdate() > 0;
        } finally {
            DatabaseConnection.cerrar(sentencia);
        }
    }

    /**
     * Elimina el usuario vinculado a un estudiante, identificado por su RUT
     */
    public boolean eliminarPorRut(String rut) throws SQLException {
        PreparedStatement sentencia = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement("DELETE FROM usuarios WHERE rut = ?");
            sentencia.setString(1, rut);
            return sentencia.executeUpdate() > 0;
        } finally {
            DatabaseConnection.cerrar(sentencia);
        }
    }
}
