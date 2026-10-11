package modelo;

/**
 * Enum con los roles que puede tener un usuario del sistema
 */
public enum Rol {
    BIBLIOTECARIO,
    ESTUDIANTE;

    /**
     * Convierte el texto guardado en la base de datos ('bibliotecario' o 'estudiante') al enum
     */
    public static Rol desdeBD(String texto) {
        return Rol.valueOf(texto.toUpperCase());
    }

    /**
     * Devuelve el texto que se guarda en la base de datos
     */
    public String paraBD() {
        return name().toLowerCase();
    }
}
