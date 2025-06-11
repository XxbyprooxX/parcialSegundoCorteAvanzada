package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 *
 * @author Andres Felipe
 */
public class JugadorVO {

    private String nombreJugador;
    private String cedula;
    private String usuario;
    private String contrasena;
    private int id;
    private int cantidadIntentos;
    private int cantidadParejasResueltas;

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

    public JugadorVO(String nombreJugador, String cedula, String usuario, String contrasena, int id, int cantidadIntentos, int cantidadParejasResueltas) {
        this.nombreJugador = nombreJugador;
        this.cedula = cedula;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.id = id;
        this.cantidadIntentos = cantidadIntentos;
        this.cantidadParejasResueltas = cantidadParejasResueltas;
    }    

    public JugadorVO() {
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public int getCantidadIntentos() {
        return cantidadIntentos;
    }

    public void setCantidadIntentos(int cantidadIntentos) {
        this.cantidadIntentos = cantidadIntentos;
    }

    public int getCantidadParejasResueltas() {
        return cantidadParejasResueltas;
    }

    public void setCantidadParejasResueltas(int cantidadParejasResueltas) {
        this.cantidadParejasResueltas = cantidadParejasResueltas;
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

}
