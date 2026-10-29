package cafelteriaiz.modelo;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResumenVentas {
    private final LocalDate fecha;
    private final double total;
    private final int cantidadPedidos;
    private final int pedidosAntiguosSinFecha;
    private final Map<String, Integer> productosVendidos;

    public ResumenVentas(LocalDate fecha, double total, int cantidadPedidos,
            int pedidosAntiguosSinFecha, Map<String, Integer> productosVendidos) {
        this.fecha = fecha;
        this.total = total;
        this.cantidadPedidos = cantidadPedidos;
        this.pedidosAntiguosSinFecha = pedidosAntiguosSinFecha;
        this.productosVendidos = new LinkedHashMap<>(productosVendidos);
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public double getTotal() {
        return total;
    }

    public int getCantidadPedidos() {
        return cantidadPedidos;
    }

    public int getPedidosAntiguosSinFecha() {
        return pedidosAntiguosSinFecha;
    }

    public Map<String, Integer> getProductosVendidos() {
        return productosVendidos;
    }
}
