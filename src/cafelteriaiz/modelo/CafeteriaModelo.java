package cafelteriaiz.modelo;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import cafelteriaiz.util.UtilGenerico;

/** Reglas de productos, pedidos, inventario y reportes. No usa componentes Swing. */
public class CafeteriaModelo {
    private final RepositorioArchivos repositorio;
    private final ArrayList<Producto> productos;
    private final ArrayList<Pedido> pedidos;

    public CafeteriaModelo() throws IOException {
        repositorio = new RepositorioArchivos();
        productos = repositorio.cargarProductos();
        pedidos = repositorio.cargarPedidos(productos);
    }

    public ArrayList<Producto> getProductos() {
        return new ArrayList<>(productos);
    }

    public ArrayList<Pedido> getPedidos() {
        return new ArrayList<>(pedidos);
    }

    public ArrayList<Producto> buscarProductos(String texto) {
        ArrayList<Producto> encontrados = UtilGenerico.crearLista();
        String buscado = texto == null ? "" : texto.trim().toLowerCase();
        for (Producto producto : productos) {
            if (producto.getCodigo().toLowerCase().contains(buscado)
                    || producto.getNombre().toLowerCase().contains(buscado)) {
                UtilGenerico.agregarElementos(encontrados, producto);
            }
        }
        return encontrados;
    }

    public Producto buscarProducto(String codigo) {
        for (Producto producto : productos) {
            if (producto.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return producto;
            }
        }
        return null;
    }

    public void registrarProducto(String codigo, String nombre, double precio, int stock) throws IOException {
        validarProducto(codigo, nombre, precio, stock);
        if (buscarProducto(codigo) != null) {
            throw new IllegalArgumentException("Ya existe un producto con ese código.");
        }
        Producto nuevo = new Producto(codigo.trim(), nombre.trim(), precio, stock);
        UtilGenerico.agregarElementos(productos, nuevo);
        try {
            repositorio.guardarProductos(productos);
        } catch (IOException e) {
            productos.remove(nuevo);
            throw e;
        }
    }

    public void editarProducto(String codigo, String nombre, double precio, int stock) throws IOException {
        validarProducto(codigo, nombre, precio, stock);
        Producto producto = buscarProducto(codigo);
        if (producto == null) {
            throw new IllegalArgumentException("Seleccione un producto de la tabla.");
        }
        String nombreAnterior = producto.getNombre();
        double precioAnterior = producto.getPrecio();
        int stockAnterior = producto.getStock();
        producto.setNombre(nombre.trim());
        producto.setPrecio(precio);
        producto.setStock(stock);
        try {
            repositorio.guardarProductos(productos);
        } catch (IOException e) {
            producto.setNombre(nombreAnterior);
            producto.setPrecio(precioAnterior);
            producto.setStock(stockAnterior);
            throw e;
        }
    }

    public void reponerStock(String codigo, int cantidad) throws IOException {
        Producto producto = buscarProducto(codigo);
        if (producto == null || cantidad <= 0) {
            throw new IllegalArgumentException("Seleccione un producto e ingrese una cantidad positiva.");
        }
        producto.aumentarStock(cantidad);
        try {
            repositorio.guardarProductos(productos);
        } catch (IOException e) {
            producto.disminuirStock(cantidad);
            throw e;
        }
    }

    public Pedido crearPedidoVacio() {
        int siguienteNumero = 1;
        for (Pedido pedido : pedidos) {
            if (pedido.getNumero() >= siguienteNumero) {
                siguienteNumero = pedido.getNumero() + 1;
            }
        }
        return new Pedido(siguienteNumero, null, 0);
    }

    public void confirmarPedido(Pedido pedido, double porcentajeIva) throws IOException {
        if (pedido == null || pedido.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Agregue al menos un producto al pedido.");
        }
        if (porcentajeIva < 0 || porcentajeIva > 100) {
            throw new IllegalArgumentException("La tasa debe estar entre 0 y 100 por ciento.");
        }
        for (DetallePedido detalle : pedido.getDetalles()) {
            if (detalle.getCantidad() > detalle.getProducto().getStock()) {
                throw new IllegalArgumentException("No hay suficiente stock para "
                        + detalle.getProducto().getNombre() + ".");
            }
        }

        pedido.setFecha(LocalDate.now());
        pedido.setPorcentajeIva(porcentajeIva);
        for (DetallePedido detalle : pedido.getDetalles()) {
            detalle.getProducto().disminuirStock(detalle.getCantidad());
        }
        UtilGenerico.agregarElementos(pedidos, pedido);
        try {
            repositorio.guardarProductos(productos);
            repositorio.guardarPedidos(pedidos);
        } catch (IOException e) {
            pedidos.remove(pedido);
            pedido.setFecha(null);
            for (DetallePedido detalle : pedido.getDetalles()) {
                detalle.getProducto().aumentarStock(detalle.getCantidad());
            }
            try {
                repositorio.guardarProductos(productos);
                repositorio.guardarPedidos(pedidos);
            } catch (IOException restauracion) {
                e.addSuppressed(restauracion);
            }
            throw e;
        }
    }

    public ResumenVentas obtenerResumen(LocalDate fecha) {
        double total = 0;
        int cantidadPedidos = 0;
        int pedidosSinFecha = 0;
        Map<String, Integer> vendidos = new LinkedHashMap<>();

        for (Pedido pedido : pedidos) {
            if (pedido.getFecha() == null) {
                pedidosSinFecha++;
            } else if (pedido.getFecha().equals(fecha)) {
                cantidadPedidos++;
                total += pedido.calcularTotal();
                for (DetallePedido detalle : pedido.getDetalles()) {
                    String clave = detalle.getProducto().getNombre() + " (" + detalle.getProducto().getCodigo() + ")";
                    int anterior = vendidos.getOrDefault(clave, 0);
                    vendidos.put(clave, anterior + detalle.getCantidad());
                }
            }
        }
        return new ResumenVentas(fecha, total, cantidadPedidos, pedidosSinFecha, vendidos);
    }

    private void validarProducto(String codigo, String nombre, double precio, int stock) {
        if (codigo == null || codigo.trim().isEmpty() || nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese el código y el nombre del producto.");
        }
        if (precio <= 0 || Double.isNaN(precio) || Double.isInfinite(precio)) {
            throw new IllegalArgumentException("El precio debe ser un número mayor que cero.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
    }
}
