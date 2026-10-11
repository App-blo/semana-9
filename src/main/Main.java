package main;

import vista.VentanaLogin;
import javax.swing.SwingUtilities;

/**
 * Clase principal que inicia el sistema de la Biblioteca Escolar
 */
public class Main {

    public static void main(String[] args) {
        /* Iniciar la interfaz en el Event Dispatch Thread de Swing */
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VentanaLogin();
            }
        });
    }
}
