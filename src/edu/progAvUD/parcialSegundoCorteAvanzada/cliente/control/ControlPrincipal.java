package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.ConexionPropiedades;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Andres Felipe
 */
public class ControlPrincipal {

    private ControlGrafico controlGrafico;
    private ControlCliente controlCliente;
    
    private boolean esperandoPrimera = true;

    public ControlPrincipal() {
        this.controlGrafico = new ControlGrafico(this);
        this.controlCliente = new ControlCliente(this);
    }

    public ConexionPropiedades crearConexionPropiedades() {
        ConexionPropiedades conexionPropiedades = null;
        boolean flag = true;
        do {
            try {
                conexionPropiedades = new ConexionPropiedades(controlGrafico.pedirArchivoPropiedades());
                if (conexionPropiedades != null) {
                    flag = false;
                }
            } catch (Exception e) {
                controlGrafico.mostrarMensajeError("Ocurrio un error en el archivo de propiedades");
            }
        } while (flag);
        return conexionPropiedades;
    }

    public void cargarDatosSocket() {
        ConexionPropiedades conexionPropiedades = crearConexionPropiedades();
        try {
            Properties propiedadesSocket = conexionPropiedades.cargarPropiedades();
            String ipServer = propiedadesSocket.getProperty("IP_SERVER");
            String puerto1 = propiedadesSocket.getProperty("PUERTO_1");
            String puerto2 = propiedadesSocket.getProperty("PUERTO_2");
            controlCliente.asignarDatosConexionCliente(ipServer, puerto1, puerto2);
        } catch (IOException e) {
            controlGrafico.mostrarMensajeError("No se pudo cargar el archivo propiedades de la conexion al socket");
        }
    }

    /**
     * Muestra un mensaje a la persona en caso de error
     *
     * @param mensaje a mostrar
     */
    public void mostrarMensajeError(String mensaje) {
        controlGrafico.mostrarMensajeError(mensaje);
    }

    /**
     * Muestra un mensaje a la persona en caso de exito
     *
     * @param mensaje a mostrar
     */
    public void mostrarMensajeExito(String mensaje) {
        controlGrafico.mostrarMensajeExito(mensaje);
    }

    public void conectarAServer() {
        controlCliente.conectarAServer();
    }

    public void crearCliente() {
        controlCliente.crearCliente();
    }

    public String enviarCredencialesCliente(String usuario, String contrasena) {
        return controlCliente.enviarCredencialesCliente(usuario, contrasena);
    }

    public void crearThreadCliente() {
        controlCliente.crearThreadCliente();
    }

    public void mostrarMensajeChatJuego(String msg) {
        controlGrafico.mostrarMensajeChatJuego(msg);
    }

    public ControlGrafico getControlGrafico() {
        return controlGrafico;
    }

    public void setControlGrafico(ControlGrafico controlGrafico) {
        this.controlGrafico = controlGrafico;
    }

    public ControlCliente getControlCliente() {
        return controlCliente;
    }

    public void setControlCliente(ControlCliente controlCliente) {
        this.controlCliente = controlCliente;
    }
    
    public void bloquearEntradaTextoChatJuego(){
        controlGrafico.bloquearEntradaTextoChatJuego();
    }
    
    public void permitirEntradaTextoChatJuego(){
        controlGrafico.permitirEntradaTextoChatJuego();
    }
    
    public void enviarPosicionCartas(int x1, int y1, int x2, int y2) throws IOException{
        controlCliente.enviarPosicionCartas(x1, y1, x2, y2);
    }

    public boolean isEsperandoPrimera() {
        return esperandoPrimera;
    }

    public void setEsperandoPrimera(boolean esperandoPrimera) {
        this.esperandoPrimera = esperandoPrimera;
    }
    
    
    
}
