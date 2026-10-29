package cafelteriaiz.vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cafelteriaiz.control.ControladorCafeteria;
import cafelteriaiz.modelo.Pedido;
import cafelteriaiz.modelo.Producto;
import cafelteriaiz.modelo.ResumenVentas;
import cafelteriaiz.util.UtilGenerico;

public class VentanaPrincipal extends JFrame {
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel panelTarjetas = new JPanel(tarjetas);
    private final PanelProductos panelProductos;
    private final PanelPedido panelPedido;
    private final PanelReportes panelReportes;

    public VentanaPrincipal(ControladorCafeteria controlador) {
        setTitle("Cafetería El Buen Sabor - Sistema de Pedidos");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panelProductos = new PanelProductos(controlador);
        panelPedido = new PanelPedido(controlador);
        panelReportes = new PanelReportes(controlador);

        panelTarjetas.add(crearInicio(), "INICIO");
        panelTarjetas.add(panelProductos, "PRODUCTOS");
        panelTarjetas.add(panelPedido, "PEDIDOS");
        panelTarjetas.add(panelReportes, "REPORTES");

        getContentPane().add(crearCabecera(), BorderLayout.NORTH);
        getContentPane().add(panelTarjetas, BorderLayout.CENTER);
    }

    private JPanel crearCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        JLabel titulo = new JLabel("CAFETERÍA EL BUEN SABOR");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(new Color(35, 78, 120));
        cabecera.add(titulo, BorderLayout.NORTH);

        JPanel navegacion = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        ArrayList<String> opciones = UtilGenerico.crearLista("INICIO", "PRODUCTOS");
        UtilGenerico.agregarElementos(opciones, "PEDIDOS", "REPORTES");
        for (String opcion : opciones) {
            JButton boton = new JButton(opcion);
            boton.addActionListener(e -> tarjetas.show(panelTarjetas, opcion));
            navegacion.add(boton);
        }
        cabecera.add(navegacion, BorderLayout.SOUTH);
        return cabecera;
    }

    private JPanel crearInicio() {
        JPanel inicio = new JPanel();
        inicio.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        inicio.setLayout(null);
        JLabel mensaje = new JLabel("Seleccione Productos, Pedidos o Reportes en el menú superior.");
        mensaje.setBounds(30, 30, 826, 21);
        mensaje.setFont(new Font("SansSerif", Font.PLAIN, 16));
        inicio.add(mensaje);
        JLabel label = new JLabel("Los pedidos antiguos se conservan, pero no tienen fecha registrada.");
        label.setBounds(137, 61, 719, 506);
        inicio.add(label);
        
        JLabel lblNewLabel = new JLabel("Lisbeth Calvachi");
        lblNewLabel.setBounds(30, 61, 115, 506);
        inicio.add(lblNewLabel);
        return inicio;
    }

    public void actualizarProductos(ArrayList<Producto> productos) {
        panelProductos.mostrarProductos(productos);
        panelPedido.actualizarProductos(productos);
    }

    public void actualizarPedido(Pedido pedido, ArrayList<Producto> productos) {
        panelPedido.mostrarPedido(pedido, productos);
    }

    public void mostrarResumen(ResumenVentas resumen) {
        panelReportes.mostrarResumen(resumen);
    }
}
