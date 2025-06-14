/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

    public ControlPrincipal() {
        this.controlGrafico = new ControlGrafico(this);
        this.controlCliente = new ControlCliente(this);
    }
    
    public ConexionPropiedades crearConexionPropiedades(){
        ConexionPropiedades conexionPropiedades = null;
        boolean flag = true;
        do{
            try{
                conexionPropiedades = new ConexionPropiedades(controlGrafico.pedirArchivoPropiedades());
                if (conexionPropiedades != null) {
                    flag = false;
                }
            }catch(Exception e){
                controlGrafico.mostrarMensajeError("Ocurrio un error en el archivo de propiedades");
            }
        }while(flag);
        return conexionPropiedades;
    }
    
    public void cargarDatosSocket(){
        ConexionPropiedades conexionPropiedades = crearConexionPropiedades();
        try{
            Properties propiedadesSocket = conexionPropiedades.cargarPropiedades();
            String ipServer = propiedadesSocket.getProperty("IP_SERVER");
            String puerto1 = propiedadesSocket.getProperty("PUERTO_1");
            String puerto2 = propiedadesSocket.getProperty("PUERTO_2");
            controlCliente.asignarDatosConexionCliente(ipServer, puerto1, puerto2);
        }catch(IOException e){
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
    
    public void conectarAServer(){
        controlCliente.conectarAServer();
    }
    
}
