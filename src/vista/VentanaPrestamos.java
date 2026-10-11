package vista;

import controlador.ControladorEstudiantes;
import controlador.ControladorLibros;
import controlador.ControladorPrestamos;
import controlador.ResultadoOperacion;
import modelo.Estudiante;
import modelo.Libro;
import modelo.Prestamo;
import modelo.Rol;
import modelo.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana de préstamos y devoluciones. El préstamo y la devolución se ejecutan
 * en un hilo en segundo plano para no congelar la interfaz. Un estudiante solo
 * ve y gestiona sus propios préstamos
 */
public class VentanaPrestamos extends JFrame {

    private ControladorPrestamos controladorPrestamos; /* Controlador de préstamos, con el hilo y la sincronización */
    private ControladorLibros controladorLibros;       /* Para cargar los libros disponibles */
    private ControladorEstudiantes controladorEstudiantes; /* Para cargar los estudiantes */
    private Usuario usuario;                           /* Usuario que inició sesión */
    private Estudiante estudianteSesion;               /* Ficha del estudiante, solo si el usuario es estudiante */

    private List<Prestamo> prestamos;                  /* Préstamos que se ven en la tabla, en el mismo orden */
    private ModeloTablaSoloLectura modeloTabla;        /* Modelo de datos de la tabla */
    private JTable tabla;

    private JComboBox<Estudiante> comboEstudiante;     /* Selector del estudiante */
    private JComboBox<Libro> comboLibro;               /* Selector del libro disponible */
    private JCheckBox chkSoloPendientes;               /* Filtro para ver solo préstamos sin devolver */
    private JButton btnPrestar;                        /* Botón para registrar el préstamo */
    private JButton btnDevolver;                       /* Botón para registrar la devolución */
    private JLabel lblEstado;                          /* Mensaje de la operación en curso */

    /**
     * Constructor con parametros de la clase VentanaPrestamos
     */
    public VentanaPrestamos(ControladorPrestamos controladorPrestamos, ControladorLibros controladorLibros,
                            ControladorEstudiantes controladorEstudiantes, Usuario usuario) {
        this.controladorPrestamos = controladorPrestamos;
        this.controladorLibros = controladorLibros;
        this.controladorEstudiantes = controladorEstudiantes;
        this.usuario = usuario;
        this.prestamos = new ArrayList<>();

        /* Un estudiante necesita tener su ficha para poder pedir libros */
        if (usuario.getRol() == Rol.ESTUDIANTE) {
            try {
                estudianteSesion = controladorEstudiantes.buscarPorRut(usuario.getRut());
            } catch (SQLException e) {
                Mensajes.errorSQL(null, "No se pudo cargar tu ficha de estudiante", e);
                return;
            }

            if (estudianteSesion == null) {
                Mensajes.advertencia(null, "Tu usuario no tiene una ficha de estudiante registrada.");
                return;
            }
        }

        setTitle("Préstamos y Devoluciones");
        setSize(860, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        /* Formulario */
        JPanel panelFormulario = new JPanel(new GridLayout(2, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        comboEstudiante = new JComboBox<>();
        comboLibro = new JComboBox<>();

        panelFormulario.add(new JLabel("Estudiante:"));
        panelFormulario.add(comboEstudiante);
        panelFormulario.add(new JLabel("Libro disponible:"));
        panelFormulario.add(comboLibro);

        chkSoloPendientes = new JCheckBox("Mostrar solo préstamos sin devolver");
        chkSoloPendientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarTabla();
            }
        });

        lblEstado = new JLabel(" ");

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.add(chkSoloPendientes);
        panelFiltros.add(lblEstado);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelFormulario, BorderLayout.NORTH);
        panelNorte.add(panelFiltros, BorderLayout.SOUTH);

        /* Tabla */
        modeloTabla = new ModeloTablaSoloLectura(
                new String[] { "ID", "Estudiante", "Libro", "Fecha préstamo", "Vence", "Estado" });
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        /* Botones */
        btnPrestar = new JButton("Registrar préstamo");
        btnDevolver = new JButton("Registrar devolución");
        JButton btnActualizar = new JButton("Actualizar");

        btnPrestar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarPrestamo();
            }
        });

        btnDevolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarDevolucion();
            }
        });

        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refrescar();
            }
        });

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnPrestar);
        panelBotones.add(btnDevolver);
        panelBotones.add(btnActualizar);

        add(panelNorte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        refrescar(); /* Mostrar los datos guardados al abrir la ventana */
        setVisible(true);
    }

    /**
     * Recarga los combos y la tabla desde la base de datos
     */
    private void refrescar() {
        cargarCombos();
        cargarTabla();
    }

    /**
     * Llena los combos de estudiante y libro, conservando la selección actual.
     * Un estudiante solo ve su propia ficha en el combo
     */
    private void cargarCombos() {
        Estudiante estudianteElegido = (Estudiante) comboEstudiante.getSelectedItem();
        Libro libroElegido = (Libro) comboLibro.getSelectedItem();

        try {
            comboEstudiante.removeAllItems();
            if (estudianteSesion != null) {
                comboEstudiante.addItem(estudianteSesion);
            } else {
                for (Estudiante estudiante : controladorEstudiantes.listarEstudiantes()) {
                    comboEstudiante.addItem(estudiante);
                }
            }

            comboLibro.removeAllItems();
            for (Libro libro : controladorLibros.listarLibrosDisponibles()) {
                comboLibro.addItem(libro);
            }
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudieron cargar los datos", e);
            return;
        }

        /* Volver a seleccionar lo que estaba elegido, si todavía existe */
        for (int i = 0; estudianteElegido != null && i < comboEstudiante.getItemCount(); i++) {
            if (comboEstudiante.getItemAt(i).getId() == estudianteElegido.getId()) {
                comboEstudiante.setSelectedIndex(i);
            }
        }
        for (int i = 0; libroElegido != null && i < comboLibro.getItemCount(); i++) {
            if (comboLibro.getItemAt(i).getId() == libroElegido.getId()) {
                comboLibro.setSelectedIndex(i);
            }
        }
    }

    /**
     * Vuelve a leer los préstamos de la base de datos y llena la tabla. Un
     * estudiante solo ve su propio historial
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0); /* Limpiar filas antes de recargar */

        try {
            List<Prestamo> todos;
            if (estudianteSesion != null) {
                todos = controladorPrestamos.listarHistorialEstudiante(estudianteSesion.getId());
            } else {
                todos = controladorPrestamos.listarPrestamos();
            }

            /* Aplicar el filtro de préstamos sin devolver */
            prestamos = new ArrayList<>();
            for (Prestamo prestamo : todos) {
                if (!chkSoloPendientes.isSelected() || !prestamo.isDevuelto()) {
                    prestamos.add(prestamo);
                }
            }
        } catch (SQLException e) {
            prestamos = new ArrayList<>();
            Mensajes.errorSQL(this, "No se pudieron cargar los préstamos", e);
            return;
        }

        for (Prestamo prestamo : prestamos) {
            modeloTabla.addRow(new Object[] {
                    prestamo.getId(),
                    prestamo.getNombreEstudiante(),
                    prestamo.getTituloLibro(),
                    prestamo.getFechaPrestamo(),
                    prestamo.getFechaDevolucion(),
                    prestamo.getEstadoTexto()
            });
        }
    }

    /**
     * Habilita o bloquea los botones mientras corre una operación en segundo plano
     */
    private void marcarOcupado(boolean ocupado, String texto) {
        btnPrestar.setEnabled(!ocupado);
        btnDevolver.setEnabled(!ocupado);
        lblEstado.setText(texto);
    }

    /**
     * Registra el préstamo en un hilo en segundo plano. Cuando termina, el
     * resultado se muestra en el hilo de la interfaz con invokeLater
     */
    private void registrarPrestamo() {
        Estudiante estudiante = (Estudiante) comboEstudiante.getSelectedItem();
        Libro libro = (Libro) comboLibro.getSelectedItem();

        if (estudiante == null || libro == null) {
            Mensajes.advertencia(this, "Debes elegir un estudiante y un libro con stock disponible.");
            return;
        }

        marcarOcupado(true, "Procesando préstamo...");

        controladorPrestamos.registrarPrestamoEnSegundoPlano(estudiante.getId(), libro.getId(),
                new ResultadoOperacion() {
                    @Override
                    public void alTerminar(final boolean exito, final String mensaje) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                mostrarResultado(exito, mensaje);
                            }
                        });
                    }
                });
    }

    /**
     * Registra la devolución del préstamo seleccionado en un hilo en segundo plano
     */
    private void registrarDevolucion() {
        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            Mensajes.advertencia(this, "Selecciona un préstamo de la tabla.");
            return;
        }

        Prestamo prestamo = prestamos.get(fila);

        if (prestamo.isDevuelto()) {
            Mensajes.advertencia(this, "Ese préstamo ya fue devuelto.");
            return;
        }

        marcarOcupado(true, "Procesando devolución...");

        controladorPrestamos.registrarDevolucionEnSegundoPlano(prestamo.getId(), new ResultadoOperacion() {
            @Override
            public void alTerminar(final boolean exito, final String mensaje) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        mostrarResultado(exito, mensaje);
                    }
                });
            }
        });
    }

    /**
     * Muestra el resultado de la operación y vuelve a cargar los datos. Se
     * ejecuta siempre en el hilo de la interfaz
     */
    private void mostrarResultado(boolean exito, String mensaje) {
        marcarOcupado(false, " ");

        if (exito) {
            Mensajes.exito(this, mensaje);
        } else {
            Mensajes.advertencia(this, mensaje);
        }

        refrescar();
    }
}
