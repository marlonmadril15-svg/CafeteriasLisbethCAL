package cafelteriaiz.modelo;

public class Producto {
    private final String codigo;
    private String nombre;
    private double precio;
    private int stock;

    public Producto(String codigo, String nombre, double precio, int stock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void aumentarStock(int cantidad) {
        stock += cantidad;
    }

    public void disminuirStock(int cantidad) {
        stock -= cantidad;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " | $" + String.format("%.2f", precio)
                + " | Stock: " + stock;
    }
}
