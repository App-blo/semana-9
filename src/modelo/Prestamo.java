package modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Clase que representa el préstamo de un libro a un estudiante. La tabla
 * prestamos también sirve como historial de cada estudiante
 */
public class Prestamo {

    private int id;                  /* Identificador, lo genera la base de datos */
    private int idEstudiante;        /* Estudiante que recibe el libro */
    private int idLibro;             /* Libro prestado */
    private LocalDate fechaPrestamo; /* Fecha en que se prestó el libro */
    private LocalDate fechaDevolucion; /* Fecha límite de devolución (vencimiento) */
    private boolean devuelto;        /* true cuando el libro ya fue devuelto */
    private String nombreEstudiante; /* Nombre del estudiante, solo para mostrar en tablas */
    private String tituloLibro;      /* Título del libro, solo para mostrar en tablas */

    /**
     * Constructor para un préstamo nuevo, aún sin id y sin devolver
     */
    public Prestamo(int idEstudiante, int idLibro, LocalDate fechaPrestamo, LocalDate fechaDevolucion) {
        this.id              = 0;                /* Aún no se guarda, la BD asigna el id */
        this.idEstudiante    = idEstudiante;     /* Definir estudiante */
        this.idLibro         = idLibro;          /* Definir libro */
        this.fechaPrestamo   = fechaPrestamo;    /* Definir fecha de préstamo */
        this.fechaDevolucion = fechaDevolucion;  /* Definir fecha de vencimiento */
        this.devuelto        = false;            /* Nace sin devolver */
    }

    /**
     * Constructor con todos los parametros, se usa al leer un préstamo desde la base de datos
     */
    public Prestamo(int id, int idEstudiante, int idLibro, LocalDate fechaPrestamo,
                    LocalDate fechaDevolucion, boolean devuelto) {
        this.id              = id;               /* Definir identificador */
        this.idEstudiante    = idEstudiante;     /* Definir estudiante */
        this.idLibro         = idLibro;          /* Definir libro */
        this.fechaPrestamo   = fechaPrestamo;    /* Definir fecha de préstamo */
        this.fechaDevolucion = fechaDevolucion;  /* Definir fecha de vencimiento */
        this.devuelto        = devuelto;         /* Definir si fue devuelto */
    }

    /**
     * Metodo get para devolver los datos del préstamo
     */
    public int getId() {
        return id; /* Devolver identificador */
    }

    public int getIdEstudiante() {
        return idEstudiante; /* Devolver id del estudiante */
    }

    public int getIdLibro() {
        return idLibro; /* Devolver id del libro */
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo; /* Devolver fecha de préstamo */
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion; /* Devolver fecha de vencimiento */
    }

    public boolean isDevuelto() {
        return devuelto; /* Devolver si ya fue devuelto */
    }

    public String getNombreEstudiante() {
        return nombreEstudiante; /* Devolver nombre del estudiante */
    }

    public String getTituloLibro() {
        return tituloLibro; /* Devolver título del libro */
    }

    /**
     * Metodo set para modificar los datos del préstamo
     */
    public void setId(int id) {
        this.id = id; /* Definir id generado por la base de datos */
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion; /* Definir nueva fecha de vencimiento */
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto; /* Definir si fue devuelto */
    }

    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante; /* Definir nombre para mostrar */
    }

    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro; /* Definir título para mostrar */
    }

    /**
     * Calcula los días de atraso: son los días que pasaron desde el vencimiento
     * si el libro todavía no se devuelve, o 0 si está al día o ya fue devuelto
     */
    public long getDiasAtraso() {
        if (devuelto || fechaDevolucion == null) {
            return 0;
        }

        long dias = ChronoUnit.DAYS.between(fechaDevolucion, LocalDate.now());
        return dias > 0 ? dias : 0;
    }

    /**
     * Devuelve el estado del préstamo como texto, deja constancia del atraso
     */
    public String getEstadoTexto() {
        if (devuelto) {
            return "Devuelto";
        }

        long atraso = getDiasAtraso();
        if (atraso > 0) {
            return "ATRASADO (" + atraso + " días)";
        }

        return "Vigente";
    }
}
