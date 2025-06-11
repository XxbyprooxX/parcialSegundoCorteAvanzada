package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Clase que representa un servidor que gestiona la conexión entre dos clientes
 * a través de sockets. Contiene los streams de entrada y salida necesarios 
 * para la comunicación entre clientes y un identificador de usuario.
 * 
 * Esta clase es utilizada en un entorno de chat o comunicación punto a punto
 * donde se requiere el envío y recepción de mensajes entre dos clientes.
 * 
 * @author Andres Felipe
 */
public class Servidor {

    /** Socket para el primer cliente conectado al servidor */
    private Socket servidorCliente1;

    /** Socket para el segundo cliente conectado al servidor */
    private Socket servidorCliente2;

    /** Flujo de entrada de datos desde el cliente 1 */
    private DataInputStream servidorInformacionEntrada1;

    /** Flujo de salida de datos hacia el cliente 1 */
    private DataOutputStream servidorInformacionSalida1;

    /** Flujo de salida de datos hacia el cliente 2 */
    private DataOutputStream servidorInformacionSalida2;

    /** Nombre de usuario que representa al cliente en la comunicación */
    private String nombreUsuario;

    /**
     * Constructor de la clase Servidor.
     * Inicializa los sockets de los clientes y el nombre del usuario.
     * 
     * @param servidorCliente1 Socket del primer cliente
     * @param servidorCliente2 Socket del segundo cliente
     * @param nombreUsuario Nombre de usuario asociado a la conexión
     */
    public Servidor(Socket servidorCliente1, Socket servidorCliente2, String nombreUsuario) {
        this.servidorCliente1 = servidorCliente1;
        this.servidorCliente2 = servidorCliente2;
        this.nombreUsuario = nombreUsuario;
    }

    /** 
     * Obtiene el socket del cliente 1.
     * @return Socket del primer cliente
     */
    public Socket getServidorCliente1() {
        return servidorCliente1;
    }

    /**
     * Establece el socket del cliente 1.
     * @param servidorCliente1 Nuevo socket para el primer cliente
     */
    public void setServidorCliente1(Socket servidorCliente1) {
        this.servidorCliente1 = servidorCliente1;
    }

    /**
     * Obtiene el socket del cliente 2.
     * @return Socket del segundo cliente
     */
    public Socket getServidorCliente2() {
        return servidorCliente2;
    }

    /**
     * Establece el socket del cliente 2.
     * @param servidorCliente2 Nuevo socket para el segundo cliente
     */
    public void setServidorCliente2(Socket servidorCliente2) {
        this.servidorCliente2 = servidorCliente2;
    }

    /**
     * Obtiene el flujo de entrada de datos desde el cliente 1.
     * @return DataInputStream del cliente 1
     */
    public DataInputStream getServidorInformacionEntrada1() {
        return servidorInformacionEntrada1;
    }

    /**
     * Establece el flujo de entrada de datos desde el cliente 1.
     * @param servidorInformacionEntrada1 Nuevo DataInputStream para el cliente 1
     */
    public void setServidorInformacionEntrada1(DataInputStream servidorInformacionEntrada1) {
        this.servidorInformacionEntrada1 = servidorInformacionEntrada1;
    }

    /**
     * Obtiene el flujo de salida de datos hacia el cliente 1.
     * @return DataOutputStream para el cliente 1
     */
    public DataOutputStream getServidorInformacionSalida1() {
        return servidorInformacionSalida1;
    }

    /**
     * Establece el flujo de salida de datos hacia el cliente 1.
     * @param servidorInformacionSalida1 Nuevo DataOutputStream para el cliente 1
     */
    public void setServidorInformacionSalida1(DataOutputStream servidorInformacionSalida1) {
        this.servidorInformacionSalida1 = servidorInformacionSalida1;
    }

    /**
     * Obtiene el flujo de salida de datos hacia el cliente 2.
     * @return DataOutputStream para el cliente 2
     */
    public DataOutputStream getServidorInformacionSalida2() {
        return servidorInformacionSalida2;
    }

    /**
     * Establece el flujo de salida de datos hacia el cliente 2.
     * @param servidorInformacionSalida2 Nuevo DataOutputStream para el cliente 2
     */
    public void setServidorInformacionSalida2(DataOutputStream servidorInformacionSalida2) {
        this.servidorInformacionSalida2 = servidorInformacionSalida2;
    }

    /**
     * Obtiene el nombre del usuario asociado a la conexión.
     * @return Nombre del usuario
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Establece el nombre del usuario asociado a la conexión.
     * @param nombreUsuario Nuevo nombre de usuario
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}
