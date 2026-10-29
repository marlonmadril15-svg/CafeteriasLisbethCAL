package cafelteriaiz.modelo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cafelteriaiz.util.UtilGenerico;

/** Lee y guarda los mismos archivos de texto que usaba la Práctica 1. */
public class RepositorioArchivos {
    private final File archivoProductos = new File("producto.txt");
    private final File archivoPedidos = new File("pedidos.txt");
    private final File archivoFechas = new File("fechasPedidos.txt");

    public ArrayList<Producto> cargarProductos() throws IOException {
        ArrayList<Producto> productos = UtilGenerico.crearLista();
        if (!archivoProductos.exists()) {
            return productos;
        }

        try (BufferedReader lector = new BufferedReader(new FileReader(archivoProductos))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split(";", -1);
                if (datos.length == 4) {
                    try {
                        Producto producto = new Producto(datos[0].trim(), datos[1].trim(),
                                Double.parseDouble(datos[2].trim()), Integer.parseInt(datos[3].trim()));
                        if (buscar(productos, producto.getCodigo()) == null) {
                            UtilGenerico.agregarElementos(productos, producto);
                        }
                    } catch (NumberFormatException e) {
                        // Se omite una fila que no tenga precio o stock numérico.
                    }
                }
            }
        }
        return productos;
    }

    public ArrayList<Pedido> cargarPedidos(ArrayList<Producto> productos) throws IOException {
        ArrayList<Pedido> pedidos = UtilGenerico.crearLista();
        Map<Integer, DatosFecha> fechas = cargarFechas();
        if (!archivoPedidos.exists()) {
            return pedidos;
        }

        try (BufferedReader lector = new BufferedReader(new FileReader(archivoPedidos))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] partes = linea.split("\\|", -1);
                if (partes.length != 2) {
                    continue;
                }
                try {
                    int numero = Integer.parseInt(partes[0].trim());
                    DatosFecha datosFecha = fechas.get(numero);
                    LocalDate fecha = datosFecha == null ? null : datosFecha.fecha;
                    double iva = datosFecha == null ? 0 : datosFecha.porcentajeIva;
                    Pedido pedido = new Pedido(numero, fecha, iva);

                    for (String textoDetalle : partes[1].split(";")) {
                        if (textoDetalle.isBlank()) {
                            continue;
                        }
                        String[] detalle = textoDetalle.split(",", -1);
                        if (detalle.length == 2) {
                            Producto producto = buscar(productos, detalle[0].trim());
                            int cantidad = Integer.parseInt(detalle[1].trim());
                            pedido.agregarDetalleGuardado(producto, cantidad);
                        }
                    }
                    UtilGenerico.agregarElementos(pedidos, pedido);
                } catch (NumberFormatException e) {
                    // Se omite una fila dañada y se conservan las demás.
                }
            }
        }
        return pedidos;
    }

    public void guardarProductos(ArrayList<Producto> productos) throws IOException {
        try (PrintWriter escritor = new PrintWriter(new FileWriter(archivoProductos))) {
            for (Producto producto : productos) {
                escritor.println(producto.getCodigo() + ";" + producto.getNombre() + ";"
                        + producto.getPrecio() + ";" + producto.getStock());
            }
        }
    }

    public void guardarPedidos(ArrayList<Pedido> pedidos) throws IOException {
        // Este archivo conserva exactamente el formato numero|codigo,cantidad; de la Práctica 1.
        try (PrintWriter escritor = new PrintWriter(new FileWriter(archivoPedidos))) {
            for (Pedido pedido : pedidos) {
                StringBuilder linea = new StringBuilder().append(pedido.getNumero()).append('|');
                for (DetallePedido detalle : pedido.getDetalles()) {
                    linea.append(detalle.getProducto().getCodigo()).append(',')
                            .append(detalle.getCantidad()).append(';');
                }
                escritor.println(linea);
            }
        }

        // Las fechas nuevas van aparte; así no se inventa fecha para pedidos antiguos.
        try (PrintWriter escritor = new PrintWriter(new FileWriter(archivoFechas))) {
            for (Pedido pedido : pedidos) {
                if (pedido.getFecha() != null) {
                    escritor.println(pedido.getNumero() + "|" + pedido.getFecha() + "|"
                            + pedido.getPorcentajeIva());
                }
            }
        }
    }

    private Map<Integer, DatosFecha> cargarFechas() throws IOException {
        Map<Integer, DatosFecha> fechas = new HashMap<>();
        if (!archivoFechas.exists()) {
            return fechas;
        }
        try (BufferedReader lector = new BufferedReader(new FileReader(archivoFechas))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] partes = linea.split("\\|", -1);
                if (partes.length == 3) {
                    try {
                        int numero = Integer.parseInt(partes[0].trim());
                        LocalDate fecha = LocalDate.parse(partes[1].trim());
                        double iva = Double.parseDouble(partes[2].trim());
                        fechas.put(numero, new DatosFecha(fecha, iva));
                    } catch (RuntimeException e) {
                        // Se ignora una fecha adicional dañada.
                    }
                }
            }
        }
        return fechas;
    }

    private Producto buscar(ArrayList<Producto> productos, String codigo) {
        for (Producto producto : productos) {
            if (producto.getCodigo().equalsIgnoreCase(codigo)) {
                return producto;
            }
        }
        return null;
    }

    private static class DatosFecha {
        private final LocalDate fecha;
        private final double porcentajeIva;

        private DatosFecha(LocalDate fecha, double porcentajeIva) {
            this.fecha = fecha;
            this.porcentajeIva = porcentajeIva;
        }
    }
}
