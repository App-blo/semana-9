package modelo;

/**
 * Subclase que representa a un estudiante de la biblioteca, extiende Persona
 */
public class Estudiante extends Persona {

    private String curso; /* Curso al que pertenece el estudiante */

    /**
     * Constructor para un estudiante nuevo, aún sin id
     */
    public Estudiante(String nombre, String rut, String curso, String correo) {
        super(0, nombre, rut, correo); /* Llamar al constructor de la superclase */
        this.curso = curso;            /* Definir curso */
    }

    /**
     * Constructor con todos los parametros, se usa al leer un estudiante desde la base de datos
     */
    public Estudiante(int id, String nombre, String rut, String curso, String correo) {
        super(id, nombre, rut, correo); /* Llamar al constructor de la superclase */
        this.curso = curso;             /* Definir curso */
    }

    /**
     * Metodo get para devolver el curso
     */
    public String getCurso() {
        return curso; /* Devolver curso */
    }

    /**
     * Metodo set para modificar el curso
     */
    public void setCurso(String curso) {
        this.curso = curso; /* Definir nuevo curso */
    }

    /**
     * Implementación del metodo abstracto getDescripcion de Persona
     */
    @Override
    public String getDescripcion() {
        return getNombre() + ", " + curso;
    }

    /**
     * Sobreescritura del metodo To String, es el texto que se ve en los JComboBox
     */
    @Override
    public String toString() {
        return getId() + " - " + getNombre() + " (" + curso + ")";
    }
}
