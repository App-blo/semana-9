package controlador;

import dao.LibroDAO;
import dao.PrestamoDAO;
import modelo.Prestamo;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de préstamos y devoluciones. Las operaciones que modifican el
 * stock son synchronized y se pueden ejecutar en un hilo en segundo plano
 * para no congelar la interfaz
 */
public class ControladorPrestamos {

    private static final int DIAS_PRESTAMO = 7; /* Días que se presta un libro antes de vencer */

    private PrestamoDAO prestamoDAO; /* Acceso a la tabla prestamos */
    private LibroDAO libroDAO;       /* Acceso a la tabla libros, para actualizar el stock */

    /**
     * Constructor de la clase ControladorPrestamos
     */
    public ControladorPrestamos() {
        this.prestamoDAO = new PrestamoDAO(); /* Inicializar DAO de préstamos */
        this.libroDAO    = new LibroDAO();    /* Inicializar DAO de libros */
    }

    /**
     * Registra un préstamo: descuenta el stock y guarda el préstamo, que a la vez
     * queda como historial del estudiante. La fecha de vencimiento se calcula
     * automáticamente. Es synchronized para que dos préstamos simultáneos no
     * puedan leer y modificar el stock al mismo tiempo (condición de carrera)
     * @return El préstamo registrado, o null si no hay stock disponible
     */
    public synchronized Prestamo registrarPrestamo(int idEstudiante, int idLibro) throws SQLException {

        /* Descontar el stock solo si hay ejemplares */
        if (!libroDAO.descontarStock(idLibro)) {
            return null;
        }

        LocalDate hoy = LocalDate.now();
        Prestamo prestamo = new Prestamo(idEstudiante, idLibro, hoy, hoy.plusDays(DIAS_PRESTAMO));

        try {
            prestamoDAO.create(prestamo);
        } catch (SQLException e) {
            libroDAO.aumentarStock(idLibro); /* Devolver el stock si no se pudo guardar el préstamo */
            throw e;
        }

        return prestamo;
    }

    /**
     * Registra la devolución de un préstamo y vuelve a sumar el stock del libro.
     * También es synchronized porque modifica el stock
     * @return Los días de atraso (0 si se devolvió a tiempo), o -1 si el
     *         préstamo no existe o ya estaba devuelto
     */
    public synchronized long registrarDevolucion(int idPrestamo) throws SQLException {
        Prestamo prestamo = prestamoDAO.readById(idPrestamo);

        if (prestamo == null || prestamo.isDevuelto()) {
            return -1;
        }

        if (!prestamoDAO.marcarDevuelto(idPrestamo)) {
            return -1;
        }

        libroDAO.aumentarStock(prestamo.getIdLibro());
        return prestamo.getDiasAtraso();
    }

    /**
     * Ejecuta registrarPrestamo en un hilo independiente, así la interfaz no se
     * bloquea mientras se consulta y actualiza la base de datos
     */
    public void registrarPrestamoEnSegundoPlano(final int idEstudiante, final int idLibro,
                                                final ResultadoOperacion resultado) {
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Prestamo prestamo = registrarPrestamo(idEstudiante, idLibro);

                    if (prestamo == null) {
                        resultado.alTerminar(false, "No hay stock disponible de ese libro.");
                    } else {
                        resultado.alTerminar(true, "Préstamo registrado. Fecha de vencimiento: "
                                + prestamo.getFechaDevolucion() + ".");
                    }
                } catch (SQLException e) {
                    resultado.alTerminar(false, "No se pudo registrar el préstamo: " + e.getMessage());
                }
            }
        }, "hilo-prestamo");

        hilo.start();
    }

    /**
     * Ejecuta registrarDevolucion en un hilo independiente
     */
    public void registrarDevolucionEnSegundoPlano(final int idPrestamo, final ResultadoOperacion resultado) {
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long atraso = registrarDevolucion(idPrestamo);

                    if (atraso < 0) {
                        resultado.alTerminar(false, "El préstamo no existe o ya estaba devuelto.");
                    } else if (atraso > 0) {
                        resultado.alTerminar(true, "Devolución registrada con " + atraso
                                + " días de atraso.");
                    } else {
                        resultado.alTerminar(true, "Devolución registrada, el libro se devolvió a tiempo.");
                    }
                } catch (SQLException e) {
                    resultado.alTerminar(false, "No se pudo registrar la devolución: " + e.getMessage());
                }
            }
        }, "hilo-devolucion");

        hilo.start();
    }

    /**
     * Lista todos los préstamos
     */
    public List<Prestamo> listarPrestamos() throws SQLException {
        return prestamoDAO.readAll();
    }

    /**
     * Lista el historial de préstamos de un estudiante
     */
    public List<Prestamo> listarHistorialEstudiante(int idEstudiante) throws SQLException {
        return prestamoDAO.readByEstudiante(idEstudiante);
    }

    /**
     * Lista los libros que están actualmente en préstamo
     */
    public List<Prestamo> listarPrestamosActivos() throws SQLException {
        return prestamoDAO.readActivos();
    }
}
