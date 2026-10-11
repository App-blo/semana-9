package modelo;

/**
 * Subclase que representa a un usuario que ingresa al sistema, extiende Persona
 */
public class Usuario extends Persona {

    private String contrasena; /* Contraseña de acceso */
    private Rol rol;           /* Rol del usuario: bibliotecario o estudiante */

    /**
     * Constructor para un usuario nuevo, aún sin id
     */
    public Usuario(String nombre, String rut, String correo, String contrasena, Rol rol) {
        super(0, nombre, rut, correo); /* Llamar al constructor de la superclase */
        this.contrasena = contrasena;  /* Definir contraseña */
        this.rol        = rol;         /* Definir rol */
    }

    /**
     * Constructor con todos los parametros, se usa al leer un usuario desde la base de datos
     */
    public Usuario(int id, String nombre, String rut, String correo, String contrasena, Rol rol) {
        super(id, nombre, rut, correo); /* Llamar al constructor de la superclase */
        this.contrasena = contrasena;   /* Definir contraseña */
        this.rol        = rol;          /* Definir rol */
    }

    /**
     * Metodo get para devolver los datos propios del usuario
     */
    public String getContrasena() {
        return contrasena; /* Devolver contraseña */
    }

    public Rol getRol() {
        return rol; /* Devolver rol */
    }

    /**
     * Metodo set para modificar los datos propios del usuario
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena; /* Definir nueva contraseña */
    }

    public void setRol(Rol rol) {
        this.rol = rol; /* Definir nuevo rol */
    }

    /**
     * Implementación del metodo abstracto getDescripcion de Persona
     */
    @Override
    public String getDescripcion() {
        return getNombre() + " (" + rol.paraBD() + ")";
    }
}
