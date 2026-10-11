package modelo;

/**
 * Clase que representa un libro del inventario de la biblioteca
 */
public class Libro {

    private int id;                 /* Identificador, lo genera la base de datos */
    private String titulo;          /* Título del libro */
    private String autor;           /* Autor del libro */
    private String isbn;            /* ISBN, es único por libro */
    private String editorial;       /* Editorial del libro */
    private int stock;              /* Ejemplares disponibles */
    private int idCategoria;        /* Categoría a la que pertenece */
    private String nombreCategoria; /* Nombre de la categoría, solo para mostrar en tablas */

    /**
     * Constructor para un libro nuevo, aún sin id
     */
    public Libro(String titulo, String autor, String isbn, String editorial, int stock, int idCategoria) {
        this.id          = 0;           /* Aún no se guarda, la BD asigna el id */
        this.titulo      = titulo;      /* Definir título */
        this.autor       = autor;       /* Definir autor */
        this.isbn        = isbn;        /* Definir ISBN */
        this.editorial   = editorial;   /* Definir editorial */
        this.stock       = stock;       /* Definir stock */
        this.idCategoria = idCategoria; /* Definir categoría */
    }

    /**
     * Constructor con todos los parametros, se usa al leer un libro desde la base de datos
     */
    public Libro(int id, String titulo, String autor, String isbn, String editorial, int stock, int idCategoria) {
        this.id          = id;          /* Definir identificador */
        this.titulo      = titulo;      /* Definir título */
        this.autor       = autor;       /* Definir autor */
        this.isbn        = isbn;        /* Definir ISBN */
        this.editorial   = editorial;   /* Definir editorial */
        this.stock       = stock;       /* Definir stock */
        this.idCategoria = idCategoria; /* Definir categoría */
    }

    /**
     * Metodo get para devolver los datos del libro
     */
    public int getId() {
        return id; /* Devolver identificador */
    }

    public String getTitulo() {
        return titulo; /* Devolver título */
    }

    public String getAutor() {
        return autor; /* Devolver autor */
    }

    public String getIsbn() {
        return isbn; /* Devolver ISBN */
    }

    public String getEditorial() {
        return editorial; /* Devolver editorial */
    }

    public int getStock() {
        return stock; /* Devolver stock */
    }

    public int getIdCategoria() {
        return idCategoria; /* Devolver categoría */
    }

    public String getNombreCategoria() {
        return nombreCategoria; /* Devolver nombre de la categoría */
    }

    /**
     * Metodo set para modificar los datos del libro
     */
    public void setId(int id) {
        this.id = id; /* Definir id generado por la base de datos */
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo; /* Definir nuevo título */
    }

    public void setAutor(String autor) {
        this.autor = autor; /* Definir nuevo autor */
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn; /* Definir nuevo ISBN */
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial; /* Definir nueva editorial */
    }

    public void setStock(int stock) {
        this.stock = stock; /* Definir nuevo stock */
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria; /* Definir nueva categoría */
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria; /* Definir nombre para mostrar */
    }

    /**
     * Sobreescritura del metodo To String, es el texto que se ve en los JComboBox
     */
    @Override
    public String toString() {
        return id + " - " + titulo + " (stock: " + stock + ")";
    }
}
