package vista;

import controlador.ControladorLibros;
import modelo.Categoria;
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
 * Ventana para registrar, editar, eliminar y listar categorías de libros
 */
public class VentanaCategorias extends JFrame {

    private ControladorLibros controlador;      /* Controlador compartido con la ventana de libros */
    private List<Categoria> categorias;         /* Categorías que se ven en la tabla, en el mismo orden */
    private ModeloTablaSoloLectura modeloTabla; /* Modelo de datos de la tabla */
    private JTable tabla;
    private JTextField txtNombre;               /* Campo para el nombre de la categoría */

    /**
     * Constructor con parametros de la clase VentanaCategorias
     */
    public VentanaCategorias(ControladorLibros controlador) {
        this.controlador = controlador; /* Definir controlador compartido */
        this.categorias = new ArrayList<>();

        setTitle("Gestión de Categorías");
        setSize(480, 360);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        /* Formulario */
        JPanel panelFormulario = new JPanel(new GridLayout(1, 2, 8, 8));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        txtNombre = new JTextField();
        panelFormulario.add(new JLabel("Nombre de la categoría:"));
        panelFormulario.add(txtNombre);

        /* Tabla */
        modeloTabla = new ModeloTablaSoloLectura(new String[] { "ID", "Nombre" });
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                    txtNombre.setText(categorias.get(tabla.getSelectedRow()).getNombre());
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

        cargarTabla(); /* Mostrar las categorías guardadas al abrir la ventana */
        setVisible(true);
    }

    /**
     * Vuelve a leer las categorías de la base de datos y llena la tabla
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0); /* Limpiar filas antes de recargar */

        try {
            categorias = controlador.listarCategorias();
        } catch (SQLException e) {
            categorias = new ArrayList<>();
            Mensajes.errorSQL(this, "No se pudieron cargar las categorías", e);
            return;
        }

        for (Categoria categoria : categorias) {
            modeloTabla.addRow(new Object[] { categoria.getId(), categoria.getNombre() });
        }
    }

    /**
     * Valida el nombre ingresado, devuelve null si no es válido
     */
    private String leerNombre() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            Mensajes.advertencia(this, "Debes ingresar el nombre de la categoría.");
            return null;
        }

        if (nombre.length() > 100) {
            Mensajes.advertencia(this, "El nombre no puede superar los 100 caracteres.");
            return null;
        }

        return nombre;
    }

    /**
     * Devuelve la categoría seleccionada en la tabla, o null si no hay ninguna
     */
    private Categoria categoriaSeleccionada() {
        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            Mensajes.advertencia(this, "Selecciona una categoría de la tabla.");
            return null;
        }

        return categorias.get(fila);
    }

    /**
     * Guarda una categoría nueva en la base de datos
     */
    private void registrar() {
        String nombre = leerNombre();
        if (nombre == null) {
            return;
        }

        try {
            controlador.registrarCategoria(new Categoria(nombre));
            Mensajes.exito(this, "Categoría registrada correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo registrar la categoría", e);
        }
    }

    /**
     * Actualiza el nombre de la categoría seleccionada
     */
    private void editar() {
        Categoria categoria = categoriaSeleccionada();
        if (categoria == null) {
            return;
        }

        String nombre = leerNombre();
        if (nombre == null) {
            return;
        }

        categoria.setNombre(nombre);

        try {
            controlador.actualizarCategoria(categoria);
            Mensajes.exito(this, "Categoría actualizada correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo actualizar la categoría", e);
        }
    }

    /**
     * Elimina la categoría seleccionada, previa confirmación
     */
    private void eliminar() {
        Categoria categoria = categoriaSeleccionada();
        if (categoria == null) {
            return;
        }

        if (!Mensajes.confirmar(this, "¿Eliminar la categoría " + categoria + "?")) {
            return;
        }

        try {
            controlador.eliminarCategoria(categoria.getId());
            Mensajes.exito(this, "Categoría eliminada correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo eliminar la categoría", e);
        }
    }

    /**
     * Deja el formulario vacío y quita la selección de la tabla
     */
    private void limpiar() {
        txtNombre.setText("");
        tabla.clearSelection();
    }
}
