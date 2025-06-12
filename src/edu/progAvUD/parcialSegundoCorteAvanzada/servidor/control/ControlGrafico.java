package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.vista.VentanaPrincipal;
import java.io.File;

/**
 *
 * @author Andres Felipe
 */
public class ControlGrafico {

    private ControlPrincipal controlPrincipal;
    private VentanaPrincipal ventanaPrincipal;

    public ControlGrafico(ControlPrincipal controlPrincipal) {
        this.controlPrincipal = controlPrincipal;
        this.ventanaPrincipal = new VentanaPrincipal();
    }

    public void mostrarMensajeError(String mensaje) {
        ventanaPrincipal.mostrarMensajeError(mensaje);
    }
    
    public void mostrarMensajeExito(String mensaje) {
        ventanaPrincipal.mostrarMensajeExito(mensaje);
    }

    /**
     * Solicita al usuario que seleccione un archivo de propiedades. Este
     * archivo puede contener configuraciones necesarias para el programa.
     *
     * @return el archivo seleccionado por el usuario.
     */
    public File pedirArchivoPropiedades() {
        return ventanaPrincipal.pedirArchivoPropiedades();
    }

}
