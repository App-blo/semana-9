package dao;

import modelo.Prestamo;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase PrestamoDAO con las operaciones CRUD de la tabla prestamos y las
 * consultas que usan los reportes
 */
public class PrestamoDAO implements CrudDAO<Prestamo> {

    private static final String SELECT_BASE =
            "SELECT p.id, p.id_estudiante, p.id_libro, p.fecha_prestamo, p.fecha_devolucion, p.devuelto, "
            + "e.nombre AS estudiante, l.titulo AS libro "
            + "FROM prestamos p "
            + "INNER JOIN estudiantes e ON p.id_estudiante = e.id "
            + "INNER JOIN libros l ON p.id_libro = l.id";

    /**
     * Convierte una fecha de la base de datos a LocalDate, respetando los valores nulos
     */
    private LocalDate aLocalDate(Date fecha) {
        return (fecha == null) ? null : fecha.toLocalDate();
    }

    /**
     * Crea un objeto Prestamo a partir de la fila actual del ResultSet
     */
    private Prestamo mapear(ResultSet resultado) throws SQLException {
        Prestamo prestamo = new Prestamo(
                resultado.getInt("id"),
                resultado.getInt("id_estudiante"),
                resultado.getInt("id_libro"),
                aLocalDate(resultado.getDate("fecha_prestamo")),
                aLocalDate(resultado.getDate("fecha_devolucion")),
                resultado.getBoolean("devuelto"));
        prestamo.setNombreEstudiante(resultado.getString("estudiante"));
        prestamo.setTituloLibro(resultado.getString("libro"));
        return prestamo;
    }

    /**
     * Ejecuta la consulta base agregando una condición opcional y, si la condición
     * tiene un parametro, su valor
     */
    private List<Prestamo> consultar(String condicion, Integer parametro) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement(SELECT_BASE + " " + condicion + " ORDER BY p.id");
            if (parametro != null) {
                sentencia.setInt(1, parametro);
            }
            resultado = sentencia.executeQuery();

            while (resultado.next()) {
                prestamos.add(mapear(resultado));
            }
        } finally {
            DatabaseConnection.cerrar(resultado, sentencia);
        }

        return prestamos;
    }

    @Override
    public void create(Prestamo prestamo) throws SQLException {
        PreparedStatement sentencia = null;
        ResultSet claves = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement(
                    "INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) "
                    + "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            sentencia.setInt(1, prestamo.getIdEstudiante());
            sentencia.setInt(2, prestamo.getIdLibro());
            sentencia.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            sentencia.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            sentencia.setBoolean(5, prestamo.isDevuelto());
            sentencia.executeUpdate();

            claves = sentencia.getGeneratedKeys();
            if (claves.next()) {
                prestamo.setId(claves.getInt(1)); /* Guardar el id generado */
            }
        } finally {
            DatabaseConnection.cerrar(claves, sentencia);
        }
    }

    @Override
    public List<Prestamo> readAll() throws SQLException {
        return consultar("", null);
    }

    @Override
    public boolean update(Prestamo prestamo) throws SQLException {
        PreparedStatement sentencia = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement(
                    "UPDATE prestamos SET id_estudiante = ?, id_libro = ?, fecha_prestamo = ?, "
                    + "fecha_devolucion = ?, devuelto = ? WHERE id = ?");
            sentencia.setInt(1, prestamo.getIdEstudiante());
            sentencia.setInt(2, prestamo.getIdLibro());
            sentencia.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            sentencia.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            sentencia.setBoolean(5, prestamo.isDevuelto());
            sentencia.setInt(6, prestamo.getId());
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
            sentencia = conexion.prepareStatement("DELETE FROM prestamos WHERE id = ?");
            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        } finally {
            DatabaseConnection.cerrar(sentencia);
        }
    }

    /**
     * Busca un préstamo por su id
     * @return El préstamo, o null si no existe
     */
    public Prestamo readById(int id) throws SQLException {
        List<Prestamo> resultado = consultar("WHERE p.id = ?", id);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    /**
     * Reporte: historial de préstamos de un estudiante
     */
    public List<Prestamo> readByEstudiante(int idEstudiante) throws SQLException {
        return consultar("WHERE p.id_estudiante = ?", idEstudiante);
    }

    /**
     * Reporte: libros que están actualmente en préstamo (sin devolver)
     */
    public List<Prestamo> readActivos() throws SQLException {
        return consultar("WHERE p.devuelto = FALSE", null);
    }

    /**
     * Marca el préstamo como devuelto, solo si todavía no lo estaba
     * @return true si se marcó, false si no existe o ya estaba devuelto
     */
    public boolean marcarDevuelto(int idPrestamo) throws SQLException {
        PreparedStatement sentencia = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement("UPDATE prestamos SET devuelto = TRUE WHERE id = ? AND devuelto = FALSE");
            sentencia.setInt(1, idPrestamo);
            return sentencia.executeUpdate() > 0;
        } finally {
            DatabaseConnection.cerrar(sentencia);
        }
    }
}
