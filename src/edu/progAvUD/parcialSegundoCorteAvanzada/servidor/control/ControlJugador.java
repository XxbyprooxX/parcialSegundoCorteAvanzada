package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.JugadorDAO;
import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.JugadorVO;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Controlador de jugador con validación mejorada para usuarios únicos
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
        try {
            // Validar y obtener datos faltantes
            if (nombreJugador == null || nombreJugador.isBlank()) {
                nombreJugador = obtenerDatoFaltante("nombre del jugador " + id, "nombre");
            }
            if (cedula == null || cedula.isBlank()) {
                cedula = obtenerDatoFaltante("cédula del jugador " + id, "cedula");
            }
            if (usuario == null || usuario.isBlank()) {
                usuario = obtenerDatoFaltante("usuario del jugador " + id, "usuario");
            }
            if (contrasena == null || contrasena.isBlank()) {
                contrasena = obtenerDatoFaltante("contraseña del jugador " + id, "contrasena");
            }

            // Crear el objeto con los valores ya validados
            JugadorVO jugador = new JugadorVO(nombreJugador.trim(), cedula.trim(), usuario.trim(), contrasena.trim(), id, cantidadIntentos, cantidadParejasResueltas);

            // Verificar unicidad del usuario y la cédula antes de insertar
            if (verificarUsuarioUnico(jugador.getUsuario())) {
                if (jugador.getCedula() != null && !jugador.getCedula().trim().isEmpty()) {
                    if (!verificarCedulaUnica(jugador.getCedula())) {
                        controlPrincipal.mostrarMensajeError("No se ha creado el jugador " + id + " porque la cédula '" + jugador.getCedula() + "' ya existe en la base de datos");
                        return;
                    }
                }

                // Insertar el jugador
                insertarJugador(jugador);
                controlPrincipal.mostrarMensajeExito("Jugador " + id + " creado exitosamente");
            } else {
                controlPrincipal.mostrarMensajeError("No se ha creado el jugador " + id + " porque el usuario '" + jugador.getUsuario() + "' ya existe en la base de datos");
            }

        } catch (Exception ex) {
            controlPrincipal.mostrarMensajeError("Error al crear jugador " + id + ": " + ex.getMessage());
        }
    }

    /**
     * Verifica que el usuario sea único en la base de datos
     *
     * @param usuario el usuario a verificar
     * @return true si el usuario es único, false si ya existe
     */
    private boolean verificarUsuarioUnico(String usuario) {
        try {
            return !jugadorDao.existeUsuario(usuario);
        } catch (SQLException ex) {
            controlPrincipal.mostrarMensajeError("Error al verificar usuario único: " + ex.getMessage());
            return false; // En caso de error, asumir que no es único para evitar duplicados
        }
    }

    /**
     * Verifica que la cédula sea única en la base de datos
     *
     * @param cedula la cédula a verificar
     * @return true si la cédula es única, false si ya existe
     */
    private boolean verificarCedulaUnica(String cedula) {
        try {
            return !jugadorDao.existeCedula(cedula);
        } catch (SQLException ex) {
            controlPrincipal.mostrarMensajeError("Error al verificar cédula única: " + ex.getMessage());
            return false; // En caso de error, asumir que no es única para evitar duplicados
        }
    }

    /**
     * Pide la lista completa de los jugadores
     *
     * @return la lista con los jugadores que están en la dataBase
     */
    public ArrayList<JugadorVO> darListaJugadores() {
        try {
            return jugadorDao.darListaJugadores();
        } catch (SQLException ex) {
            controlPrincipal.mostrarMensajeError("Error SQL al obtener lista de jugadores: " + ex.getMessage());
            return new ArrayList<>(); // Retornar lista vacía en lugar de null
        }
    }

    /**
     * Se encarga de consultar la cantidad de filas o Jugadores que hay en la
     * base de datos
     *
     * @return el valor total de Jugadores o -1 en caso de error
     */
    public int consultarCantidadJugadores() {
        try {
            return jugadorDao.consultarCantidadJugadores();
        } catch (SQLException ex) {
            controlPrincipal.mostrarMensajeError("Error SQL al consultar cantidad de jugadores: " + ex.getMessage());
            return -1;
        }
    }

    /**
     * Inserta un nuevo jugador en la base de datos.
     *
     * @param jugador objeto jugadorVO con los datos a insertar.
     */
    public void insertarJugador(JugadorVO jugador) {
        try {
            jugadorDao.insertarJugador(jugador);
        } catch (SQLException ex) {
            // Si es un error de usuario duplicado, mostrar mensaje específico
            if (ex.getMessage().contains("ya existe")) {
                controlPrincipal.mostrarMensajeError(ex.getMessage());
            } else {
                controlPrincipal.mostrarMensajeError("Error SQL al insertar jugador: " + ex.getMessage());
            }
        }
    }

    /**
     * Solicita al usuario un dato faltante y valida si es numérico según el
     * tipo. Reintenta en caso de error y valida unicidad para usuario.
     *
     * @param mensaje texto para solicitar el dato.
     * @param tipo indica si debe parsear a long ("cedula") o validar otros
     * tipos;
     * @return cadena con el valor ingresado ya validado.
     */
    private String obtenerDatoFaltante(String mensaje, String tipo) {
        String dato = controlPrincipal.mostrarJOptionEscribirDatoFaltante(mensaje);

        if (dato == null || dato.trim().isBlank()) {
            controlPrincipal.mostrarMensajeError("No se ha escrito nada en el campo de " + tipo);
            return obtenerDatoFaltante(mensaje, tipo);
        }

        if (tipo.equalsIgnoreCase("cedula")) {
            try {
                long cedulaNum = Long.parseLong(dato.trim());
                if (cedulaNum <= 0) {
                    controlPrincipal.mostrarMensajeError("La cédula debe ser un número positivo");
                    return obtenerDatoFaltante(mensaje, tipo);
                }

                // Verificar que la cédula sea única
                if (!verificarCedulaUnica(dato.trim())) {
                    controlPrincipal.mostrarMensajeError("La cédula '" + dato.trim() + "' ya existe. Ingrese una cédula diferente.");
                    return obtenerDatoFaltante(mensaje, tipo);
                }
            } catch (NumberFormatException e) {
                controlPrincipal.mostrarMensajeError("Se ha escrito algo incorrecto en el campo de " + tipo + ". Debe ser un número entero positivo.");
                return obtenerDatoFaltante(mensaje, tipo);
            }
        } else if (tipo.equalsIgnoreCase("usuario")) {
            // Validar longitud del usuario
            if (dato.trim().length() > 20) {
                controlPrincipal.mostrarMensajeError("El usuario no puede tener más de 20 caracteres.");
                return obtenerDatoFaltante(mensaje, tipo);
            }

            // Verificar que el usuario sea único
            if (!verificarUsuarioUnico(dato.trim())) {
                controlPrincipal.mostrarMensajeError("El usuario '" + dato.trim() + "' ya existe. Ingrese un usuario diferente.");
                return obtenerDatoFaltante(mensaje, tipo);
            }
        } else if (tipo.equalsIgnoreCase("contrasena")) {
            // Validar longitud de la contraseña
            if (dato.trim().length() > 20) {
                controlPrincipal.mostrarMensajeError("La contraseña no puede tener más de 20 caracteres.");
                return obtenerDatoFaltante(mensaje, tipo);
            }
        } else if (tipo.equalsIgnoreCase("nombre")) {
            // Validar longitud del nombre
            if (dato.trim().length() > 35) {
                controlPrincipal.mostrarMensajeError("El nombre no puede tener más de 35 caracteres.");
                return obtenerDatoFaltante(mensaje, tipo);
            }
        }

        return dato.trim();
    }

    public boolean consultarUsuarioYContrasenaExistente(String usuario, String contrasena) {
        JugadorVO jugador = new JugadorVO();
        try {
            jugador = jugadorDao.consultarUsuarioJugador(usuario, jugador);
            if (jugador.getContrasena() != null && jugador.getContrasena().equals(contrasena)) {
                return true;
            } else {
                controlPrincipal.mostrarMensajeError("La contrasena no coincide");
            }
        } catch (SQLException ex) {
            controlPrincipal.mostrarMensajeError("No existe el usuario");
        }
        return false;
    }

    /**
     * Método que obtiene un objeto JugadorVO basado en las credenciales de
     * login
     *
     * @param usuario Nombre de usuario
     * @return JugadorVO si las credenciales son válidas, null en caso contrario
     */
    public String obtenerJugadorPorCredenciales(String usuario) {
        JugadorVO jugador = new JugadorVO();
        try {
            jugador = jugadorDao.consultarUsuarioJugador(usuario, jugador);
            if (jugador.getNombreJugador() != null) {
                return jugador.getNombreJugador() + "," + jugador.getCedula() + "," + jugador.getUsuario() + "," + jugador.getContrasena();
            }
        } catch (SQLException ex) {
            controlPrincipal.mostrarMensajeError("No existe el usuario");
        }
        return "Error";
    }
}