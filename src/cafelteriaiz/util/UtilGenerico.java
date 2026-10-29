package cafelteriaiz.util;

import java.util.ArrayList;

/** Métodos sencillos que se pueden reutilizar con diferentes tipos de datos. */
public class UtilGenerico {

    @SafeVarargs
    public static <T> ArrayList<T> crearLista(T... elementos) {
        ArrayList<T> lista = new ArrayList<>();
        if (elementos != null) {
            for (T elemento : elementos) {
                lista.add(elemento);
            }
        }
        return lista;
    }

    @SafeVarargs
    public static <T> void agregarElementos(ArrayList<T> lista, T... elementos) {
        if (lista != null && elementos != null) {
            for (T elemento : elementos) {
                lista.add(elemento);
            }
        }
    }
}
