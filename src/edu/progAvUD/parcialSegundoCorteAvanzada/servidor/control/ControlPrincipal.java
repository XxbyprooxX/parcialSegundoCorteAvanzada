package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.ConexionBD;
import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.ConexionPropiedades;
import java.io.IOException;
import java.util.Properties;

/**
 * Controlador principal corregido con mejor validación de datos
 * @author Andres Felipe
 */
public class ControlPrincipal {

    private ControlGrafico controlGrafico;
    private ControlJugador controlJugador;

    public ControlPrincipal() {
        this.controlGrafico = new ControlGrafico(this);
        this.controlJugador = new ControlJugador(this);
    }

    /**
     * Este método se encarga de crear la conexión con las propiedades
     * @return la conexión
     */
    public ConexionPropiedades crearConexionPropiedades() {
        ConexionPropiedades conexionPropiedades = null;
        boolean flag = true;
        do {
            try {
                conexionPropiedades = new ConexionPropiedades(controlGrafico.pedirArchivoPropiedades());
                if (conexionPropiedades != null) {
                    flag = false;
                }
            } catch (Exception ex) {
                controlGrafico.mostrarMensajeError("No se pudo crear la conexión correctamente: " + ex.getMessage());
            }
        } while (flag);

        return conexionPropiedades;
    }

    /**
     * Este método se encarga de cargar las propiedades de la base de datos
     */
    public void cargarDatosBD() {
        ConexionPropiedades conexionPropiedades = crearConexionPropiedades();
        try {
            Properties propiedadesBD = conexionPropiedades.cargarPropiedades();
            String URLBD = propiedadesBD.getProperty("URLBD");
            String usuario = propiedadesBD.getProperty("usuario");
            String contrasena = propiedadesBD.getProperty("contrasena");
            
            // Validar que las propiedades no sean nulas
            if (URLBD == null || usuario == null) {
                controlGrafico.mostrarMensajeError("URLBD y usuario son obligatorios en el archivo de propiedades");
            }
            
            ConexionBD.setURLBD(URLBD);
            ConexionBD.setUsuario(usuario);
            ConexionBD.setContrasena(contrasena != null ? contrasena : ""); // Permitir contraseña vacía

        } catch (IOException ex) {
            controlGrafico.mostrarMensajeError("No se pudo cargar el archivo propiedades de la Base de Datos: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            controlGrafico.mostrarMensajeError("Error en propiedades de BD: " + ex.getMessage());
        }
    }

    /**
     * Carga los datos de los jugadores desde las propiedades con validación mejorada
     */
    public void cargarDatosJugadoresPropiedades() {
        ConexionPropiedades conexionPropiedades = crearConexionPropiedades();
        boolean flag = true;
        do {
            try {
                Properties propiedadesJugadores = conexionPropiedades.cargarPropiedades();
                String cantidadStr = propiedadesJugadores.getProperty("cantidadJugadoresARegistrar");
                
                if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
                    controlGrafico.mostrarMensajeError("La propiedad 'cantidadJugadoresARegistrar' no está definida");
                }
                
                int cantidadDeJugadoresRegistrar = Integer.parseInt(cantidadStr.trim());
                
                if (cantidadDeJugadoresRegistrar <= 0) {
                    controlGrafico.mostrarMensajeError("La cantidad de jugadores debe ser mayor a 0");
                }
                
                for (int i = 1; i <= cantidadDeJugadoresRegistrar; i++) {
                    String nombreJugador = propiedadesJugadores.getProperty("jugador" + i + ".nombreJugador", "").trim();
                    String cedula = propiedadesJugadores.getProperty("jugador" + i + ".cedula", "").trim();
                    String usuario = propiedadesJugadores.getProperty("jugador" + i + ".usuario", "").trim();
                    String contrasena = propiedadesJugadores.getProperty("jugador" + i + ".contrasena", "").trim();
                    
                    // Validar cédula si no está en blanco
                    if (!cedula.isEmpty()) {
                        if (!validarCedula(cedula)) {
                            controlGrafico.mostrarMensajeError("La cédula del jugador " + i + " debe ser un número entero válido: " + cedula);
                        }
                    }
                    
                    // Crear el jugador
                    controlJugador.crearJugador(nombreJugador, cedula, usuario, contrasena, i, 0, 0);
                }
                
                flag = false;
                controlGrafico.mostrarMensajeExito("Se han creado correctamente los jugadores");
                
            } catch (IOException ex) {
                controlGrafico.mostrarMensajeError("No se pudo cargar el archivo propiedades de los jugadores: " + ex.getMessage());
                System.exit(0);
            } catch (NumberFormatException ex) {
                controlGrafico.mostrarMensajeError("Error en formato numérico: " + ex.getMessage());
                System.exit(0);
            } catch (IllegalArgumentException ex) {
                controlGrafico.mostrarMensajeError("Error en propiedades: " + ex.getMessage());
                System.exit(0);
            } catch (Exception ex) {
                controlGrafico.mostrarMensajeError("Error inesperado: " + ex.getMessage());
                System.exit(0);
            }
        } while (flag);
    }
    
    /**
     * Valida que una cédula sea un número entero válido
     * @param cedula la cédula a validar
     * @return true si es válida, false en caso contrario
     */
    private boolean validarCedula(String cedula) {
        try {
            long cedulaNum = Long.parseLong(cedula);
            return cedulaNum > 0; // La cédula debe ser un número positivo
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Manda a mostrar las opciones a elegir por el usuario
     * @param datoFaltante es el dato que está en blanco
     * @return el valor seleccionado
     */
    public String mostrarJOptionEscribirDatoFaltante(String datoFaltante) {
        return controlGrafico.mostrarJOptionEscribirDatoFaltante(datoFaltante);
    }
    
    public void mostrarMensajeError(String mensaje) {
        controlGrafico.mostrarMensajeError(mensaje);
    }
    
    public void mostrarMensajeExito(String mensaje) {
        controlGrafico.mostrarMensajeExito(mensaje);
    }
    
    /**
     * Envía un mensaje a la consola del servidor (interfaz gráfica).
     * @param mensaje Mensaje que se quiere mostrar.
     */
    public void mostrarMensajeConsolaServidor(String mensaje) {
        controlGrafico.mostrarMensajeConsolaServidor(mensaje);
    }
}