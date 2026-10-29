package cafelteriaiz.modelo;

import java.time.LocalDate;
import java.util.ArrayList;

import cafelteriaiz.util.UtilGenerico;

public class Pedido {
    private final int numero;
    private LocalDate fecha;
    private double porcentajeIva;
    private final ArrayList<DetallePedido> detalles;

    public Pedido(int numero, LocalDate fecha, double porcentajeIva) {
        this.numero = numero;
        this.fecha = fecha;
        this.porcentajeIva = porcentajeIva;
        this.detalles = UtilGenerico.crearLista();
    }

    public int getNumero() {
        return numero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getPorcentajeIva() {
        return porcentajeIva;
    }

    public void setPorcentajeIva(double porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    public ArrayList<DetallePedido> getDetalles() {
        return detalles;
    }

    public boolean agregarProducto(Producto producto, int cantidad) {
        if (producto == null || cantidad <= 0) {
            return false;
        }

        for (DetallePedido detalle : detalles) {
            if (detalle.getProducto().getCodigo().equalsIgnoreCase(producto.getCodigo())) {
                int nuevaCantidad = detalle.getCantidad() + cantidad;
                if (nuevaCantidad > producto.getStock()) {
                    return false;
                }
                detalle.setCantidad(nuevaCantidad);
                return true;
            }
        }

        if (cantidad > producto.getStock()) {
            return false;
        }
        UtilGenerico.agregarElementos(detalles, new DetallePedido(producto, cantidad));
        return true;
    }

    /** Carga detalles guardados sin volver a descontar el stock. */
    public void agregarDetalleGuardado(Producto producto, int cantidad) {
        if (producto != null && cantidad > 0) {
            UtilGenerico.agregarElementos(detalles, new DetallePedido(producto, cantidad));
        }
    }

    public double calcularSubtotal() {
        double subtotal = 0;
        for (DetallePedido detalle : detalles) {
            subtotal += detalle.calcularSubtotal();
        }
        return subtotal;
    }

    public double calcularIva() {
        return calcularIva(porcentajeIva);
    }

    public double calcularIva(double porcentaje) {
        return calcularSubtotal() * porcentaje / 100.0;
    }

    public double calcularTotal() {
        return calcularSubtotal() + calcularIva();
    }
}
