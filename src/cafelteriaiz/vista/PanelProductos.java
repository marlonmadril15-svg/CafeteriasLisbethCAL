package cafelteriaiz.vista;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import cafelteriaiz.control.ControladorCafeteria;
import cafelteriaiz.modelo.Producto;

public class PanelProductos extends JPanel {
    private final ControladorCafeteria controlador;
    private final JTextField codigo = new JTextField();
    private final JTextField nombre = new JTextField();
    private final JTextField precio = new JTextField();
    private final JTextField stock = new JTextField();
    private final JTextField buscar = new JTextField();
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[] {"Código", "Nombre", "Precio", "Stock"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);

    public PanelProductos(ControladorCafeteria controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));
        add(crearFormulario(), BorderLayout.NORTH);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> llenarFormulario());
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(crearBusqueda(), BorderLayout.SOUTH);
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel campos = new JPanel(new GridLayout(2, 4, 6, 4));
        campos.add(new JLabel("Código"));
        campos.add(new JLabel("Nombre"));
        campos.add(new JLabel("Precio"));
        campos.add(new JLabel("Stock"));
        campos.add(codigo);
        campos.add(nombre);
        campos.add(precio);
        campos.add(stock);
        panel.add(new JLabel("Datos del producto"), BorderLayout.NORTH);
        panel.add(campos, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        JButton registrar = new JButton("Registrar");
        JButton editar = new JButton("Guardar cambios");
        JButton reponer = new JButton("Reponer stock");
        JButton limpiar = new JButton("Limpiar");
        registrar.addActionListener(e -> controlador.registrarProducto(
                codigo.getText(), nombre.getText(), precio.getText(), stock.getText()));
        editar.addActionListener(e -> controlador.editarProducto(
                codigo.getText(), nombre.getText(), precio.getText(), stock.getText()));
        reponer.addActionListener(e -> controlador.reponerStock(codigo.getText(), stock.getText()));
        limpiar.addActionListener(e -> limpiarFormulario());
        botones.add(registrar);
        botones.add(editar);
        botones.add(reponer);
        botones.add(limpiar);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearBusqueda() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Buscar código o nombre:"));
        buscar.setColumns(18);
        panel.add(buscar);
        JButton botonBuscar = new JButton("Buscar");
        JButton verTodos = new JButton("Ver todos");
        botonBuscar.addActionListener(e -> controlador.buscarProductos(buscar.getText()));
        verTodos.addActionListener(e -> {
            buscar.setText("");
            controlador.buscarProductos("");
        });
        panel.add(botonBuscar);
        panel.add(verTodos);
        return panel;
    }

    public void mostrarProductos(ArrayList<Producto> productos) {
        modeloTabla.setRowCount(0);
        for (Producto producto : productos) {
            modeloTabla.addRow(new Object[] {producto.getCodigo(), producto.getNombre(),
                    String.format("%.2f", producto.getPrecio()), producto.getStock()});
        }
    }

    private void llenarFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            codigo.setText(modeloTabla.getValueAt(fila, 0).toString());
            nombre.setText(modeloTabla.getValueAt(fila, 1).toString());
            precio.setText(modeloTabla.getValueAt(fila, 2).toString());
            stock.setText(modeloTabla.getValueAt(fila, 3).toString());
        }
    }

    private void limpiarFormulario() {
        codigo.setText("");
        nombre.setText("");
        precio.setText("");
        stock.setText("");
        tabla.clearSelection();
    }
}
