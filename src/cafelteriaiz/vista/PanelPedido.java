package cafelteriaiz.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import cafelteriaiz.control.ControladorCafeteria;
import cafelteriaiz.modelo.DetallePedido;
import cafelteriaiz.modelo.Pedido;
import cafelteriaiz.modelo.Producto;

public class PanelPedido extends JPanel {
    private final ControladorCafeteria controlador;
    private final JComboBox<Producto> productos = new JComboBox<>();
    private final JTextField cantidad = new JTextField(5);
    private final JTextField porcentajeIva = new JTextField(5);
    private final JLabel subtotal = new JLabel("Subtotal: $0.00");
    private final JLabel iva = new JLabel("IVA: por configurar");
    private final JLabel total = new JLabel("Total: por configurar");
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[] {"Producto", "Cantidad", "Precio", "Subtotal"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private int numeroPedidoMostrado = -1;
    private Pedido pedidoActual;

    public PanelPedido(ControladorCafeteria controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));
        add(crearParteSuperior(), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(crearParteInferior(), BorderLayout.SOUTH);
        porcentajeIva.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { actualizarTotales(); }
            public void removeUpdate(DocumentEvent e) { actualizarTotales(); }
            public void changedUpdate(DocumentEvent e) { actualizarTotales(); }
        });
    }

    private JPanel crearParteSuperior() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel campos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        campos.add(new JLabel("Producto:"));
        productos.setPrototypeDisplayValue(new Producto("CODIGO", "Producto de ejemplo", 0, 0));
        campos.add(productos);
        campos.add(new JLabel("Cantidad:"));
        campos.add(cantidad);
        JButton agregar = new JButton("Agregar producto");
        agregar.addActionListener(e -> {
            Producto producto = (Producto) productos.getSelectedItem();
            if (producto != null) {
                controlador.agregarAlPedido(producto.getCodigo(), cantidad.getText());
                cantidad.setText("");
            }
        });
        campos.add(agregar);
        panel.add(new JLabel("Nuevo pedido"), BorderLayout.NORTH);
        panel.add(campos, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearParteInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel resumen = new JPanel(new GridLayout(2, 3, 8, 4));
        resumen.add(subtotal);
        resumen.add(new JLabel("Tasa IVA (%) editable:"));
        resumen.add(porcentajeIva);
        resumen.add(iva);
        resumen.add(total);
        resumen.add(new JLabel("Ingrese la tasa indicada por su docente."));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmar = new JButton("Confirmar pedido");
        JButton cancelar = new JButton("Cancelar pedido");
        confirmar.addActionListener(e -> controlador.confirmarPedido(porcentajeIva.getText()));
        cancelar.addActionListener(e -> controlador.cancelarPedido());
        botones.add(confirmar);
        botones.add(cancelar);
        panel.add(resumen, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    public void actualizarProductos(ArrayList<Producto> lista) {
        String codigoSeleccionado = null;
        Producto actual = (Producto) productos.getSelectedItem();
        if (actual != null) {
            codigoSeleccionado = actual.getCodigo();
        }
        productos.removeAllItems();
        for (Producto producto : lista) {
            productos.addItem(producto);
        }
        if (codigoSeleccionado != null) {
            for (int i = 0; i < productos.getItemCount(); i++) {
                if (productos.getItemAt(i).getCodigo().equalsIgnoreCase(codigoSeleccionado)) {
                    productos.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public void mostrarPedido(Pedido pedido, ArrayList<Producto> lista) {
        if (pedido == null) {
            return;
        }
        if (numeroPedidoMostrado != pedido.getNumero()) {
            numeroPedidoMostrado = pedido.getNumero();
            porcentajeIva.setText("");
        }
        pedidoActual = pedido;
        actualizarProductos(lista);
        modeloTabla.setRowCount(0);
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            modeloTabla.addRow(new Object[] {producto.getNombre(), detalle.getCantidad(),
                    String.format("$%.2f", producto.getPrecio()),
                    String.format("$%.2f", detalle.calcularSubtotal())});
        }
        actualizarTotales();
    }

    private void actualizarTotales() {
        if (pedidoActual == null) {
            return;
        }
        double valorSubtotal = pedidoActual.calcularSubtotal();
        subtotal.setText(String.format("Subtotal: $%.2f", valorSubtotal));
        try {
            String texto = porcentajeIva.getText().trim().replace(',', '.');
            if (texto.isEmpty()) {
                throw new NumberFormatException();
            }
            double tasa = Double.parseDouble(texto);
            if (tasa < 0 || tasa > 100) {
                throw new NumberFormatException();
            }
            double valorIva = pedidoActual.calcularIva(tasa);
            iva.setText(String.format("IVA: $%.2f", valorIva));
            total.setText(String.format("Total: $%.2f", valorSubtotal + valorIva));
        } catch (NumberFormatException e) {
            iva.setText("IVA: por configurar");
            total.setText("Total: ingrese la tasa");
        }
    }
}
