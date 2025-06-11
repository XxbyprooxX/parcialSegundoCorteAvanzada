package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.modelo;

/**
 *
 * @author Andres Felipe
 */
public class Jugador {
    
    private String nombreJugador;
    private int id;
    private String cedula;
    private String usuario;
    private String contrasena;
    private int cantidadIntentos;
    private int cantidadParejasResueltas;

    public Jugador(String nombreJugador, int id, String cedula, String usuario, String contrasena, int cantidadIntentos, int cantidadParejasResueltas) {
        this.nombreJugador = nombreJugador;
        this.id = id;
        this.cedula = cedula;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.cantidadIntentos = cantidadIntentos;
        this.cantidadParejasResueltas = cantidadParejasResueltas;
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

}