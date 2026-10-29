package cafelteriaiz.control;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import cafelteriaiz.modelo.CafeteriaModelo;
import cafelteriaiz.modelo.Pedido;
import cafelteriaiz.modelo.Producto;
import cafelteriaiz.modelo.ResumenVentas;
import cafelteriaiz.vista.VentanaPrincipal;

/** Recibe los eventos de la pantalla y llama al Modelo. */
public class ControladorCafeteria {
    private final CafeteriaModelo modelo;
    private final VentanaPrincipal ventana;
    private Pedido pedidoActual;

    public ControladorCafeteria() throws IOException {
        modelo = new CafeteriaModelo();
        ventana = new VentanaPrincipal(this);
        pedidoActual = modelo.crearPedidoVacio();
        actualizarPantallas();
    }

    public void iniciar() {
        ventana.setVisible(true);
    }

    public void registrarProducto(String codigo, String nombre, String precioTexto, String stockTexto) {
        try {
            modelo.registrarProducto(codigo, nombre, leerDecimal(precioTexto), Integer.parseInt(stockTexto.trim()));
            actualizarPantallas();
            mensaje("Producto registrado.");
        } catch (NumberFormatException e) {
            error("Revise que el precio y el stock sean números válidos.");
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (IOException e) {
            error("No se pudo guardar producto.txt: " + e.getMessage());
        }
    }

    public void editarProducto(String codigo, String nombre, String precioTexto, String stockTexto) {
        try {
            modelo.editarProducto(codigo, nombre, leerDecimal(precioTexto), Integer.parseInt(stockTexto.trim()));
            actualizarPantallas();
            mensaje("Producto actualizado.");
        } catch (NumberFormatException e) {
            error("Revise que el precio y el stock sean números válidos.");
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (IOException e) {
            error("No se pudo guardar producto.txt: " + e.getMessage());
        }
    }

    public void reponerStock(String codigo, String cantidadTexto) {
        try {
            modelo.reponerStock(codigo, Integer.parseInt(cantidadTexto.trim()));
            actualizarPantallas();
            mensaje("Stock repuesto.");
        } catch (NumberFormatException e) {
            error("Ingrese una cantidad entera válida.");
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (IOException e) {
            error("No se pudo guardar producto.txt: " + e.getMessage());
        }
    }

    public void buscarProductos(String texto) {
        ventana.actualizarProductos(modelo.buscarProductos(texto));
    }

    public void agregarAlPedido(String codigo, String cantidadTexto) {
        try {
            Producto producto = modelo.buscarProducto(codigo);
            int cantidad = Integer.parseInt(cantidadTexto.trim());
            if (!pedidoActual.agregarProducto(producto, cantidad)) {
                error("Revise el producto, la cantidad y el stock disponible.");
                return;
            }
            actualizarPantallas();
        } catch (NumberFormatException e) {
            error("Ingrese una cantidad entera válida.");
        }
    }

    public void confirmarPedido(String porcentajeIvaTexto) {
        try {
            double porcentajeIva = leerDecimal(porcentajeIvaTexto);
            modelo.confirmarPedido(pedidoActual, porcentajeIva);
            mensaje("Pedido guardado y stock actualizado.");
            pedidoActual = modelo.crearPedidoVacio();
            actualizarPantallas();
        } catch (NumberFormatException e) {
            error("Ingrese la tasa de IVA que indicó su docente. No hay una tasa fija en el programa.");
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (IOException e) {
            error("No se pudo guardar el pedido: " + e.getMessage());
        }
    }

    public void cancelarPedido() {
        pedidoActual = modelo.crearPedidoVacio();
        actualizarPantallas();
        mensaje("Pedido cancelado. El stock no cambió.");
    }

    public void consultarReporte(String fechaTexto) {
        try {
            LocalDate fecha = LocalDate.parse(fechaTexto.trim());
            ResumenVentas resumen = modelo.obtenerResumen(fecha);
            ventana.mostrarResumen(resumen);
        } catch (Exception e) {
            error("Escriba la fecha con el formato AAAA-MM-DD.");
        }
    }

    private void actualizarPantallas() {
        ArrayList<Producto> productos = modelo.getProductos();
        ventana.actualizarProductos(productos);
        ventana.actualizarPedido(pedidoActual, productos);
    }

    private double leerDecimal(String texto) {
        return Double.parseDouble(texto.trim().replace(',', '.'));
    }

    private void mensaje(String texto) {
        JOptionPane.showMessageDialog(ventana, texto, "Cafetería El Buen Sabor",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String texto) {
        JOptionPane.showMessageDialog(ventana, texto, "Revise los datos", JOptionPane.WARNING_MESSAGE);
    }
}
