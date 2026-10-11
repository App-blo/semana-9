package modelo;

/**
 * Clase que representa una categoría de libros
 */
public class Categoria {

    private int id;        /* Identificador, lo genera la base de datos */
    private String nombre; /* Nombre de la categoría */

    /**
     * Constructor para una categoría nueva, aún sin id
     */
    public Categoria(String nombre) {
        this.id     = 0;      /* Aún no se guarda, la BD asigna el id */
        this.nombre = nombre; /* Definir nombre */
    }

    /**
     * Constructor con todos los parametros, se usa al leer una categoría desde la base de datos
     */
    public Categoria(int id, String nombre) {
        this.id     = id;     /* Definir identificador */
        this.nombre = nombre; /* Definir nombre */
    }

    /**
     * Metodo get para devolver los datos de la categoría
     */
    public int getId() {
        return id; /* Devolver identificador */
    }

    public String getNombre() {
        return nombre; /* Devolver nombre */
    }

    /**
     * Metodo set para modificar los datos de la categoría
     */
    public void setId(int id) {
        this.id = id; /* Definir id generado por la base de datos */
    }

    public void setNombre(String nombre) {
        this.nombre = nombre; /* Definir nuevo nombre */
    }

    /**
     * Sobreescritura del metodo To String, es el texto que se ve en los JComboBox
     */
    @Override
    public String toString() {
        return nombre;
    }
}
