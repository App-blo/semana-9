package vista;

import controlador.ControladorEstudiantes;
import controlador.ControladorLibros;
import controlador.ControladorPrestamos;
import modelo.Estudiante;
import modelo.LibroMasPrestado;
import modelo.Prestamo;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * Ventana de reportes: libros más prestados, historial por estudiante y
 * libros actualmente en préstamo
 */
public class VentanaReportes extends JFrame {

    private static final String REPORTE_MAS_PRESTADOS = "Libros más prestados";
    private static final String REPORTE_HISTORIAL     = "Historial por estudiante";
    private static final String REPORTE_EN_PRESTAMO   = "Libros actualmente en préstamo";

    private ControladorPrestamos controladorPrestamos;
    private ControladorLibros controladorLibros;
    private ControladorEstudiantes controladorEstudiantes;

    private ModeloTablaSoloLectura modeloTabla;   /* Modelo de datos de la tabla */
    private JComboBox<String> comboReporte;       /* Selector del tipo de reporte */
    private JComboBox<Estudiante> comboEstudiante; /* Estudiante, solo para el historial */
    private JLabel lblResumen;                    /* Cantidad de registros del reporte */

    /**
     * Constructor con parametros de la clase VentanaReportes
     */
    public VentanaReportes(ControladorPrestamos controladorPrestamos, ControladorLibros controladorLibros,
                           ControladorEstudiantes controladorEstudiantes) {
        this.controladorPrestamos = controladorPrestamos;
        this.controladorLibros = controladorLibros;
        this.controladorEstudiantes = controladorEstudiantes;

        setTitle("Reportes");
        setSize(820, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        /* Selección del reporte */
        comboReporte = new JComboBox<>(new String[] { REPORTE_MAS_PRESTADOS, REPORTE_HISTORIAL, REPORTE_EN_PRESTAMO });
        comboEstudiante = new JComboBox<>();
        comboEstudiante.setEnabled(false);

        comboReporte.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* El combo de estudiante solo sirve para el historial */
                comboEstudiante.setEnabled(REPORTE_HISTORIAL.equals(comboReporte.getSelectedItem()));
            }
        });

        JButton btnGenerar = new JButton("Generar reporte");
        btnGenerar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarReporte();
            }
        });

        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNorte.add(new JLabel("Reporte:"));
        panelNorte.add(comboReporte);
        panelNorte.add(new JLabel("Estudiante:"));
        panelNorte.add(comboEstudiante);
        panelNorte.add(btnGenerar);

        /* Tabla */
        modeloTabla = new ModeloTablaSoloLectura(new String[] { "Reporte" });
        JTable tabla = new JTable(modeloTabla);

        lblResumen = new JLabel("Elige un reporte y pulsa \"Generar reporte\".");
        lblResumen.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        add(panelNorte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(lblResumen, BorderLayout.SOUTH);

        cargarEstudiantes();
        setVisible(true);
    }

    /**
     * Llena el combo con los estudiantes de la base de datos
     */
    private void cargarEstudiantes() {
        try {
            comboEstudiante.removeAllItems();
            for (Estudiante estudiante : controladorEstudiantes.listarEstudiantes()) {
                comboEstudiante.addItem(estudiante);
            }
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudieron cargar los estudiantes", e);
        }
    }

    /**
     * Genera el reporte elegido y lo muestra en la tabla
     */
    private void generarReporte() {
        String reporte = (String) comboReporte.getSelectedItem();

        try {
            if (REPORTE_MAS_PRESTADOS.equals(reporte)) {
                mostrarLibrosMasPrestados(controladorLibros.listarLibrosMasPrestados());

            } else if (REPORTE_HISTORIAL.equals(reporte)) {
                Estudiante estudiante = (Estudiante) comboEstudiante.getSelectedItem();

                if (estudiante == null) {
                    Mensajes.advertencia(this, "Debes elegir un estudiante.");
                    return;
                }

                mostrarPrestamos(controladorPrestamos.listarHistorialEstudiante(estudiante.getId()));

            } else {
                mostrarPrestamos(controladorPrestamos.listarPrestamosActivos());
            }
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo generar el reporte", e);
        }
    }

    /**
     * Muestra el reporte de libros más prestados
     */
    private void mostrarLibrosMasPrestados(List<LibroMasPrestado> libros) {
        modeloTabla.setColumnIdentifiers(new String[] { "Título", "Autor", "Veces prestado" });
        modeloTabla.setRowCount(0);

        for (LibroMasPrestado libro : libros) {
            modeloTabla.addRow(new Object[] { libro.getTitulo(), libro.getAutor(), libro.getTotalPrestamos() });
        }

        lblResumen.setText(libros.size() + " libros en el reporte.");
    }

    /**
     * Muestra un reporte de préstamos (historial o libros en préstamo)
     */
    private void mostrarPrestamos(List<Prestamo> lista) {
        modeloTabla.setColumnIdentifiers(
                new String[] { "ID", "Estudiante", "Libro", "Fecha préstamo", "Vence", "Estado" });
        modeloTabla.setRowCount(0);

        for (Prestamo prestamo : lista) {
            modeloTabla.addRow(new Object[] {
                    prestamo.getId(),
                    prestamo.getNombreEstudiante(),
                    prestamo.getTituloLibro(),
                    prestamo.getFechaPrestamo(),
                    prestamo.getFechaDevolucion(),
                    prestamo.getEstadoTexto()
            });
        }

        lblResumen.setText(lista.size() + " préstamos en el reporte.");
    }
}
