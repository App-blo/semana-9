package vista;

import controlador.ControladorEstudiantes;
import modelo.Estudiante;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana para registrar, editar, eliminar y listar estudiantes. Al registrar
 * un estudiante también se crea su usuario de acceso con la contraseña ingresada
 */
public class VentanaEstudiantes extends JFrame {

    private ControladorEstudiantes controlador;  /* Controlador de estudiantes */
    private List<Estudiante> estudiantes;        /* Estudiantes que se ven en la tabla, en el mismo orden */
    private ModeloTablaSoloLectura modeloTabla;  /* Modelo de datos de la tabla */
    private JTable tabla;

    private JTextField txtNombre;                /* Campo para el nombre */
    private JTextField txtRut;                   /* Campo para el RUT */
    private JTextField txtCurso;                 /* Campo para el curso */
    private JTextField txtCorreo;                /* Campo para el correo */
    private JPasswordField txtContrasena;        /* Campo para la contraseña del usuario */

    /**
     * Constructor con parametros de la clase VentanaEstudiantes
     */
    public VentanaEstudiantes(ControladorEstudiantes controlador) {
        this.controlador = controlador; /* Definir controlador */
        this.estudiantes = new ArrayList<>();

        setTitle("Gestión de Estudiantes");
        setSize(720, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        /* Formulario */
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        txtNombre = new JTextField();
        txtRut = new JTextField();
        txtCurso = new JTextField();
        txtCorreo = new JTextField();
        txtContrasena = new JPasswordField();

        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("RUT (ej. 12345678-9):"));
        panelFormulario.add(txtRut);
        panelFormulario.add(new JLabel("Curso:"));
        panelFormulario.add(txtCurso);
        panelFormulario.add(new JLabel("Correo:"));
        panelFormulario.add(txtCorreo);
        panelFormulario.add(new JLabel("Contraseña (vacía al editar = no cambia):"));
        panelFormulario.add(txtContrasena);

        /* Tabla */
        modeloTabla = new ModeloTablaSoloLectura(new String[] { "ID", "Nombre", "RUT", "Curso", "Correo" });
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                    Estudiante estudiante = estudiantes.get(tabla.getSelectedRow());
                    txtNombre.setText(estudiante.getNombre());
                    txtRut.setText(estudiante.getRut());
                    txtCurso.setText(estudiante.getCurso());
                    txtCorreo.setText(estudiante.getCorreo());
                    txtContrasena.setText("");
                }
            }
        });

        /* Botones */
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrar();
            }
        });

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editar();
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminar();
            }
        });

        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiar();
            }
        });

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        add(panelFormulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        cargarTabla(); /* Mostrar los estudiantes guardados al abrir la ventana */
        setVisible(true);
    }

    /**
     * Vuelve a leer los estudiantes de la base de datos y llena la tabla
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0); /* Limpiar filas antes de recargar */

        try {
            estudiantes = controlador.listarEstudiantes();
        } catch (SQLException e) {
            estudiantes = new ArrayList<>();
            Mensajes.errorSQL(this, "No se pudieron cargar los estudiantes", e);
            return;
        }

        for (Estudiante estudiante : estudiantes) {
            modeloTabla.addRow(new Object[] {
                    estudiante.getId(),
                    estudiante.getNombre(),
                    estudiante.getRut(),
                    estudiante.getCurso(),
                    estudiante.getCorreo()
            });
        }
    }

    /**
     * Valida los datos personales del formulario y arma el estudiante,
     * devuelve null si algún dato no es válido
     */
    private Estudiante leerFormulario() {
        String nombre = txtNombre.getText().trim();
        String rut = txtRut.getText().trim();
        String curso = txtCurso.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (nombre.isEmpty() || rut.isEmpty() || curso.isEmpty() || correo.isEmpty()) {
            Mensajes.advertencia(this, "Debes completar nombre, RUT, curso y correo.");
            return null;
        }

        if (nombre.length() > 100 || correo.length() > 100 || curso.length() > 20) {
            Mensajes.advertencia(this, "Algún campo supera el largo permitido "
                    + "(nombre y correo 100, curso 20 caracteres).");
            return null;
        }

        if (!rut.matches("\\d{7,8}-[0-9kK]")) {
            Mensajes.advertencia(this, "El RUT debe tener el formato 12345678-9, sin puntos y con guion.");
            return null;
        }

        if (!correo.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            Mensajes.advertencia(this, "El correo no tiene un formato válido, por ejemplo nombre@correo.cl.");
            return null;
        }

        return new Estudiante(nombre, rut, curso, correo);
    }

    /**
     * Devuelve el estudiante seleccionado en la tabla, o null si no hay ninguno
     */
    private Estudiante estudianteSeleccionado() {
        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            Mensajes.advertencia(this, "Selecciona un estudiante de la tabla.");
            return null;
        }

        return estudiantes.get(fila);
    }

    /**
     * Guarda un estudiante nuevo junto con su usuario de acceso
     */
    private void registrar() {
        Estudiante estudiante = leerFormulario();
        if (estudiante == null) {
            return;
        }

        String contrasena = new String(txtContrasena.getPassword());

        if (contrasena.length() < 4) {
            Mensajes.advertencia(this, "La contraseña debe tener al menos 4 caracteres.");
            return;
        }

        try {
            controlador.registrarEstudiante(estudiante, contrasena);
            Mensajes.exito(this, "Estudiante registrado correctamente, ya puede ingresar con su correo.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo registrar el estudiante", e);
        }
    }

    /**
     * Actualiza el estudiante seleccionado y su usuario de acceso
     */
    private void editar() {
        Estudiante seleccionado = estudianteSeleccionado();
        if (seleccionado == null) {
            return;
        }

        Estudiante estudiante = leerFormulario();
        if (estudiante == null) {
            return;
        }
        estudiante.setId(seleccionado.getId()); /* Mantener el id del estudiante que se edita */

        /* La contraseña solo se cambia si se escribió una nueva */
        String contrasena = new String(txtContrasena.getPassword());
        String contrasenaNueva = null;

        if (!contrasena.isEmpty()) {
            if (contrasena.length() < 4) {
                Mensajes.advertencia(this, "La contraseña debe tener al menos 4 caracteres.");
                return;
            }
            contrasenaNueva = contrasena;
        }

        try {
            controlador.actualizarEstudiante(estudiante, seleccionado.getRut(), contrasenaNueva);
            Mensajes.exito(this, "Estudiante actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo actualizar el estudiante", e);
        }
    }

    /**
     * Elimina el estudiante seleccionado y su usuario, previa confirmación
     */
    private void eliminar() {
        Estudiante estudiante = estudianteSeleccionado();
        if (estudiante == null) {
            return;
        }

        if (!Mensajes.confirmar(this, "¿Eliminar al estudiante " + estudiante.getNombre() + "?")) {
            return;
        }

        try {
            controlador.eliminarEstudiante(estudiante);
            Mensajes.exito(this, "Estudiante eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo eliminar el estudiante", e);
        }
    }

    /**
     * Deja el formulario vacío y quita la selección de la tabla
     */
    private void limpiar() {
        txtNombre.setText("");
        txtRut.setText("");
        txtCurso.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        tabla.clearSelection();
    }
}
