package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.ConexionBD;
import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.ConexionPropiedades;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

/**
 * Controlador principal corregido con mejor validación de datos.
 * Esta clase es el punto central del control del servidor, coordinando las interacciones
 * entre la interfaz gráfica (ControlGrafico), la lógica de negocio de los jugadores (ControlJugador)
 * y la gestión del servidor de red (ControlServidor).
 *
 * @author Andres Felipe
 */
public class ControlPrincipal {

    private ControlGrafico controlGrafico;
    private ControlJugador controlJugador;
    private ControlServidor controlServidor;
    private int[][] matrizCartas;

    /**
     * Constructor de la clase ControlPrincipal.
     * Inicializa las instancias de los controladores gráfico, de jugador y del servidor,
     * y también la matriz que almacenará el orden de las cartas del juego.
     */
    public ControlPrincipal() {
        this.controlGrafico = new ControlGrafico(this);
        this.controlJugador = new ControlJugador(this);
        this.controlServidor = new ControlServidor(this);
        matrizCartas = new int[5][8];
    }

    /**
     * Este método se encarga de crear una conexión para cargar propiedades desde un archivo.
     * Solicita al usuario la selección de un archivo de propiedades a través del control gráfico
     * y maneja posibles errores durante la creación de la conexión. Reintenta la operación si falla.
     *
     * @return Un objeto `ConexionPropiedades` que representa la conexión al archivo de propiedades.
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
     * Este método se encarga de cargar las propiedades de la base de datos desde un archivo.
     * Lee la URL de la base de datos, el usuario y la contraseña del archivo de propiedades
     * y los asigna a la clase `ConexionBD` para establecer la conexión. Realiza validaciones básicas
     * para asegurar que las propiedades esenciales no estén vacías.
     */
    public void cargarDatosBD() {
        ConexionPropiedades conexionPropiedades = crearConexionPropiedades();
        try {
            Properties propiedadesBD = conexionPropiedades.cargarPropiedades();
            String URLBD = propiedadesBD.getProperty("URLBD");
            String usuario = propiedadesBD.getProperty("usuario");
            String contrasena = propiedadesBD.getProperty("contrasena");

            // Validar que las propiedades obligatorias no sean nulas o vacías
            if (URLBD == null || URLBD.isBlank() || usuario == null || usuario.isBlank()) {
                controlGrafico.mostrarMensajeError("URLBD y usuario son obligatorios en el archivo de propiedades de la Base de Datos.");
                return; // Termina la ejecución si las propiedades obligatorias faltan
            }

            ConexionBD.setURLBD(URLBD);
            ConexionBD.setUsuario(usuario);
            // Permite que la contraseña sea vacía si no está presente o es nula
            ConexionBD.setContrasena(contrasena != null ? contrasena : ""); 

        } catch (IOException ex) {
            controlGrafico.mostrarMensajeError("No se pudo cargar el archivo de propiedades de la Base de Datos: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            controlGrafico.mostrarMensajeError("Error en las propiedades de la Base de Datos: " + ex.getMessage());
        }
    }

    /**
     * Este método se encarga de cargar las propiedades de los puertos de los sockets desde un archivo.
     * Lee los valores de `PUERTO_1` y `PUERTO_2` del archivo de propiedades y los asigna
     * al control del servidor para su configuración.
     */
    public void cargarPropiedadesSockets() {
        ConexionPropiedades conexionPropiedades = crearConexionPropiedades();
        try {
            Properties propiedadesPuertos = conexionPropiedades.cargarPropiedades();
            String Puerto1 = propiedadesPuertos.getProperty("PUERTO_1");
            String Puerto2 = propiedadesPuertos.getProperty("PUERTO_2");

            // Validar que las propiedades de los puertos no sean nulas o vacías
            if (Puerto1 == null || Puerto1.isBlank() || Puerto2 == null || Puerto2.isBlank()) {
                controlGrafico.mostrarMensajeError("Los puertos son obligatorios para la ejecución del servidor.");
                return; // Termina la ejecución si las propiedades obligatorias faltan
            }
            controlServidor.asignarIps(Puerto1, Puerto2);
        } catch (IOException ex) {
            controlGrafico.mostrarMensajeError("No se pudo cargar el archivo de propiedades de los puertos: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            controlGrafico.mostrarMensajeError("Error en las propiedades de los puertos: " + ex.getMessage());
        }
    }

    /**
     * Carga los datos de los jugadores desde un archivo de propiedades con validación mejorada.
     * Lee la cantidad de jugadores a registrar y luego itera para obtener los datos de cada jugador.
     * Realiza validaciones de formato para la cédula y otros campos antes de intentar crear cada jugador.
     * En caso de errores, muestra mensajes al usuario y puede terminar la aplicación si el error es crítico.
     */
    public void cargarDatosJugadoresPropiedades() {
        ConexionPropiedades conexionPropiedades = crearConexionPropiedades();
        boolean flag = true;
        do {
            try {
                Properties propiedadesJugadores = conexionPropiedades.cargarPropiedades();
                String cantidadStr = propiedadesJugadores.getProperty("cantidadJugadoresARegistrar");

                if (cantidadStr == null || cantidadStr.trim().isBlank()) {
                    controlGrafico.mostrarMensajeError("La propiedad 'cantidadJugadoresARegistrar' no está definida en el archivo de propiedades de jugadores.");
                    System.exit(0); // Sale de la aplicación si la propiedad esencial no está definida
                }

                int cantidadDeJugadoresRegistrar = Integer.parseInt(cantidadStr.trim());

                if (cantidadDeJugadoresRegistrar <= 0) {
                    controlGrafico.mostrarMensajeError("La cantidad de jugadores a registrar debe ser un número positivo mayor que 0.");
                    System.exit(0); // Sale de la aplicación si la cantidad es inválida
                }

                for (int i = 1; i <= cantidadDeJugadoresRegistrar; i++) {
                    // Obtener propiedades del jugador, utilizando una cadena vacía como valor por defecto si no existen
                    String nombreJugador = propiedadesJugadores.getProperty("jugador" + i + ".nombreJugador", "").trim();
                    String cedula = propiedadesJugadores.getProperty("jugador" + i + ".cedula", "").trim();
                    String usuario = propiedadesJugadores.getProperty("jugador" + i + ".usuario", "").trim();
                    String contrasena = propiedadesJugadores.getProperty("jugador" + i + ".contrasena", "").trim();

                    // Validar cédula si no está en blanco
                    if (!cedula.isBlank()) {
                        if (!validarCedula(cedula)) {
                            controlGrafico.mostrarMensajeError("La cédula del jugador " + i + " debe ser un número entero válido: '" + cedula + "'.");
                            // Considera si debes salir o continuar con el siguiente jugador.
                            // Por ahora, se mantiene el comportamiento original de solo mostrar error.
                        }
                    }

                    // Crear el jugador
                    controlJugador.crearJugador(nombreJugador, cedula, usuario, contrasena, i, 0, 0);
                }

                flag = false;
                controlGrafico.mostrarMensajeExito("Se han creado correctamente los jugadores a partir de las propiedades.");

            } catch (IOException ex) {
                controlGrafico.mostrarMensajeError("No se pudo cargar el archivo de propiedades de los jugadores: " + ex.getMessage());
                System.exit(0); // Salida crítica en caso de que el archivo no se pueda cargar
            } catch (NumberFormatException ex) {
                controlGrafico.mostrarMensajeError("Error en el formato numérico de la cantidad de jugadores a registrar: " + ex.getMessage());
                System.exit(0); // Salida crítica en caso de error de formato numérico
            } catch (IllegalArgumentException ex) {
                controlGrafico.mostrarMensajeError("Error en las propiedades de los jugadores: " + ex.getMessage());
                System.exit(0); // Salida crítica para otros errores de propiedades
            } catch (Exception ex) {
                controlGrafico.mostrarMensajeError("Error inesperado al cargar datos de jugadores: " + ex.getMessage());
                System.exit(0); // Salida crítica para errores no capturados
            }
        } while (flag);
    }

    /**
     * Valida que una cédula sea un número entero válido y positivo.
     *
     * @param cedula La cadena que representa la cédula a validar.
     * @return `true` si la cédula es un número entero positivo válido, `false` en caso contrario.
     */
    private boolean validarCedula(String cedula) {
        try {
            long cedulaNum = Long.parseLong(cedula);
            return cedulaNum > 0; // La cédula debe ser un número positivo
        } catch (NumberFormatException e) {
            return false; // No es un número válido
        }
    }
    
    /**
     * Asigna un orden aleatorio a las cartas del juego y las almacena en la matriz `matrizCartas`.
     * Se generan 20 pares de números (del 1 al 20), se mezclan aleatoriamente y luego se distribuyen
     * en una matriz de 5 filas por 8 columnas. La matriz se imprime en la consola para depuración.
     */
    public void asignarOrdenMatrizCartas(){
        ArrayList<Integer> numeros = new ArrayList<>();
        // Crea 20 pares de números (del 1 al 20)
        for (int i = 1; i <= 20; i++) {
            numeros.add(i);
            numeros.add(i);
        }
        
        Collections.shuffle(numeros); // Mezcla aleatoriamente los números
        
        // Rellena la matriz con los números mezclados
        int index = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 8; j++) {
                this.matrizCartas[i][j] = numeros.get(index++);
            }
        }
        
        // Imprime la matriz en la consola (para depuración)
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(matrizCartas[i][j] + "\t");
            }
            System.out.println();
        }
    }
    

    /**
     * Solicita al usuario un dato faltante a través del control gráfico.
     *
     * @param datoFaltante El mensaje o descripción del dato que se le pide al usuario.
     * @return El valor (cadena de texto) ingresado por el usuario.
     */
    public String mostrarJOptionEscribirDatoFaltante(String datoFaltante) {
        return controlGrafico.mostrarJOptionEscribirDatoFaltante(datoFaltante);
    }

    /**
     * Muestra un mensaje de error en la interfaz gráfica del usuario.
     *
     * @param mensaje El mensaje de error a mostrar.
     */
    public void mostrarMensajeError(String mensaje) {
        controlGrafico.mostrarMensajeError(mensaje);
    }

    /**
     * Muestra un mensaje de éxito en la interfaz gráfica del usuario.
     *
     * @param mensaje El mensaje de éxito a mostrar.
     */
    public void mostrarMensajeExito(String mensaje) {
        controlGrafico.mostrarMensajeExito(mensaje);
    }

    /**
     * Envía un mensaje a la consola del servidor, que se visualiza en la interfaz gráfica.
     *
     * @param mensaje El mensaje de texto que se desea mostrar en la consola del servidor.
     */
    public void mostrarMensajeConsolaServidor(String mensaje) {
        controlGrafico.mostrarMensajeConsolaServidor(mensaje);
    }

    /**
     * Inicia el servidor en un nuevo hilo de ejecución.
     * Esto permite que el servidor funcione en segundo plano sin bloquear la interfaz de usuario.
     */
    public void empezarServer() {
        new Thread(() -> controlServidor.runServer()).start();
    }

    /**
     * Busca si un usuario y contraseña específicos existen y coinciden en la base de datos.
     * Delega esta operación al `ControlJugador`.
     *
     * @param usuario El nombre de usuario a buscar.
     * @param contrasena La contraseña a verificar.
     * @return `true` si el usuario y la contraseña coinciden, `false` en caso contrario.
     */
    public boolean buscarUsuarioYContrasenaExistente(String usuario, String contrasena) {
        return controlJugador.consultarUsuarioYContrasenaExistente(usuario, contrasena);
    }
    
    /**
     * Obtiene la información completa de un jugador a partir de su nombre de usuario.
     * Delega esta operación al `ControlJugador`.
     *
     * @param usuario El nombre de usuario del jugador cuya información se desea obtener.
     * @return Una cadena con la información del jugador si se encuentra, o un mensaje de error si no.
     */
    public String obtenerJugadorPorCredenciales(String usuario) {
        return controlJugador.obtenerJugadorPorCredenciales(usuario);
    }
    
    /**
     * Controla la visibilidad del botón de inicio de juego en la interfaz gráfica.
     *
     * @param estado Un valor booleano: `true` para hacer visible el botón, `false` para ocultarlo.
     */
    public void ocultarBotonIniciarJuego(boolean estado){
        controlGrafico.ocultarBotonIniciarJuego(estado);
    }

    /**
     * Obtiene la matriz de cartas actual que define el orden de las imágenes en el juego.
     *
     * @return Una matriz bidimensional de enteros que representa la disposición de las cartas.
     */
    public int[][] getMatrizCartas() {
        return matrizCartas;
    }   
    
    /**
     * Inicia la lógica principal del juego a través del `ControlServidor`.
     * Este método es el punto de partida para las operaciones de juego después de la configuración inicial.
     */
    public void iniciarJuego(){
        controlServidor.iniciarJuego();
    }
    
    /**
     * Selecciona visualmente una carta en la interfaz gráfica del juego.
     *
     * @param idCarta El identificador único de la carta que se desea seleccionar.
     */
    public void seleccionarCarta(int idCarta){
        controlGrafico.seleccionarCarta(idCarta);
    }
    
    /**
     * Deselecciona visualmente una carta en la interfaz gráfica del juego.
     *
     * @param idCarta El identificador único de la carta que se desea deseleccionar.
     */
    public void deseleccionarCarta(int idCarta){
        controlGrafico.deseleccionarCarta(idCarta);
    }
    
    /**
     * Actualiza la información mostrada en el panel de estadísticas del juego.
     *
     * @param numeroIntentos La cadena que representa el número total de intentos realizados.
     * @param numeroParejas La cadena que representa el número de parejas de cartas resueltas.
     * @param nombreUsuario La cadena que representa el nombre del jugador actual.
     */
    public void actualizarPanelEstadisticas(String numeroIntentos, String numeroParejas, String nombreUsuario){
        controlGrafico.actualizarPanelEstadisticas(numeroIntentos, numeroParejas, nombreUsuario);
    }
}