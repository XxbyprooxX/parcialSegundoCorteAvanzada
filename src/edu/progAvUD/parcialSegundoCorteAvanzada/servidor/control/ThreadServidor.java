package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.Servidor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Cristianlol789
 */
public class ThreadServidor extends Thread{
    
    /**
     * Objeto que representa la conexión con el cliente
     */
    private Servidor servidor;

    /**
     * Controlador principal del servidor para acceder a la consola y a la lista
     * de usuarios
     */
    private ControlServidor controlServidor;
    
    /**
     * Constructor del hilo servidor que inicia la conexión con los clientes.
     *
     * @param socketCliente1 Socket de comunicación para entrada/salida del
     * cliente.
     * @param socketCliente2 Socket adicional para enviar mensajes desde el
     * servidor.
     * @param controlServidor Referencia al controlador general del servidor.
     */
    public ThreadServidor(Socket socketCliente1, Socket socketCliente2, ControlServidor controlServidor) {
        String nombreUsuario = "";
        this.servidor = new Servidor(socketCliente1, socketCliente2, nombreUsuario);
        this.controlServidor = controlServidor;
    }
    
    /**
     * Método que se ejecuta cuando se inicia el hilo. Establece los flujos de
     * entrada/salida y maneja el ciclo de escucha del cliente.
     */
    @Override
    public void run() {
    controlServidor.mostrarMensajeConsolaServidor(".::Esperando Mensajes :");
        try {
            DataInputStream entrada = new DataInputStream(this.servidor.getServidorCliente1().getInputStream());
            this.servidor.setServidorInformacionEntrada1(entrada);

            DataOutputStream salida1 = new DataOutputStream(this.servidor.getServidorCliente1().getOutputStream());
            this.servidor.setServidorInformacionSalida1(salida1);

            DataOutputStream salida2 = new DataOutputStream(this.servidor.getServidorCliente2().getOutputStream());
            this.servidor.setServidorInformacionSalida2(salida2);

            servidor.setNombreUsuario(entrada.readUTF());

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }}
    
}
