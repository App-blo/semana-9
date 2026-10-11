package dao;

import modelo.Libro;
import modelo.LibroMasPrestado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase LibroDAO con las operaciones CRUD de la tabla libros, más los metodos
 * que modifican el stock y el reporte de libros más prestados
 */
public class LibroDAO implements CrudDAO<Libro> {

    private static final String SELECT_BASE =
            "SELECT l.id, l.titulo, l.autor, l.isbn, l.editorial, l.stock, l.id_categoria, c.nombre AS categoria "
            + "FROM libros l LEFT JOIN categorias c ON l.id_categoria = c.id";

    /**
     * Crea un objeto Libro a partir de la fila actual del ResultSet
     */
    private Libro mapear(ResultSet resultado) throws SQLException {
        Libro libro = new Libro(
                resultado.getInt("id"),
                resultado.getString("titulo"),
                resultado.getString("autor"),
                resultado.getString("isbn"),
                resultado.getString("editorial"),
                resultado.getInt("stock"),
                resultado.getInt("id_categoria"));
        libro.setNombreCategoria(resultado.getString("categoria"));
        return libro;
    }

    @Override
    public void create(Libro libro) throws SQLException {
        PreparedStatement sentencia = null;
        ResultSet claves = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement(
                    "INSERT INTO libros (titulo, autor, isbn, editorial, stock, id_categoria) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, libro.getTitulo());
            sentencia.setString(2, libro.getAutor());
            sentencia.setString(3, libro.getIsbn());
            sentencia.setString(4, libro.getEditorial());
            sentencia.setInt(5, libro.getStock());
            sentencia.setInt(6, libro.getIdCategoria());
            sentencia.executeUpdate();

            claves = sentencia.getGeneratedKeys();
            if (claves.next()) {
                libro.setId(claves.getInt(1)); /* Guardar el id generado */
            }
        } finally {
            DatabaseConnection.cerrar(claves, sentencia);
        }
    }

    @Override
    public List<Libro> readAll() throws SQLException {
        List<Libro> libros = new ArrayList<>();
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement(SELECT_BASE + " ORDER BY l.id");
            resultado = sentencia.executeQuery();

            while (resultado.next()) {
                libros.add(mapear(resultado));
            }
        } finally {
            DatabaseConnection.cerrar(resultado, sentencia);
        }

        return libros;
    }

    @Override
    public boolean update(Libro libro) throws SQLException {
        PreparedStatement sentencia = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement(
                    "UPDATE libros SET titulo = ?, autor = ?, isbn = ?, editorial = ?, stock = ?, id_categoria = ? WHERE id = ?");
            sentencia.setString(1, libro.getTitulo());
            sentencia.setString(2, libro.getAutor());
            sentencia.setString(3, libro.getIsbn());
            sentencia.setString(4, libro.getEditorial());
            sentencia.setInt(5, libro.getStock());
            sentencia.setInt(6, libro.getIdCategoria());
            sentencia.setInt(7, libro.getId());
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
            sentencia = conexion.prepareStatement("DELETE FROM libros WHERE id = ?");
            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        } finally {
            DatabaseConnection.cerrar(sentencia);
        }
    }

    /**
     * Descuenta una unidad de stock. El metodo es synchronized y además la
     * consulta SQL comprueba el stock al mismo tiempo que lo descuenta, así
     * dos préstamos simultáneos nunca dejan el stock en negativo
     * @return true si había stock y se descontó, false si no quedaban ejemplares
     */
    public synchronized boolean descontarStock(int idLibro) throws SQLException {
        PreparedStatement sentencia = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement("UPDATE libros SET stock = stock - 1 WHERE id = ? AND stock > 0");
            sentencia.setInt(1, idLibro);
            return sentencia.executeUpdate() > 0;
        } finally {
            DatabaseConnection.cerrar(sentencia);
        }
    }

    /**
     * Suma una unidad de stock, se usa al devolver un libro. Es synchronized
     * por la misma razón que descontarStock
     */
    public synchronized boolean aumentarStock(int idLibro) throws SQLException {
        PreparedStatement sentencia = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement("UPDATE libros SET stock = stock + 1 WHERE id = ?");
            sentencia.setInt(1, idLibro);
            return sentencia.executeUpdate() > 0;
        } finally {
            DatabaseConnection.cerrar(sentencia);
        }
    }

    /**
     * Reporte: los 10 libros que más veces se han prestado
     */
    public List<LibroMasPrestado> readMasPrestados() throws SQLException {
        List<LibroMasPrestado> libros = new ArrayList<>();
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            Connection conexion = DatabaseConnection.getInstance().getConnection();
            sentencia = conexion.prepareStatement(
                    "SELECT l.titulo, l.autor, COUNT(p.id) AS total "
                    + "FROM libros l INNER JOIN prestamos p ON p.id_libro = l.id "
                    + "GROUP BY l.id, l.titulo, l.autor "
                    + "ORDER BY total DESC, l.titulo LIMIT 10");
            resultado = sentencia.executeQuery();

            while (resultado.next()) {
                libros.add(new LibroMasPrestado(
                        resultado.getString("titulo"),
                        resultado.getString("autor"),
                        resultado.getInt("total")));
            }
        } finally {
            DatabaseConnection.cerrar(resultado, sentencia);
        }

        return libros;
    }
}
