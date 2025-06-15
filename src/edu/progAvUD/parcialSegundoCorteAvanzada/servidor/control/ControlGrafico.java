package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.vista.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JToggleButton;

/**
 *
 * @author Andres Felipe
 */
public class ControlGrafico implements ActionListener {

    private ControlPrincipal controlPrincipal;
    private VentanaPrincipal ventanaPrincipal;

    public ControlGrafico(ControlPrincipal controlPrincipal) {
        this.controlPrincipal = controlPrincipal;
        this.ventanaPrincipal = new VentanaPrincipal(this);
        ventanaPrincipal.panelInicial.jButtonPropiedadesBD.addActionListener(this);
        ventanaPrincipal.panelInicial.jButtonPropiedadesJugadores.addActionListener(this);
        ventanaPrincipal.panelInicial.jButtonPropiedadesSockets.addActionListener(this);
        ventanaPrincipal.panelInicial.jButtonPropiedadesJugadores.setEnabled(false);
        ventanaPrincipal.panelInicial.jButtonPropiedadesSockets.setEnabled(false);
        ventanaPrincipal.mostrarPanel(ventanaPrincipal.panelInicial);
        
        ventanaPrincipal.panelConsolaServidor.jButtonEmpezarJuego.setVisible(false);
        ventanaPrincipal.panelConsolaServidor.jButtonEmpezarJuego.addActionListener(this);
        
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventanaPrincipal.panelInicial.jButtonPropiedadesBD) {
            controlPrincipal.cargarDatosBD();
            ventanaPrincipal.panelInicial.jButtonPropiedadesSockets.setEnabled(true);
            ventanaPrincipal.panelInicial.jButtonPropiedadesBD.setEnabled(false);
        }
        if (e.getSource() == ventanaPrincipal.panelInicial.jButtonPropiedadesJugadores) {
            ventanaPrincipal.panelInicial.jButtonPropiedadesJugadores.setEnabled(false);
            controlPrincipal.cargarDatosJugadoresPropiedades();
            ventanaPrincipal.mostrarPanel(ventanaPrincipal.panelConsolaServidor);
            controlPrincipal.empezarServer();
        }
        if (e.getSource() == ventanaPrincipal.panelInicial.jButtonPropiedadesSockets) {
            controlPrincipal.cargarPropiedadesSockets();
            ventanaPrincipal.panelInicial.jButtonPropiedadesJugadores.setEnabled(true);
            ventanaPrincipal.panelInicial.jButtonPropiedadesSockets.setEnabled(false);
        }
        if(e.getSource() == ventanaPrincipal.panelConsolaServidor.jButtonEmpezarJuego){
            anadirCartasJuego();
            ventanaPrincipal.mostrarPanel(ventanaPrincipal.panelJuego);
        }
            
    }

    /**
     * Muestra un mensaje en la consola gráfica del servidor.
     *
     * @param mensaje Texto a mostrar en el área de consola.
     */
    public void mostrarMensajeConsolaServidor(String mensaje) {
        ventanaPrincipal.mostrarMensajeConsolaServidor(mensaje);
    }
    
    
    public void ocultarBotonIniciarJuego(boolean estado){
        ventanaPrincipal.panelConsolaServidor.jButtonEmpezarJuego.setVisible(estado);
    }
    
    public void anadirCartasJuego(){
        for(int i=0;i<40;i++){
            ventanaPrincipal.panelJuego.anadirBoton(i);
        }
    }
    
}
