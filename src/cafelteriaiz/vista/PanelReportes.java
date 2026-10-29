package cafelteriaiz.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import cafelteriaiz.control.ControladorCafeteria;
import cafelteriaiz.modelo.ResumenVentas;

public class PanelReportes extends JPanel {
    private final ControladorCafeteria controlador;
    private final JTextField fecha = new JTextField(10);
    private final JLabel pedidos = new JLabel("Pedidos: 0");
    private final JLabel ventas = new JLabel("Ventas: $0.00");
    private final JLabel avisoFecha = new JLabel("Los pedidos anteriores sin fecha no se suman a este día.");
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[] {"Producto", "Unidades vendidas"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    public PanelReportes(ControladorCafeteria controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));
        fecha.setText(java.time.LocalDate.now().toString());
        add(crearParteSuperior(), BorderLayout.NORTH);
        add(new JScrollPane(new JTable(modeloTabla)), BorderLayout.CENTER);
        add(crearParteInferior(), BorderLayout.SOUTH);
    }

    private JPanel crearParteSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Reporte de ventas por día (AAAA-MM-DD):"));
        panel.add(fecha);
        JButton consultar = new JButton("Consultar");
        consultar.addActionListener(e -> controlador.consultarReporte(fecha.getText()));
        panel.add(consultar);
        return panel;
    }

    private JPanel crearParteInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel datos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datos.add(pedidos);
        datos.add(ventas);
        panel.add(datos, BorderLayout.NORTH);
        panel.add(avisoFecha, BorderLayout.SOUTH);
        return panel;
    }

    public void mostrarResumen(ResumenVentas resumen) {
        pedidos.setText("Pedidos con fecha: " + resumen.getCantidadPedidos());
        ventas.setText(String.format("Ventas de %s: $%.2f", resumen.getFecha(), resumen.getTotal()));
        if (resumen.getCantidadPedidos() == 0) {
            avisoFecha.setText("No hay pedidos con fecha para ese día. "
                    + resumen.getPedidosAntiguosSinFecha() + " pedidos antiguos no tienen fecha.");
        } else {
            avisoFecha.setText("Pedidos antiguos sin fecha, excluidos del reporte diario: "
                    + resumen.getPedidosAntiguosSinFecha());
        }

        modeloTabla.setRowCount(0);
        ArrayList<Map.Entry<String, Integer>> vendidos = new ArrayList<>(
                resumen.getProductosVendidos().entrySet());
        Collections.sort(vendidos, Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed());
        for (Map.Entry<String, Integer> producto : vendidos) {
            modeloTabla.addRow(new Object[] {producto.getKey(), producto.getValue()});
        }
    }
}
