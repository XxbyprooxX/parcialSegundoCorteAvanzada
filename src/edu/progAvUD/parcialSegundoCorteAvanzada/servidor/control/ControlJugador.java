package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.JugadorDAO;
import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.JugadorVO;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Cristianlol789
 */
public class ControlJugador {

    private ControlPrincipal controlPrincipal;
    private JugadorDAO jugadorDao;

    public ControlJugador(ControlPrincipal controlPrincipal) {
        this.controlPrincipal = controlPrincipal;
        this.jugadorDao = new JugadorDAO();
    }

    public void crearJugador(String nombreJugador, String cedula, String usuario, String contrasena, int id, int cantidadIntentos, int cantidadParejasResueltas) {
        if (nombreJugador.isBlank()) {
            nombreJugador = obtenerDatoFaltante("nombre del jugador " + id, "nombre");
        }
        if (cedula.isBlank()) {
            cedula = obtenerDatoFaltante("cédula del jugador " + id, "cedula");
        }
        if (usuario.isBlank()) {
            usuario = obtenerDatoFaltante("usuario del jugador " + id, "usuario");
        }
        if (contrasena.isBlank()) {
            contrasena = obtenerDatoFaltante("contraseña del jugador " + id, "contrasena");
        }

        // Crear el objeto con los valores ya validados
        JugadorVO jugador = new JugadorVO(nombreJugador, cedula, usuario, contrasena, id, cantidadIntentos, cantidadParejasResueltas);

        // Verificar unicidad e insertar
        if (consultarCantidadJugadores() == 0) {
            insertarJugador(jugador);
        } else {
            if (verificarjugadorRepetido(jugador)) {
                insertarJugador(jugador);
            } else {
                controlPrincipal.mostrarMensajeError("No se ha creado el jugador " + id + " porque ya se encuentra en la Base de Datos");
            }
        }
    }

    /**
     * Pide la lista completa de los jugadores
     *
     * @return la lista con los jugadores que estan en la dataBase
     */
    public ArrayList<JugadorVO> darListaJugadores() {
        try {
            return jugadorDao.darListaJugadores();
        } catch (SQLException ex) {
            controlPrincipal.mostrarMensajeError("SQLException darListaJugadores");
        }
        return null;
    }

    /**
     * Se encarga de verificar que en la base de datos no esten repetidos los
     * mismos Jugadores
     *
     * @param jugador le llega el jugador para comprobar si esta o no repetido
     * @return un true o false para saber si esta o no repetido
     */
    public boolean verificarjugadorRepetido(JugadorVO jugador) {
        ArrayList<JugadorVO> jugadores = darListaJugadores();
        for (JugadorVO jugadorExistente : jugadores) {
            String cedula1 = jugador.getCedula() != null ? jugador.getCedula().trim() : "";
            String cedula2 = jugadorExistente.getCedula() != null ? jugadorExistente.getCedula().trim() : "";

            String usuario1 = jugador.getUsuario() != null ? jugador.getUsuario().trim().toLowerCase() : "";
            String usuario2 = jugadorExistente.getUsuario() != null ? jugadorExistente.getUsuario().trim().toLowerCase() : "";

            if (cedula1.equals(cedula2) || usuario1.equals(usuario2)) {
                return false; // Está repetido
            }
        }
        return true; // No está repetido
    }

    /**
     * Se encarga de consultar la cantidad de filas o Jugadores que hay en la
     * base de datos
     *
     * @return el valor total de Jugadores o -1 en caso de que no pueda
     * conectarse o sea 0
     */
    public int consultarCantidadJugadores() {
        try {
            return jugadorDao.consultarCantidadJugadores();
        } catch (SQLException ex) {
            controlPrincipal.mostrarMensajeError("SQLException ConsultarCantidadJugadores");
        }
        return -1;
    }

    /**
     * Inserta un nuevo jugador en la base de datos.
     *
     * @param Jugador objeto jugadorVO con los datos a insertar.
     */
    public void insertarJugador(JugadorVO Jugador) {
        try {
            jugadorDao.insertarJugador(Jugador);
        } catch (SQLException ex) {
            controlPrincipal.mostrarMensajeError("SQLException insertarJugador");
        }
    }

    /**
     * Solicita al usuario un dato faltante y valida si es numérico según el
     * tipo (peso o edad). Reintenta en caso de error.
     *
     * @param mensaje texto para solicitar el dato.
     * @param tipo indica si debe parsear a double ("peso") o int ("edad").
     * @return cadena con el valor ingresado ya validado.
     */
    private String obtenerDatoFaltante(String mensaje, String tipo) {
        String dato = controlPrincipal.mostrarJOptionEscribirDatoFaltante(mensaje);

        if (dato == null || dato.isBlank()) {
            controlPrincipal.mostrarMensajeError("No se ha escrito nada en el campo de " + tipo);
            return obtenerDatoFaltante(mensaje, tipo);
        }

        if (tipo.equalsIgnoreCase("peso")) {
            try {
                Double.parseDouble(dato.trim());
            } catch (NumberFormatException e) {
                controlPrincipal.mostrarMensajeError("Se ha escrito algo incorrecto en el campo de " + tipo + ". Debe ser un número decimal.");
                return obtenerDatoFaltante(mensaje, tipo);
            }
        } else if (tipo.equalsIgnoreCase("edad")) {
            try {
                Integer.parseInt(dato.trim());
            } catch (NumberFormatException e) {
                controlPrincipal.mostrarMensajeError("Se ha escrito algo incorrecto en el campo de " + tipo + ". Debe ser un número entero.");
                return obtenerDatoFaltante(mensaje, tipo);
            }
        }

        return dato.trim();
    }

}