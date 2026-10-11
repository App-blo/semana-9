package vista;

import controlador.ControladorEstudiantes;
import controlador.ControladorLibros;
import controlador.ControladorPrestamos;
import modelo.Rol;
import modelo.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ventana principal con el panel de navegación. Los botones que se muestran
 * dependen del rol: el bibliotecario tiene gestión total y el estudiante solo
 * puede consultar libros y registrar préstamos y devoluciones
 */
public class VentanaPrincipal extends JFrame {

    private Usuario usuario;                         /* Usuario que inició sesión */
    private ControladorLibros controladorLibros;     /* Compartido por las ventanas de libros y reportes */
    private ControladorEstudiantes controladorEstudiantes;
    private ControladorPrestamos controladorPrestamos; /* Único, para que la sincronización sea compartida */

    /**
     * Constructor con parametros de la clase VentanaPrincipal
     */
    public VentanaPrincipal(Usuario usuario) {
        this.usuario = usuario; /* Definir usuario de la sesión */
        this.controladorLibros = new ControladorLibros();
        this.controladorEstudiantes = new ControladorEstudiantes();
        this.controladorPrestamos = new ControladorPrestamos();

        boolean esBibliotecario = (usuario.getRol() == Rol.BIBLIOTECARIO);

        setTitle("Biblioteca Escolar - Menú principal");
        setSize(420, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel lblUsuario = new JLabel("Sesión: " + usuario.getDescripcion(), SwingConstants.CENTER);
        lblUsuario.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JPanel panelBotones = new JPanel(new GridLayout(0, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(15, 40, 20, 40));

        /* Todos los roles pueden consultar libros y registrar préstamos y devoluciones */
        panelBotones.add(crearBoton("Consultar / Gestionar Libros", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new VentanaLibros(controladorLibros, VentanaPrincipal.this.usuario);
            }
        }));

        panelBotones.add(crearBoton("Préstamos y Devoluciones", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new VentanaPrestamos(controladorPrestamos, controladorLibros, controladorEstudiantes,
                        VentanaPrincipal.this.usuario);
            }
        }));

        /* El resto de las opciones son solo para el bibliotecario */
        if (esBibliotecario) {
            panelBotones.add(crearBoton("Gestión de Categorías", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new VentanaCategorias(controladorLibros);
                }
            }));

            panelBotones.add(crearBoton("Gestión de Estudiantes", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new VentanaEstudiantes(controladorEstudiantes);
                }
            }));

            panelBotones.add(crearBoton("Reportes", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new VentanaReportes(controladorPrestamos, controladorLibros, controladorEstudiantes);
                }
            }));
        }

        panelBotones.add(crearBoton("Cerrar sesión", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new VentanaLogin();
                dispose();
            }
        }));

        add(lblUsuario, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);

        setVisible(true);
    }

    /**
     * Crea un botón del menú con su acción
     */
    private JButton crearBoton(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(accion);
        return boton;
    }
}
