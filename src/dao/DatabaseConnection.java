package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase DatabaseConnection que aplica el patrón Singleton: existe una única
 * instancia que administra una única conexión JDBC a la base de datos MySQL
 * de la biblioteca
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/biblioteca"; /* Dirección de la base de datos */
    private static final String USER     = "root";                                   /* Usuario de MySQL */
    private static final String PASSWORD = "";                                       /* Contraseña de tu MySQL */

    private static DatabaseConnection instancia; /* Única instancia de la clase */
    private Connection conexion;                 /* Única conexión a la base de datos */

    /**
     * Constructor privado, evita que se creen instancias desde afuera
     */
    private DatabaseConnection() {
    }

    /**
     * Devuelve la única instancia de la clase, la crea la primera vez que se pide
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instancia == null) {
            instancia = new DatabaseConnection();
        }
        return instancia;
    }

    /**
     * Devuelve la conexión, si todavía no existe o se cerró la vuelve a abrir
     */
    public synchronized Connection getConnection() throws SQLException {
        try {
            if (conexion == null || conexion.isClosed() || !conexion.isValid(2)) {
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
            throw e;
        }
        return conexion;
    }

    /**
     * Cierra la conexión, se usa al terminar la aplicación
     */
    public synchronized void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
        }
        conexion = null;
    }

    /**
     * Cierra los recursos JDBC recibidos (ResultSet y Statement). La conexión
     * no se cierra aquí porque es compartida por toda la aplicación
     */
    public static void cerrar(AutoCloseable... recursos) {
        for (AutoCloseable recurso : recursos) {
            try {
                if (recurso != null) {
                    recurso.close(); /* Cerrar el recurso si fue creado */
                }
            } catch (Exception e) {
                System.out.println("Error al cerrar un recurso: " + e.getMessage());
            }
        }
    }
}
