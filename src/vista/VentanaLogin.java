package vista;

import controlador.ControladorAutenticacion;
import modelo.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Ventana de autenticación, valida el correo y la contraseña contra la base
 * de datos y abre el menú principal con las funciones que corresponden al rol
 */
public class VentanaLogin extends JFrame {

    private ControladorAutenticacion controlador; /* Controlador que valida las credenciales */

    private JTextField txtCorreo;          /* Campo para el correo del usuario */
    private JPasswordField txtContrasena;  /* Campo para la contraseña */

    /**
     * Constructor de la clase VentanaLogin
     */
    public VentanaLogin() {
        controlador = new ControladorAutenticacion(); /* Controlador de autenticación */

        setTitle("Biblioteca Escolar - Ingreso");
        setSize(380, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelFormulario = new JPanel(new GridLayout(2, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));

        txtCorreo = new JTextField();
        txtContrasena = new JPasswordField();

        panelFormulario.add(new JLabel("Correo:"));
        panelFormulario.add(txtCorreo);
        panelFormulario.add(new JLabel("Contraseña:"));
        panelFormulario.add(txtContrasena);

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ingresar();
            }
        });

        /* Con Enter en el campo de contraseña también se ingresa */
        txtContrasena.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ingresar();
            }
        });

        JLabel titulo = new JLabel("Sistema de Gestión de Biblioteca Escolar", SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        add(titulo, BorderLayout.NORTH);
        add(panelFormulario, BorderLayout.CENTER);
        add(btnIngresar, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Valida los campos y consulta el usuario en la base de datos
     */
    private void ingresar() {
        String correo = txtCorreo.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Mensajes.advertencia(this, "Debes ingresar el correo y la contraseña.");
            return;
        }

        try {
            Usuario usuario = controlador.iniciarSesion(correo, contrasena);

            if (usuario == null) {
                Mensajes.advertencia(this, "Correo o contraseña incorrectos.");
                txtContrasena.setText("");
                return;
            }

            new VentanaPrincipal(usuario); /* Abrir el menú con las funciones del rol */
            dispose();

        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo validar el ingreso", e);
        }
    }
}
