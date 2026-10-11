package vista;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Clase con los mensajes que se repiten en todas las ventanas, así los
 * errores de validación y de base de datos se muestran siempre igual
 */
public class Mensajes {

    /**
     * Arma un texto claro para el usuario a partir de una SQLException
     * @param accion Qué se estaba intentando hacer, por ejemplo "No se pudo eliminar el libro"
     */
    public static String textoError(String accion, SQLException e) {

        /* Código 1062 de MySQL: se repitió un valor único (RUT, ISBN, correo) */
        if (e.getErrorCode() == 1062) {
            return accion + ": ya existe un registro con ese valor "
                    + "(por ejemplo, un RUT o un ISBN repetido).";
        }

        /* Se violó una llave foránea, por ejemplo eliminar algo que tiene préstamos */
        if (e instanceof SQLIntegrityConstraintViolationException) {
            return accion + ": el registro está relacionado con otros datos "
                    + "(por ejemplo, tiene préstamos asociados).";
        }

        /* Los códigos SQLState que empiezan con 08 son errores de conexión */
        if (e.getSQLState() != null && e.getSQLState().startsWith("08")) {
            return accion + ": no se pudo conectar con la base de datos. "
                    + "Revisa que MySQL esté encendido, el conector JDBC agregado y los datos de DatabaseConnection.";
        }

        return accion + ": " + e.getMessage();
    }

    /**
     * Muestra un mensaje de error de base de datos
     */
    public static void errorSQL(Component padre, String accion, SQLException e) {
        JOptionPane.showMessageDialog(padre, textoError(accion, e),
                "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra una advertencia por un dato mal ingresado
     */
    public static void advertencia(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Datos inválidos", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Muestra un mensaje informativo de operación exitosa
     */
    public static void exito(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Operación exitosa", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Pregunta al usuario si confirma una acción
     * @return true si el usuario eligió Sí
     */
    public static boolean confirmar(Component padre, String pregunta) {
        int respuesta = JOptionPane.showConfirmDialog(padre, pregunta, "Confirmar", JOptionPane.YES_NO_OPTION);
        return respuesta == JOptionPane.YES_OPTION;
    }
}
