package vista;

import javax.swing.table.DefaultTableModel;

/**
 * DefaultTableModel cuyas celdas no se pueden editar directamente, los datos
 * se modifican solo desde el formulario
 */
public class ModeloTablaSoloLectura extends DefaultTableModel {

    /**
     * Constructor con parametros de la clase ModeloTablaSoloLectura
     */
    public ModeloTablaSoloLectura(String[] columnas) {
        super(columnas, 0); /* Crear la tabla sin filas */
    }

    /**
     * Sobreescritura del metodo isCellEditable para bloquear la edición
     */
    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }
}
