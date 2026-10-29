package cafelteriaiz.control;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Punto de inicio del sistema de cafetería. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ControladorCafeteria controlador = new ControladorCafeteria();
                controlador.iniciar();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo iniciar el sistema: " + e.getMessage(),
                        "Error al abrir", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
