package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * La clase **`Servidor`** modela la conexión y los flujos de comunicación asociados a un cliente específico
 * dentro de la arquitectura del servidor. Aunque su nombre pueda sugerir que es el servidor en sí,
 * en este contexto, actúa como un **objeto de sesión** para manejar los detalles de un cliente
 * conectado, incluyendo sus sockets y los streams de datos para interactuar con él.
 *
 * Esta clase es fundamental para gestionar la comunicación bidireccional y la identificación
 * de cada usuario conectado, permitiendo que el servidor principal coordine las interacciones.
 *
 * @author Andres Felipe
 */
public class Servidor {
    
    /**
     * Puerto estático para la primera conexión del cliente.
     * Este puerto se utiliza para el flujo principal de datos (entrada/salida).
     */
    private static int PUERTO_1;
    
    /**
     * Puerto estático para la segunda conexión del cliente.
     * Este puerto podría utilizarse para un flujo de datos secundario, como notificaciones o mensajes adicionales.
     */
    private static int PUERTO_2;

    /**
     * El **socket principal** que representa la conexión del cliente con el servidor.
     * Este socket se utiliza para establecer los flujos de entrada y salida de datos del cliente.
     */
    private Socket servidorCliente1;

    /**
     * Un **socket secundario** para el mismo cliente. Su propósito podría ser enviar mensajes
     * unidireccionales desde el servidor al cliente, o manejar un tipo diferente de comunicación.
     * En un diseño común, un solo socket suele ser suficiente, pero dos pueden ser usados para separar funciones.
     */
    private Socket servidorCliente2;

    /**
     * El flujo de **entrada de datos** desde el cliente, asociado a `servidorCliente1`.
     * Permite al servidor leer la información enviada por el cliente.
     */
    private DataInputStream servidorInformacionEntrada1;

    /**
     * El flujo de **salida de datos** hacia el cliente, asociado a `servidorCliente1`.
     * Permite al servidor enviar información al cliente.
     */
    private DataOutputStream servidorInformacionSalida1;

    /**
     * El flujo de **salida de datos** hacia el cliente, asociado a `servidorCliente2`.
     * Utilizado para enviar información a través del segundo socket.
     */
    private DataOutputStream servidorInformacionSalida2;

    /**
     * El **nombre de usuario** que identifica al cliente en la aplicación.
     * Este nombre se establece típicamente después de un proceso de autenticación.
     */
    private String nombreUsuario;

    /**
     * Constructor de la clase `Servidor`.
     * Inicializa los sockets de los clientes con los que este "objeto de sesión de servidor"
     * se comunicará y establece el nombre de usuario inicial.
     *
     * @param servidorCliente1 El socket principal para la comunicación con el cliente.
     * @param servidorCliente2 El socket secundario, que podría usarse para un propósito de comunicación diferente.
     * @param nombreUsuario El nombre de usuario inicial asociado a esta conexión.
     */
    public Servidor(Socket servidorCliente1, Socket servidorCliente2, String nombreUsuario) {
        this.servidorCliente1 = servidorCliente1;
        this.servidorCliente2 = servidorCliente2;
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * Obtiene el socket principal (`servidorCliente1`) asociado a este cliente.
     * @return El {@link Socket} del primer cliente.
     */
    public Socket getServidorCliente1() {
        return servidorCliente1;
    }

    /**
     * Establece el socket principal (`servidorCliente1`) para este cliente.
     * @param servidorCliente1 El nuevo {@link Socket} para el primer cliente.
     */
    public void setServidorCliente1(Socket servidorCliente1) {
        this.servidorCliente1 = servidorCliente1;
    }

    /**
     * Obtiene el socket secundario (`servidorCliente2`) asociado a este cliente.
     * @return El {@link Socket} del segundo cliente.
     */
    public Socket getServidorCliente2() {
        return servidorCliente2;
    }

    /**
     * Establece el socket secundario (`servidorCliente2`) para este cliente.
     * @param servidorCliente2 El nuevo {@link Socket} para el segundo cliente.
     */
    public void voidsetServidorCliente2(Socket servidorCliente2) { // Changed method name to follow Java conventions
        this.servidorCliente2 = servidorCliente2;
    }

    /**
     * Obtiene el flujo de entrada de datos (`DataInputStream`) desde el cliente,
     * asociado al `servidorCliente1`.
     * @return El {@link DataInputStream} del cliente 1.
     */
    public DataInputStream getServidorInformacionEntrada1() {
        return servidorInformacionEntrada1;
    }

    /**
     * Establece el flujo de entrada de datos (`DataInputStream`) desde el cliente,
     * asociado al `servidorCliente1`.
     * @param servidorInformacionEntrada1 El nuevo {@link DataInputStream} para el cliente 1.
     */
    public void setServidorInformacionEntrada1(DataInputStream servidorInformacionEntrada1) {
        this.servidorInformacionEntrada1 = servidorInformacionEntrada1;
    }

    /**
     * Obtiene el flujo de salida de datos (`DataOutputStream`) hacia el cliente,
     * asociado al `servidorCliente1`.
     * @return El {@link DataOutputStream} para el cliente 1.
     */
    public DataOutputStream getServidorInformacionSalida1() {
        return servidorInformacionSalida1;
    }

    /**
     * Establece el flujo de salida de datos (`DataOutputStream`) hacia el cliente,
     * asociado al `servidorCliente1`.
     * @param servidorInformacionSalida1 El nuevo {@link DataOutputStream} para el cliente 1.
     */
    public void setServidorInformacionSalida1(DataOutputStream servidorInformacionSalida1) {
        this.servidorInformacionSalida1 = servidorInformacionSalida1;
    }

    /**
     * Obtiene el flujo de salida de datos (`DataOutputStream`) hacia el cliente,
     * asociado al `servidorCliente2`.
     * @return El {@link DataOutputStream} para el cliente 2.
     */
    public DataOutputStream getServidorInformacionSalida2() {
        return servidorInformacionSalida2;
    }

    /**
     * Establece el flujo de salida de datos (`DataOutputStream`) hacia el cliente,
     * asociado al `servidorCliente2`.
     * @param servidorInformacionSalida2 El nuevo {@link DataOutputStream} para el cliente 2.
     */
    public void setServidorInformacionSalida2(DataOutputStream servidorInformacionSalida2) {
        this.servidorInformacionSalida2 = servidorInformacionSalida2;
    }

    /**
     * Obtiene el nombre de usuario asociado a esta sesión de cliente.
     * @return El nombre del usuario.
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Establece el nombre de usuario para esta sesión de cliente.
     * @param nombreUsuario El nuevo nombre de usuario.
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * Obtiene el valor del primer puerto estático configurado para el servidor.
     * @return El número del {@link #PUERTO_1}.
     */
    public static int getPUERTO_1() {
        return PUERTO_1;
    }

    /**
     * Establece el valor del primer puerto estático para el servidor.
     * @param PUERTO_1 El nuevo número para el {@link #PUERTO_1}.
     */
    public static void setPUERTO_1(int PUERTO_1) {
        Servidor.PUERTO_1 = PUERTO_1;
    }

    /**
     * Obtiene el valor del segundo puerto estático configurado para el servidor.
     * @return El número del {@link #PUERTO_2}.
     */
    public static int getPUERTO_2() {
        return PUERTO_2;
    }

    /**
     * Establece el valor del segundo puerto estático para el servidor.
     * @param PUERTO_2 El nuevo número para el {@link #PUERTO_2}.
     */
    public static void setPUERTO_2(int PUERTO_2) {
        Servidor.PUERTO_2 = PUERTO_2;
    }
}