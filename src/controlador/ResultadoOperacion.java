package controlador;

/**
 * Interfaz que usa la vista para enterarse cuando termina una operación que
 * se ejecutó en un hilo en segundo plano
 */
public interface ResultadoOperacion {

    /**
     * Se llama desde el hilo en segundo plano cuando la operación termina. La
     * vista debe pasar a la interfaz con SwingUtilities.invokeLater
     * @param exito   true si la operación se realizó
     * @param mensaje Texto para mostrarle al usuario
     */
    void alTerminar(boolean exito, String mensaje);
}
