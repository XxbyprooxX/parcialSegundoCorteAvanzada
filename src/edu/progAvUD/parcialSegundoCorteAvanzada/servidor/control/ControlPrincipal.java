package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.ConexionBD;
import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.ConexionPropiedades;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

/**
 * Controlador principal corregido con mejor validación de datos
 *
 * @author Andres Felipe
 */
public class ControlPrincipal {

    private ControlGrafico controlGrafico;
    private ControlJugador controlJugador;
    private ControlServidor controlServidor;
    private int[][] matrizCartas;

    public ControlPrincipal() {
        this.controlGrafico = new ControlGrafico(this);
        this.controlJugador = new ControlJugador(this);
        this.controlServidor = new ControlServidor(this);
        matrizCartas = new int[5][8];
    }

    /**
     * Este método se encarga de crear la conexión con las propiedades
     *
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
            if (URLBD.isBlank() || usuario.isBlank()) {
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
     * Este método se encarga de cargar las propiedades de los puertos de los
     * sockets
     */
    public void cargarPropiedadesSockets() {
        ConexionPropiedades conexionPropiedades = crearConexionPropiedades();
        try {
            Properties propiedadesPuertos = conexionPropiedades.cargarPropiedades();
            String Puerto1 = propiedadesPuertos.getProperty("PUERTO_1");
            String Puerto2 = propiedadesPuertos.getProperty("PUERTO_2");

            // Validar que las propiedades no sean nulas
            if (Puerto1.isBlank() || Puerto2.isBlank()) {
                controlGrafico.mostrarMensajeError("Los puertos son obligatorios para la ejecucion");
            }
            controlServidor.asignarIps(Puerto1, Puerto2);
        } catch (IOException ex) {
            controlGrafico.mostrarMensajeError("No se pudo cargar el archivo propiedades de los puertos: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            controlGrafico.mostrarMensajeError("Error en propiedades de los puertos: " + ex.getMessage());
        }
    }

    /**
     * Carga los datos de los jugadores desde las propiedades con validación
     * mejorada
     */
    public void cargarDatosJugadoresPropiedades() {
        ConexionPropiedades conexionPropiedades = crearConexionPropiedades();
        boolean flag = true;
        do {
            try {
                Properties propiedadesJugadores = conexionPropiedades.cargarPropiedades();
                String cantidadStr = propiedadesJugadores.getProperty("cantidadJugadoresARegistrar");

                if (cantidadStr.isBlank() || cantidadStr.trim().isBlank()) {
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
                    if (!cedula.isBlank()) {
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
     *
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
    
    
    public void asignarOrdenMatrizCartas(){
        ArrayList<Integer> numeros = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            numeros.add(i);
            numeros.add(i);
        }
        
        Collections.shuffle(numeros);
        
        // Rellenar la matriz con los números mezclados
        int index = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 8; j++) {
                this.matrizCartas[i][j] = numeros.get(index++);
            }
        }
        
        // Imprimir la matriz
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(matrizCartas[i][j] + "\t");
            }
            System.out.println();
        }
    }
    

    /**
     * Manda a mostrar las opciones a elegir por el usuario
     *
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
     *
     * @param mensaje Mensaje que se quiere mostrar.
     */
    public void mostrarMensajeConsolaServidor(String mensaje) {
        controlGrafico.mostrarMensajeConsolaServidor(mensaje);
    }

    public void empezarServer() {
        new Thread(() -> controlServidor.runServer()).start();
    }

    public boolean buscarUsuarioYContrasenaExistente(String usuario, String contrasena) {
        return controlJugador.consultarUsuarioYContrasenaExistente(usuario, contrasena);
    }
    
    public String obtenerJugadorPorCredenciales(String usuario) {
        return controlJugador.obtenerJugadorPorCredenciales(usuario);
    }
    
    public void ocultarBotonIniciarJuego(boolean estado){
        controlGrafico.ocultarBotonIniciarJuego(estado);
    }

    public int[][] getMatrizCartas() {
        return matrizCartas;
    }  
    
}