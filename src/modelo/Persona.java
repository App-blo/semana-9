package modelo;

/**
 * Clase abstracta que representa a una persona vinculada a la biblioteca,
 * con los datos comunes de usuarios y estudiantes
 */
public abstract class Persona {

    private int id;        /* Identificador, lo genera la base de datos */
    private String nombre; /* Nombre completo */
    private String rut;    /* RUT de la persona */
    private String correo; /* Correo electrónico */

    /**
     * Constructor con parametros de la clase Persona
     */
    public Persona(int id, String nombre, String rut, String correo) {
        this.id     = id;     /* Definir identificador */
        this.nombre = nombre; /* Definir nombre */
        this.rut    = rut;    /* Definir RUT */
        this.correo = correo; /* Definir correo */
    }

    /**
     * Metodo get para devolver los datos de la persona
     */
    public int getId() {
        return id; /* Devolver identificador */
    }

    public String getNombre() {
        return nombre; /* Devolver nombre */
    }

    public String getRut() {
        return rut; /* Devolver RUT */
    }

    public String getCorreo() {
        return correo; /* Devolver correo */
    }

    /**
     * Metodo set para modificar los datos de la persona
     */
    public void setId(int id) {
        this.id = id; /* Definir id generado por la base de datos */
    }

    public void setNombre(String nombre) {
        this.nombre = nombre; /* Definir nuevo nombre */
    }

    public void setRut(String rut) {
        this.rut = rut; /* Definir nuevo RUT */
    }

    public void setCorreo(String correo) {
        this.correo = correo; /* Definir nuevo correo */
    }

    /**
     * Metodo abstracto que cada subclase implementa para describirse
     */
    public abstract String getDescripcion();
}
