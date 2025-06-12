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

    /**
     * Muestra un cuadro de diálogo para que el usuario escriba un dato que
     * falta.
     *
     * @param datoFaltante descripción del dato que debe ingresar el usuario.
     * @return el texto ingresado por el usuario.
     */
    public String mostrarJOptionEscribirDatoFaltante(String datoFaltante) {
        return ventanaPrincipal.mostrarJOptionEscribirDatoFaltante(datoFaltante);
    }
}
