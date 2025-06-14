package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 *
 * @author Cristianlol789
 */
public class Cliente {

    private static String IP_SERVER;
    
    private static int PUERTO_1;
    
    private static int PUERTO_2;
    
    // Flujo de entrada desde el servidor (lectura de datos)
    private DataInputStream entrada;

    // Flujo de salida hacia el servidor (envío de datos)
    private DataOutputStream salida;

    // Segundo flujo de entrada (posiblemente para mensajes privados o monitoreo)
    private DataInputStream entrada2;

    // Socket principal de comunicación con el servidor
    private Socket comunication;

    // Segundo socket de comunicación (posiblemente para otro canal)
    private Socket comunication2;

    public Cliente() {
    }

    public DataInputStream getEntrada() {
        return entrada;
    }

    public void setEntrada(DataInputStream entrada) {
        this.entrada = entrada;
    }

    public DataOutputStream getSalida() {
        return salida;
    }

    public void setSalida(DataOutputStream salida) {
        this.salida = salida;
    }

    public DataInputStream getEntrada2() {
        return entrada2;
    }

    public void setEntrada2(DataInputStream entrada2) {
        this.entrada2 = entrada2;
    }

    public Socket getComunication() {
        return comunication;
    }

    public void setComunication(Socket comunication) {
        this.comunication = comunication;
    }

    public Socket getComunication2() {
        return comunication2;
    }

    public void setComunication2(Socket comunication2) {
        this.comunication2 = comunication2;
    }

    public static String getIP_SERVER() {
        return IP_SERVER;
    }

    public static void setIP_SERVER(String IP_SERVER) {
        Cliente.IP_SERVER = IP_SERVER;
    }

    public static int getPUERTO_1() {
        return PUERTO_1;
    }

    public static void setPUERTO_1(int PUERTO_1) {
        Cliente.PUERTO_1 = PUERTO_1;
    }

    public static int getPUERTO_2() {
        return PUERTO_2;
    }

    public static void setPUERTO_2(int PUERTO_2) {
        Cliente.PUERTO_2 = PUERTO_2;
    }

    
    
    
    
}