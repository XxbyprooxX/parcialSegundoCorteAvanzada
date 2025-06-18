package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.ConexionPropiedades;
import java.io.IOException;
import java.util.Properties;

/**
 * Clase principal de control del cliente.
 *
 * Esta clase actúa como intermediaria entre la interfaz gráfica (ControlGrafico)
 * y la lógica de comunicación y operación del cliente (ControlCliente).
 * Su objetivo es coordinar la inicialización de conexiones, manejo de archivos 
 * de configuración, comunicación con el servidor y control de entrada del usuario.
 * 
 * Autor: Andres Felipe
 */
public class ControlPrincipal {

    private ControlGrafico controlGrafico;
    private ControlCliente controlCliente;
    
    private boolean esperandoPrimera = true;

    /**
     * Constructor principal que inicializa los controladores gráfico y de cliente.
     */
    public ControlPrincipal() {
        this.controlGrafico = new ControlGrafico(this);
        this.controlCliente = new ControlCliente(this);
    }

    /**
     * Crea una instancia de ConexionPropiedades solicitando al usuario el archivo correspondiente.
     * 
     * @return una instancia válida de ConexionPropiedades
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
            } catch (Exception e) {
                controlGrafico.mostrarMensajeError("Ocurrió un error en el archivo de propiedades");
            }
        } while (flag);
        return conexionPropiedades;
    }

    /**
     * Carga los datos de IP y puertos del servidor desde el archivo de propiedades
     * y los asigna al cliente.
     */
    public void cargarDatosSocket() {
        ConexionPropiedades conexionPropiedades = crearConexionPropiedades();
        try {
            Properties propiedadesSocket = conexionPropiedades.cargarPropiedades();
            String ipServer = propiedadesSocket.getProperty("IP_SERVER");
            String puerto1 = propiedadesSocket.getProperty("PUERTO_1");
            String puerto2 = propiedadesSocket.getProperty("PUERTO_2");
            controlCliente.asignarDatosConexionCliente(ipServer, puerto1, puerto2);
        } catch (IOException e) {
            controlGrafico.mostrarMensajeError("No se pudo cargar el archivo propiedades de la conexión al socket");
        }
    }

    /**
     * Muestra un mensaje de error en la interfaz gráfica.
     *
     * @param mensaje Mensaje a mostrar
     */
    public void mostrarMensajeError(String mensaje) {
        controlGrafico.mostrarMensajeError(mensaje);
    }

    /**
     * Muestra un mensaje de éxito en la interfaz gráfica.
     *
     * @param mensaje Mensaje a mostrar
     */
    public void mostrarMensajeExito(String mensaje) {
        controlGrafico.mostrarMensajeExito(mensaje);
    }

    /**
     * Solicita al cliente que establezca conexión con el servidor.
     */
    public void conectarAServer() {
        controlCliente.conectarAServer();
    }

    /**
     * Inicializa la creación del cliente.
     */
    public void crearCliente() {
        controlCliente.crearCliente();
    }

    /**
     * Envía las credenciales del cliente al servidor para autenticación.
     *
     * @param usuario Nombre de usuario
     * @param contrasena Contraseña
     * @return Respuesta del servidor
     */
    public String enviarCredencialesCliente(String usuario, String contrasena) {
        return controlCliente.enviarCredencialesCliente(usuario, contrasena);
    }

    /**
     * Crea el hilo de escucha del cliente para recibir mensajes del servidor.
     */
    public void crearThreadCliente() {
        controlCliente.crearThreadCliente();
    }

    /**
     * Muestra un mensaje dentro del chat del juego en la interfaz.
     *
     * @param msg Mensaje a mostrar
     */
    public void mostrarMensajeChatJuego(String msg) {
        controlGrafico.mostrarMensajeChatJuego(msg);
    }

    /**
     * Getter para obtener el controlador gráfico.
     *
     * @return Instancia de ControlGrafico
     */
    public ControlGrafico getControlGrafico() {
        return controlGrafico;
    }

    /**
     * Setter para actualizar el controlador gráfico.
     *
     * @param controlGrafico Nuevo controlador gráfico
     */
    public void setControlGrafico(ControlGrafico controlGrafico) {
        this.controlGrafico = controlGrafico;
    }

    /**
     * Getter para obtener el controlador del cliente.
     *
     * @return Instancia de ControlCliente
     */
    public ControlCliente getControlCliente() {
        return controlCliente;
    }

    /**
     * Setter para actualizar el controlador del cliente.
     *
     * @param controlCliente Nuevo controlador de cliente
     */
    public void setControlCliente(ControlCliente controlCliente) {
        this.controlCliente = controlCliente;
    }

    /**
     * Bloquea la entrada de texto del chat del juego.
     */
    public void bloquearEntradaTextoChatJuego() {
        controlGrafico.bloquearEntradaTextoChatJuego();
    }

    /**
     * Permite la entrada de texto del chat del juego.
     */
    public void permitirEntradaTextoChatJuego() {
        controlGrafico.permitirEntradaTextoChatJuego();
    }

    /**
     * Envía las coordenadas seleccionadas por el jugador al servidor.
     *
     * @param x1 Coordenada X1
     * @param y1 Coordenada Y1
     * @param x2 Coordenada X2
     * @param y2 Coordenada Y2
     * @throws IOException En caso de error de comunicación
     */
    public void enviarPosicionCartas(int x1, int y1, int x2, int y2) throws IOException {
        controlCliente.enviarPosicionCartas(x1, y1, x2, y2);
    }

    /**
     * Indica si el sistema está esperando la primera coordenada del jugador.
     *
     * @return true si espera la primera, false si espera la segunda
     */
    public boolean isEsperandoPrimera() {
        return esperandoPrimera;
    }

    /**
     * Establece si el sistema está esperando la primera coordenada.
     *
     * @param esperandoPrimera Estado de espera
     */
    public void setEsperandoPrimera(boolean esperandoPrimera) {
        this.esperandoPrimera = esperandoPrimera;
    }

}