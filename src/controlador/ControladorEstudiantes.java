package controlador;

import dao.EstudianteDAO;
import dao.UsuarioDAO;
import modelo.Estudiante;
import modelo.Rol;
import modelo.Usuario;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador de la gestión de estudiantes. Al registrar un estudiante también
 * se crea su usuario con rol estudiante, así puede ingresar al sistema
 */
public class ControladorEstudiantes {

    private EstudianteDAO estudianteDAO; /* Acceso a la tabla estudiantes */
    private UsuarioDAO usuarioDAO;       /* Acceso a la tabla usuarios */

    /**
     * Constructor de la clase ControladorEstudiantes
     */
    public ControladorEstudiantes() {
        this.estudianteDAO = new EstudianteDAO(); /* Inicializar DAO de estudiantes */
        this.usuarioDAO    = new UsuarioDAO();    /* Inicializar DAO de usuarios */
    }

    /**
     * Guarda el estudiante y su usuario de acceso. Si falla la creación del
     * usuario se elimina el estudiante recién creado para no dejar datos a medias
     */
    public void registrarEstudiante(Estudiante estudiante, String contrasena) throws SQLException {
        estudianteDAO.create(estudiante);

        try {
            usuarioDAO.create(new Usuario(estudiante.getNombre(), estudiante.getRut(),
                    estudiante.getCorreo(), contrasena, Rol.ESTUDIANTE));
        } catch (SQLException e) {
            estudianteDAO.delete(estudiante.getId());
            throw e;
        }
    }

    public List<Estudiante> listarEstudiantes() throws SQLException {
        return estudianteDAO.readAll();
    }

    /**
     * Actualiza el estudiante y su usuario vinculado (por RUT)
     * @param rutOriginal     RUT que tenía el estudiante antes de editarlo
     * @param contrasenaNueva Nueva contraseña, o null para dejar la actual
     */
    public boolean actualizarEstudiante(Estudiante estudiante, String rutOriginal, String contrasenaNueva)
            throws SQLException {
        boolean actualizado = estudianteDAO.update(estudiante);

        if (actualizado) {
            usuarioDAO.actualizarPorRut(rutOriginal, estudiante.getNombre(), estudiante.getRut(),
                    estudiante.getCorreo(), contrasenaNueva);
        }

        return actualizado;
    }

    /**
     * Elimina el estudiante y su usuario vinculado. Si el estudiante tiene
     * préstamos registrados la base de datos lo impide y se lanza la excepción
     */
    public boolean eliminarEstudiante(Estudiante estudiante) throws SQLException {
        boolean eliminado = estudianteDAO.delete(estudiante.getId());

        if (eliminado) {
            usuarioDAO.eliminarPorRut(estudiante.getRut());
        }

        return eliminado;
    }

    /**
     * Busca la ficha de estudiante que corresponde a un usuario, por su RUT
     * @return El estudiante, o null si el usuario no tiene ficha de estudiante
     */
    public Estudiante buscarPorRut(String rut) throws SQLException {
        return estudianteDAO.readByRut(rut);
    }
}
