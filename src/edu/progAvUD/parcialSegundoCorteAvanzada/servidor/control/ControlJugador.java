package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.JugadorDAO;
import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.JugadorVO;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Controlador de jugador con validación mejorada para usuarios únicos.
 * Esta clase se encarga de la lógica de negocio relacionada con la gestión de jugadores,
 * incluyendo la creación, validación de unicidad de usuario y cédula, y la consulta de datos de jugadores.
 *
 * @author Cristianlol789
 */
public class ControlJugador {

    private ControlPrincipal controlPrincipal;
    private JugadorDAO jugadorDao;

    /**
     * Construye una nueva instancia de ControlJugador.
     * Inicializa las dependencias con el controlador principal y el objeto de acceso a datos de jugador.
     *
     * @param controlPrincipal La instancia del controlador principal de la aplicación.
     */
    public ControlJugador(ControlPrincipal controlPrincipal) {
        this.controlPrincipal = controlPrincipal;
        this.jugadorDao = new JugadorDAO();
    }

    /**
     * Crea un nuevo jugador en el sistema.
     * Este método valida los datos de entrada, solicita la información faltante al usuario
     * si es necesario, y verifica la unicidad del usuario y la cédula antes de intentar insertar
     * el jugador en la base de datos.
     *
     * @param nombreJugador El nombre completo del jugador.
     * @param cedula La cédula de identidad del jugador.
     * @param usuario El nombre de usuario único para el jugador.
     * @param contrasena La contraseña del jugador.
     * @param id El identificador único del jugador.
     * @param cantidadIntentos La cantidad de intentos que ha realizado el jugador.
     * @param cantidadParejasResueltas La cantidad de parejas de cartas que ha resuelto el jugador.
     */
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
     * Verifica que el nombre de usuario sea único en la base de datos.
     *
     * @param usuario El nombre de usuario a verificar.
     * @return `true` si el usuario es único (no existe en la base de datos), `false` si ya existe o si ocurre un error.
     */
    private boolean verificarUsuarioUnico(String usuario) {
        try {
            return !jugadorDao.existeUsuario(usuario);
        } catch (SQLException ex) {
            controlPrincipal.mostrarMensajeError("Error al verificar usuario único: " + ex.getMessage());
            return false; // En caso de error, asume que no es único para evitar duplicados.
        }
    }

    /**
     * Verifica que la cédula sea única en la base de datos.
     *
     * @param cedula La cédula a verificar.
     * @return `true` si la cédula es única (no existe en la base de datos), `false` si ya existe o si ocurre un error.
     */
    private boolean verificarCedulaUnica(String cedula) {
        try {
            return !jugadorDao.existeCedula(cedula);
        } catch (SQLException ex) {
            controlPrincipal.mostrarMensajeError("Error al verificar cédula única: " + ex.getMessage());
            return false; // En caso de error, asume que no es única para evitar duplicados.
        }
    }

    /**
     * Obtiene la lista completa de todos los jugadores registrados en la base de datos.
     *
     * @return Una `ArrayList` de objetos `JugadorVO` que representan a los jugadores. Retorna una lista vacía si ocurre un error.
     */
    public ArrayList<JugadorVO> darListaJugadores() {
        try {
            return jugadorDao.darListaJugadores();
        } catch (SQLException ex) {
            controlPrincipal.mostrarMensajeError("Error SQL al obtener lista de jugadores: " + ex.getMessage());
            return new ArrayList<>(); // Retorna una lista vacía en lugar de null.
        }
    }

    /**
     * Consulta la cantidad total de jugadores almacenados en la base de datos.
     *
     * @return El número total de jugadores, o -1 en caso de error durante la consulta.
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
     * Muestra un mensaje de error si la inserción falla, especialmente si es debido a un usuario duplicado.
     *
     * @param jugador El objeto `JugadorVO` que contiene los datos del jugador a insertar.
     */
    public void insertarJugador(JugadorVO jugador) {
        try {
            jugadorDao.insertarJugador(jugador);
        } catch (SQLException ex) {
            // Si es un error de usuario duplicado, muestra un mensaje específico.
            if (ex.getMessage().contains("ya existe")) {
                controlPrincipal.mostrarMensajeError(ex.getMessage());
            } else {
                controlPrincipal.mostrarMensajeError("Error SQL al insertar jugador: " + ex.getMessage());
            }
        }
    }

    /**
     * Solicita al usuario un dato faltante y valida la entrada según el tipo de dato.
     * Reintenta la solicitud en caso de entrada inválida y valida la unicidad para los campos de usuario y cédula.
     *
     * @param mensaje El texto que se mostrará al usuario para solicitar el dato.
     * @param tipo Indica el tipo de dato esperado (ej. "cedula", "usuario", "contrasena", "nombre") para aplicar validaciones específicas.
     * @return La cadena con el valor ingresado por el usuario, ya validado y sin espacios en blanco al inicio o al final.
     */
    private String obtenerDatoFaltante(String mensaje, String tipo) {
        String dato = controlPrincipal.mostrarJOptionEscribirDatoFaltante(mensaje);

        if (dato == null || dato.trim().isBlank()) {
            controlPrincipal.mostrarMensajeError("No se ha escrito nada en el campo de " + tipo);
            return obtenerDatoFaltante(mensaje, tipo); // Reintenta la solicitud.
        }

        if (tipo.equalsIgnoreCase("cedula")) {
            try {
                long cedulaNum = Long.parseLong(dato.trim());
                if (cedulaNum <= 0) {
                    controlPrincipal.mostrarMensajeError("La cédula debe ser un número positivo");
                    return obtenerDatoFaltante(mensaje, tipo); // Reintenta la solicitud.
                }

                // Verificar que la cédula sea única
                if (!verificarCedulaUnica(dato.trim())) {
                    controlPrincipal.mostrarMensajeError("La cédula '" + dato.trim() + "' ya existe. Ingrese una cédula diferente.");
                    return obtenerDatoFaltante(mensaje, tipo); // Reintenta la solicitud.
                }
            } catch (NumberFormatException e) {
                controlPrincipal.mostrarMensajeError("Se ha escrito algo incorrecto en el campo de " + tipo + ". Debe ser un número entero positivo.");
                return obtenerDatoFaltante(mensaje, tipo); // Reintenta la solicitud.
            }
        } else if (tipo.equalsIgnoreCase("usuario")) {
            // Validar longitud del usuario
            if (dato.trim().length() > 20) {
                controlPrincipal.mostrarMensajeError("El usuario no puede tener más de 20 caracteres.");
                return obtenerDatoFaltante(mensaje, tipo); // Reintenta la solicitud.
            }

            // Verificar que el usuario sea único
            if (!verificarUsuarioUnico(dato.trim())) {
                controlPrincipal.mostrarMensajeError("El usuario '" + dato.trim() + "' ya existe. Ingrese un usuario diferente.");
                return obtenerDatoFaltante(mensaje, tipo); // Reintenta la solicitud.
            }
        } else if (tipo.equalsIgnoreCase("contrasena")) {
            // Validar longitud de la contraseña
            if (dato.trim().length() > 20) {
                controlPrincipal.mostrarMensajeError("La contraseña no puede tener más de 20 caracteres.");
                return obtenerDatoFaltante(mensaje, tipo); // Reintenta la solicitud.
            }
        } else if (tipo.equalsIgnoreCase("nombre")) {
            // Validar longitud del nombre
            if (dato.trim().length() > 35) {
                controlPrincipal.mostrarMensajeError("El nombre no puede tener más de 35 caracteres.");
                return obtenerDatoFaltante(mensaje, tipo); // Reintenta la solicitud.
            }
        }

        return dato.trim();
    }

    /**
     * Consulta si un usuario y contraseña específicos existen y coinciden en la base de datos.
     *
     * @param usuario El nombre de usuario a verificar.
     * @param contrasena La contraseña a verificar.
     * @return `true` si el usuario y la contraseña coinciden, `false` en caso contrario o si el usuario no existe.
     */
    public boolean consultarUsuarioYContrasenaExistente(String usuario, String contrasena) {
        JugadorVO jugador = new JugadorVO();
        try {
            jugador = jugadorDao.consultarUsuarioJugador(usuario, jugador);
            if (jugador.getContrasena() != null && jugador.getContrasena().equals(contrasena)) {
                return true;
            } else {
                controlPrincipal.mostrarMensajeError("La contraseña no coincide");
            }
        } catch (SQLException ex) {
            controlPrincipal.mostrarMensajeError("No existe el usuario");
        }
        return false;
    }

    /**
     * Obtiene un objeto `JugadorVO` basado en el nombre de usuario proporcionado.
     * Este método se utiliza para recuperar los datos de un jugador después de una autenticación exitosa.
     *
     * @param usuario El nombre de usuario del jugador.
     * @return Una cadena formateada con los datos del jugador (nombre, cédula, usuario, contraseña)
     * separados por comas si se encuentra el jugador; de lo contrario, "Error".
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