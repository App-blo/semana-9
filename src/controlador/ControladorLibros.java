package controlador;

import dao.CategoriaDAO;
import dao.LibroDAO;
import modelo.Categoria;
import modelo.Libro;
import modelo.LibroMasPrestado;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la gestión de libros y categorías. Las SQLException se
 * propagan hacia la vista, que es la que le muestra el mensaje al usuario
 */
public class ControladorLibros {

    private LibroDAO libroDAO;         /* Acceso a la tabla libros */
    private CategoriaDAO categoriaDAO; /* Acceso a la tabla categorias */

    /**
     * Constructor de la clase ControladorLibros
     */
    public ControladorLibros() {
        this.libroDAO     = new LibroDAO();     /* Inicializar DAO de libros */
        this.categoriaDAO = new CategoriaDAO(); /* Inicializar DAO de categorías */
    }

    /* ── Libros ── */

    public void registrarLibro(Libro libro) throws SQLException {
        libroDAO.create(libro);
    }

    public List<Libro> listarLibros() throws SQLException {
        return libroDAO.readAll();
    }

    /**
     * Lista solo los libros que tienen al menos un ejemplar disponible
     */
    public List<Libro> listarLibrosDisponibles() throws SQLException {
        List<Libro> disponibles = new ArrayList<>();

        for (Libro libro : libroDAO.readAll()) {
            if (libro.getStock() > 0) {
                disponibles.add(libro);
            }
        }

        return disponibles;
    }

    public boolean actualizarLibro(Libro libro) throws SQLException {
        return libroDAO.update(libro);
    }

    public boolean eliminarLibro(int id) throws SQLException {
        return libroDAO.delete(id);
    }

    /* ── Categorías ── */

    public void registrarCategoria(Categoria categoria) throws SQLException {
        categoriaDAO.create(categoria);
    }

    public List<Categoria> listarCategorias() throws SQLException {
        return categoriaDAO.readAll();
    }

    public boolean actualizarCategoria(Categoria categoria) throws SQLException {
        return categoriaDAO.update(categoria);
    }

    public boolean eliminarCategoria(int id) throws SQLException {
        return categoriaDAO.delete(id);
    }

    /* ── Reporte ── */

    public List<LibroMasPrestado> listarLibrosMasPrestados() throws SQLException {
        return libroDAO.readMasPrestados();
    }
}
