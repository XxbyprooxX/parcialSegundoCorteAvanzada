/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.cliente.modelo.Cliente;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Andres Felipe
 */
public class ControlCliente {

    private Cliente cliente;
    private ThreadCliente threadCliente;
    private ControlPrincipal controlPrincipal;

    public ControlCliente(ControlPrincipal controlPrincipal) {
        this.controlPrincipal = controlPrincipal;
    }

    public void asignarDatosConexionCliente(String ip, String puerto1, String puerto2) {
        Cliente.setIP_SERVER(ip);

        try {
            int puerto1Int = Integer.parseInt(puerto1);
            int puerto2Int = Integer.parseInt(puerto2);
            Cliente.setPUERTO_1(puerto1Int);
            Cliente.setPUERTO_2(puerto2Int);
        } catch (NumberFormatException e) {

        }
        System.out.println(Cliente.getIP_SERVER() + ", " + Cliente.getPUERTO_1() + ", " + Cliente.getPUERTO_2());
    }

    public void conectarAServer() {

        try {
            Socket comunicacion1 = new Socket(Cliente.getIP_SERVER(), Cliente.getPUERTO_1());
            Socket comunicacion2 = new Socket(Cliente.getIP_SERVER(), Cliente.getPUERTO_2());

            cliente.setComunication(comunicacion1);
            cliente.setComunication2(comunicacion2);
            cliente.setEntrada(new DataInputStream(comunicacion1.getInputStream()));
            cliente.setSalida(new DataOutputStream(comunicacion1.getOutputStream()));
            cliente.setEntrada2(new DataInputStream(comunicacion2.getInputStream()));
            controlPrincipal.mostrarMensajeExito("Se ha hecho la conexion al server");
        } catch (IOException e) {
            controlPrincipal.mostrarMensajeError("No se ha podido establecer una conexion con el servidor. Intentelo de nuevo");
            System.exit(0);
        }

    }

    public String enviarCredencialesCliente(String usuario, String contrasena) {
        String respuesta = "";
        try {
            cliente.getSalida().writeUTF("login," + usuario + "," + contrasena);
            String estado = cliente.getEntrada().readUTF();

            if (estado.equalsIgnoreCase("valido")) {
                respuesta = "logeado";
            } else if (estado.equalsIgnoreCase("invalido")) {
                respuesta = "noLogeado";
            } else if (estado.equalsIgnoreCase("yaConectado")) {
                respuesta = "conectado";
            }

        } catch (IOException ex) {
            controlPrincipal.mostrarMensajeError("Ocurrio algun error al mandar credenciales");
        }
        return respuesta;
    }

    public void crearCliente() {
        this.cliente = new Cliente();
    }

    public void crearThreadCliente() {
        this.threadCliente = new ThreadCliente(cliente.getEntrada(), cliente.getSalida(), this);
        threadCliente.start();
    }

    /**
     * Muestra un mensaje de error en la interfaz
     *
     * @param mensaje contenido del error
     */
    public void mostrarMensajeError(String mensaje) {
        controlPrincipal.mostrarMensajeError(mensaje);
    }

    public void mostrarMensajeChatJuego(String msg) {
        controlPrincipal.mostrarMensajeChatJuego(msg);
    }

    public String getCoordenadasCartas() {
        return controlPrincipal.getCoordenadasCartas();
    }

    public void setCoordenadasCartas(String coordenadasCartas) {
        controlPrincipal.setCoordenadasCartas(coordenadasCartas);
    }

    public int getPasoActivoCoordenadas() {
        return controlPrincipal.getPasoActivoCoordenadas();
    }

    public void setPasoActivoCoordenadas(int pasoActivoCoordenadas) {
        controlPrincipal.setPasoActivoCoordenadas(pasoActivoCoordenadas);
    }
    
    public void bloquearEntradaTextoChatJuego(){
        controlPrincipal.bloquearEntradaTextoChatJuego();
    }
    
    public void permitirEntradaTextoChatJuego(){
        controlPrincipal.permitirEntradaTextoChatJuego();
    }

}
