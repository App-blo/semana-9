package vista;

import controlador.ControladorLibros;
import modelo.Categoria;
import modelo.Libro;
import modelo.Rol;
import modelo.Usuario;
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
 * Ventana de libros. El bibliotecario puede registrar, modificar, eliminar y
 * visualizar libros; el estudiante solo los puede consultar
 */
public class VentanaLibros extends JFrame {

    private ControladorLibros controlador;      /* Controlador compartido con las demás ventanas */
    private boolean soloConsulta;               /* true cuando el usuario es estudiante */
    private List<Libro> libros;                 /* Libros que se ven en la tabla, en el mismo orden */
    private ModeloTablaSoloLectura modeloTabla; /* Modelo de datos de la tabla */
    private JTable tabla;

    private JTextField txtTitulo;               /* Campo para el título */
    private JTextField txtAutor;                /* Campo para el autor */
    private JTextField txtIsbn;                 /* Campo para el ISBN */
    private JTextField txtEditorial;            /* Campo para la editorial */
    private JTextField txtStock;                /* Campo para el stock */
    private JComboBox<Categoria> comboCategoria; /* Selector de la categoría */

    /**
     * Constructor con parametros de la clase VentanaLibros
     */
    public VentanaLibros(ControladorLibros controlador, Usuario usuario) {
        this.controlador = controlador; /* Definir controlador compartido */
        this.soloConsulta = (usuario.getRol() == Rol.ESTUDIANTE);
        this.libros = new ArrayList<>();

        setTitle(soloConsulta ? "Consulta de Libros" : "Gestión de Libros");
        setSize(820, soloConsulta ? 400 : 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        /* Tabla */
        modeloTabla = new ModeloTablaSoloLectura(
                new String[] { "ID", "Título", "Autor", "ISBN", "Editorial", "Stock", "Categoría" });
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());

        if (soloConsulta) {
            /* El estudiante solo puede ver el listado */
            JButton btnActualizar = new JButton("Actualizar");
            btnActualizar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cargarTabla();
                }
            });
            panelBotones.add(btnActualizar);
        } else {
            /* Formulario del bibliotecario */
            JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 8, 8));
            panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

            txtTitulo = new JTextField();
            txtAutor = new JTextField();
            txtIsbn = new JTextField();
            txtEditorial = new JTextField();
            txtStock = new JTextField();
            comboCategoria = new JComboBox<>();

            panelFormulario.add(new JLabel("Título:"));
            panelFormulario.add(txtTitulo);
            panelFormulario.add(new JLabel("Autor:"));
            panelFormulario.add(txtAutor);
            panelFormulario.add(new JLabel("ISBN:"));
            panelFormulario.add(txtIsbn);
            panelFormulario.add(new JLabel("Editorial:"));
            panelFormulario.add(txtEditorial);
            panelFormulario.add(new JLabel("Stock:"));
            panelFormulario.add(txtStock);
            panelFormulario.add(new JLabel("Categoría:"));
            panelFormulario.add(comboCategoria);
            add(panelFormulario, BorderLayout.NORTH);

            tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                        cargarFormulario(libros.get(tabla.getSelectedRow()));
                    }
                }
            });

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

            panelBotones.add(btnRegistrar);
            panelBotones.add(btnEditar);
            panelBotones.add(btnEliminar);
            panelBotones.add(btnLimpiar);

            cargarCategorias();
        }

        add(panelBotones, BorderLayout.SOUTH);

        cargarTabla(); /* Mostrar los libros guardados al abrir la ventana */
        setVisible(true);
    }

    /**
     * Llena el combo de categorías con las de la base de datos
     */
    private void cargarCategorias() {
        try {
            comboCategoria.removeAllItems();
            for (Categoria categoria : controlador.listarCategorias()) {
                comboCategoria.addItem(categoria);
            }
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudieron cargar las categorías", e);
        }
    }

    /**
     * Vuelve a leer los libros de la base de datos y llena la tabla
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0); /* Limpiar filas antes de recargar */

        try {
            libros = controlador.listarLibros();
        } catch (SQLException e) {
            libros = new ArrayList<>();
            Mensajes.errorSQL(this, "No se pudieron cargar los libros", e);
            return;
        }

        for (Libro libro : libros) {
            modeloTabla.addRow(new Object[] {
                    libro.getId(),
                    libro.getTitulo(),
                    libro.getAutor(),
                    libro.getIsbn(),
                    libro.getEditorial(),
                    libro.getStock(),
                    libro.getNombreCategoria()
            });
        }
    }

    /**
     * Muestra los datos de un libro en el formulario
     */
    private void cargarFormulario(Libro libro) {
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtIsbn.setText(libro.getIsbn());
        txtEditorial.setText(libro.getEditorial());
        txtStock.setText(String.valueOf(libro.getStock()));

        for (int i = 0; i < comboCategoria.getItemCount(); i++) {
            if (comboCategoria.getItemAt(i).getId() == libro.getIdCategoria()) {
                comboCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * Valida el formulario y arma el libro, devuelve null si algún dato no es válido
     */
    private Libro leerFormulario() {
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        String isbn = txtIsbn.getText().trim();
        String editorial = txtEditorial.getText().trim();
        String textoStock = txtStock.getText().trim();

        if (titulo.isEmpty() || autor.isEmpty() || isbn.isEmpty() || editorial.isEmpty() || textoStock.isEmpty()) {
            Mensajes.advertencia(this, "Debes completar título, autor, ISBN, editorial y stock.");
            return null;
        }

        if (titulo.length() > 200 || autor.length() > 100 || isbn.length() > 20 || editorial.length() > 100) {
            Mensajes.advertencia(this, "Algún campo supera el largo permitido "
                    + "(título 200, autor 100, ISBN 20 y editorial 100 caracteres).");
            return null;
        }

        int stock;

        try {
            stock = Integer.parseInt(textoStock);
        } catch (NumberFormatException e) {
            Mensajes.advertencia(this, "El stock debe ser un número entero.");
            return null;
        }

        if (stock < 0) {
            Mensajes.advertencia(this, "El stock no puede ser negativo.");
            return null;
        }

        Categoria categoria = (Categoria) comboCategoria.getSelectedItem();

        if (categoria == null) {
            Mensajes.advertencia(this, "Debes registrar al menos una categoría antes de agregar libros.");
            return null;
        }

        return new Libro(titulo, autor, isbn, editorial, stock, categoria.getId());
    }

    /**
     * Devuelve el libro seleccionado en la tabla, o null si no hay ninguno
     */
    private Libro libroSeleccionado() {
        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            Mensajes.advertencia(this, "Selecciona un libro de la tabla.");
            return null;
        }

        return libros.get(fila);
    }

    /**
     * Guarda un libro nuevo en la base de datos
     */
    private void registrar() {
        Libro libro = leerFormulario();
        if (libro == null) {
            return;
        }

        try {
            controlador.registrarLibro(libro);
            Mensajes.exito(this, "Libro registrado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo registrar el libro", e);
        }
    }

    /**
     * Actualiza el libro seleccionado con lo que hay en el formulario
     */
    private void editar() {
        Libro seleccionado = libroSeleccionado();
        if (seleccionado == null) {
            return;
        }

        Libro libro = leerFormulario();
        if (libro == null) {
            return;
        }
        libro.setId(seleccionado.getId()); /* Mantener el id del libro que se edita */

        try {
            controlador.actualizarLibro(libro);
            Mensajes.exito(this, "Libro actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo actualizar el libro", e);
        }
    }

    /**
     * Elimina el libro seleccionado, previa confirmación
     */
    private void eliminar() {
        Libro libro = libroSeleccionado();
        if (libro == null) {
            return;
        }

        if (!Mensajes.confirmar(this, "¿Eliminar el libro \"" + libro.getTitulo() + "\"?")) {
            return;
        }

        try {
            controlador.eliminarLibro(libro.getId());
            Mensajes.exito(this, "Libro eliminado correctamente.");
            limpiar();
            cargarTabla();
        } catch (SQLException e) {
            Mensajes.errorSQL(this, "No se pudo eliminar el libro", e);
        }
    }

    /**
     * Deja el formulario vacío y quita la selección de la tabla
     */
    private void limpiar() {
        txtTitulo.setText("");
        txtAutor.setText("");
        txtIsbn.setText("");
        txtEditorial.setText("");
        txtStock.setText("");
        tabla.clearSelection();
    }
}
