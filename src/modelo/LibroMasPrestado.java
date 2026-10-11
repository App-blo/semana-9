package modelo;

/**
 * Clase que representa una fila del reporte de libros más prestados
 */
public class LibroMasPrestado {

    private String titulo;       /* Título del libro */
    private String autor;        /* Autor del libro */
    private int totalPrestamos;  /* Cantidad de veces que fue prestado */

    /**
     * Constructor con parametros de la clase LibroMasPrestado
     */
    public LibroMasPrestado(String titulo, String autor, int totalPrestamos) {
        this.titulo         = titulo;         /* Definir título */
        this.autor          = autor;          /* Definir autor */
        this.totalPrestamos = totalPrestamos; /* Definir total de préstamos */
    }

    /**
     * Metodo get para devolver los datos del reporte
     */
    public String getTitulo() {
        return titulo; /* Devolver título */
    }

    public String getAutor() {
        return autor; /* Devolver autor */
    }

    public int getTotalPrestamos() {
        return totalPrestamos; /* Devolver total de préstamos */
    }
}
