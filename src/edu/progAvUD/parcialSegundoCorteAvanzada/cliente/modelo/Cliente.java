package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * La clase {@code Cliente} representa un cliente dentro del sistema cliente-servidor del juego.
 * 
 * Se encarga de mantener la configuración de red, incluyendo direcciones IP, puertos y los
 * flujos de comunicación mediante sockets. Además, administra los flujos de entrada y salida
 * para el envío y recepción de datos desde y hacia el servidor.
 * 
 * Esta clase permite gestionar dos canales de comunicación (posiblemente uno principal y otro
 * para funciones secundarias como chat privado o monitoreo).
 * 
 * Los atributos IP y puertos son estáticos, lo que permite configurarlos a nivel global
 * para todas las instancias del cliente.
 * 
 * Autor: Cristianlol789
 */
public class Cliente {

    // Dirección IP del servidor al que se conecta el cliente
    private static String IP_SERVER;
    
    // Primer puerto de conexión al servidor
    private static int PUERTO_1;
    
    // Segundo puerto de conexión al servidor (posiblemente para un canal alternativo)
    private static int PUERTO_2;
    
    // Flujo de entrada desde el servidor (lectura de datos)
    private DataInputStream entrada;

    // Flujo de salida hacia el servidor (envío de datos)
    private DataOutputStream salida;

    // Segundo flujo de entrada (posiblemente para mensajes privados o monitoreo)
    private DataInputStream entrada2;

    // Socket principal de comunicación con el servidor
    private Socket comunication;

    // Segundo socket de comunicación (canal alternativo)
    private Socket comunication2;

    /**
     * Constructor vacío de la clase {@code Cliente}.
     * Se utiliza para inicializar un cliente sin establecer inmediatamente las conexiones.
     */
    public Cliente() {
    }

    /**
     * Obtiene el flujo de entrada principal del cliente.
     *
     * @return Objeto {@code DataInputStream} para leer datos desde el servidor.
     */
    public DataInputStream getEntrada() {
        return entrada;
    }

    /**
     * Establece el flujo de entrada principal del cliente.
     *
     * @param entrada Flujo de entrada {@code DataInputStream} que será asignado.
     */
    public void setEntrada(DataInputStream entrada) {
        this.entrada = entrada;
    }

    /**
     * Obtiene el flujo de salida principal del cliente.
     *
     * @return Objeto {@code DataOutputStream} para enviar datos al servidor.
     */
    public DataOutputStream getSalida() {
        return salida;
    }

    /**
     * Establece el flujo de salida principal del cliente.
     *
     * @param salida Flujo de salida {@code DataOutputStream} que será asignado.
     */
    public void setSalida(DataOutputStream salida) {
        this.salida = salida;
    }

    /**
     * Obtiene el segundo flujo de entrada del cliente.
     *
     * @return Objeto {@code DataInputStream} usado como canal alternativo de entrada.
     */
    public DataInputStream getEntrada2() {
        return entrada2;
    }

    /**
     * Establece el segundo flujo de entrada del cliente.
     *
     * @param entrada2 Flujo de entrada alternativo que será asignado.
     */
    public void setEntrada2(DataInputStream entrada2) {
        this.entrada2 = entrada2;
    }

    /**
     * Obtiene el socket principal de comunicación con el servidor.
     *
     * @return Objeto {@code Socket} principal.
     */
    public Socket getComunication() {
        return comunication;
    }

    /**
     * Establece el socket principal de comunicación con el servidor.
     *
     * @param comunication Objeto {@code Socket} a asignar.
     */
    public void setComunication(Socket comunication) {
        this.comunication = comunication;
    }

    /**
     * Obtiene el segundo socket de comunicación con el servidor.
     *
     * @return Objeto {@code Socket} alternativo.
     */
    public Socket getComunication2() {
        return comunication2;
    }

    /**
     * Establece el segundo socket de comunicación con el servidor.
     *
     * @param comunication2 Objeto {@code Socket} alternativo a asignar.
     */
    public void setComunication2(Socket comunication2) {
        this.comunication2 = comunication2;
    }

    /**
     * Obtiene la dirección IP configurada para el servidor.
     *
     * @return Cadena con la dirección IP del servidor.
     */
    public static String getIP_SERVER() {
        return IP_SERVER;
    }

    /**
     * Establece la dirección IP del servidor.
     *
     * @param IP_SERVER Cadena con la nueva dirección IP.
     */
    public static void setIP_SERVER(String IP_SERVER) {
        Cliente.IP_SERVER = IP_SERVER;
    }

    /**
     * Obtiene el primer puerto configurado para la conexión.
     *
     * @return Número del primer puerto.
     */
    public static int getPUERTO_1() {
        return PUERTO_1;
    }

    /**
     * Establece el primer puerto para la conexión.
     *
     * @param PUERTO_1 Número del nuevo puerto principal.
     */
    public static void setPUERTO_1(int PUERTO_1) {
        Cliente.PUERTO_1 = PUERTO_1;
    }

    /**
     * Obtiene el segundo puerto configurado para la conexión.
     *
     * @return Número del segundo puerto.
     */
    public static int getPUERTO_2() {
        return PUERTO_2;
    }

    /**
     * Establece el segundo puerto para la conexión.
     *
     * @param PUERTO_2 Número del nuevo puerto alternativo.
     */
    public static void setPUERTO_2(int PUERTO_2) {
        Cliente.PUERTO_2 = PUERTO_2;
    }
}