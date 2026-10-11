package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;
import java.sql.SQLException;

/**
 * Controlador del módulo de autenticación, valida el ingreso de los usuarios
 */
public class ControladorAutenticacion {

    private UsuarioDAO usuarioDAO; /* Acceso a la tabla usuarios */

    /**
     * Constructor de la clase ControladorAutenticacion
     */
    public ControladorAutenticacion() {
        this.usuarioDAO = new UsuarioDAO(); /* Inicializar DAO de usuarios */
    }

    /**
     * Valida que exista un usuario con ese correo y contraseña
     * @return El usuario con su rol, o null si las credenciales no son válidas
     */
    public Usuario iniciarSesion(String correo, String contrasena) throws SQLException {
        return usuarioDAO.autenticar(correo, contrasena);
    }
}
