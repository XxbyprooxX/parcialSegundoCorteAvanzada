package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.cliente.modelo.Cliente;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Clase encargada de manejar la lógica de conexión y comunicación del cliente
 * con el servidor.
 * 
 * Esta clase actúa como puente entre la capa de modelo (`Cliente`) y el 
 * controlador principal. Se encarga de establecer la conexión al servidor,
 * enviar credenciales, iniciar el hilo de recepción de mensajes, y gestionar
 * el envío de coordenadas del juego.
 * 
 * Autor: Andres Felipe
 */
public class ControlCliente {

    private Cliente cliente;
    private ThreadCliente threadCliente;
    private ControlPrincipal controlPrincipal;

    /**
     * Constructor que recibe el controlador principal para permitir
     * la comunicación entre capas.
     * 
     * @param controlPrincipal Referencia al controlador principal
     */
    public ControlCliente(ControlPrincipal controlPrincipal) {
        this.controlPrincipal = controlPrincipal;
    }

    /**
     * Asigna los datos de conexión (IP y puertos) al cliente.
     * 
     * @param ip Dirección IP del servidor
     * @param puerto1 Puerto para canal de comunicación 1
     * @param puerto2 Puerto para canal de comunicación 2
     */
    public void asignarDatosConexionCliente(String ip, String puerto1, String puerto2) {
        Cliente.setIP_SERVER(ip);

        try {
            int puerto1Int = Integer.parseInt(puerto1);
            int puerto2Int = Integer.parseInt(puerto2);
            Cliente.setPUERTO_1(puerto1Int);
            Cliente.setPUERTO_2(puerto2Int);
        } catch (NumberFormatException e) {
            // Error silencioso: los valores no se asignan si son inválidos
        }
    }

    /**
     * Establece la conexión al servidor utilizando los datos IP y puertos
     * previamente configurados. Si no se puede conectar, finaliza la aplicación.
     */
    public void conectarAServer() {
        try {
            Socket comunicacion1 = new Socket(Cliente.getIP_SERVER(), Cliente.getPUERTO_1());
            Socket comunicacion2 = new Socket(Cliente.getIP_SERVER(), Cliente.getPUERTO_2());

            cliente.setComunication(comunicacion1);
            cliente.setComunication2(comunicacion2);
            cliente.setEntrada(new DataInputStream(comunicacion1.getInputStream()));
            cliente.setSalida(new DataOutputStream(comunicacion1.getOutputStream()));
            cliente.setEntrada2(new DataInputStream(comunicacion2.getInputStream()));

            controlPrincipal.mostrarMensajeExito("Se ha hecho la conexión al servidor");
        } catch (IOException e) {
            controlPrincipal.mostrarMensajeError("No se ha podido establecer una conexión con el servidor. Inténtelo de nuevo");
            System.exit(0);
        }
    }

    /**
     * Envía las credenciales del usuario al servidor para autenticar el inicio de sesión.
     * 
     * @param usuario Nombre de usuario
     * @param contrasena Contraseña
     * @return Una cadena indicando el estado: "logeado", "noLogeado", "conectado"
     */
    public String enviarCredencialesCliente(String usuario, String contrasena) {
        String respuesta = "";
        try {
            cliente.getSalida().writeUTF("login," + usuario + "," + contrasena);
            String estado = cliente.getEntrada().readUTF();

            if (estado.equalsIgnoreCase("valido")) {
                respuesta = "logeado";
            } else if (estado.equalsIgnoreCase("invalido")) {
                respuesta = "noLogeado";
            } else if (estado.equalsIgnoreCase("yaConectado")) {
                respuesta = "conectado";
            }

        } catch (IOException ex) {
            controlPrincipal.mostrarMensajeError("Ocurrió algún error al mandar credenciales");
        }
        return respuesta;
    }

    /**
     * Crea una nueva instancia del objeto Cliente.
     */
    public void crearCliente() {
        this.cliente = new Cliente();
    }

    /**
     * Crea y lanza el hilo encargado de recibir mensajes del servidor.
     */
    public void crearThreadCliente() {
        this.threadCliente = new ThreadCliente(cliente.getEntrada(), cliente.getSalida(), this);
        threadCliente.start();
    }

    /**
     * Muestra un mensaje de error en la interfaz de usuario.
     * 
     * @param mensaje Contenido del mensaje de error
     */
    public void mostrarMensajeError(String mensaje) {
        controlPrincipal.mostrarMensajeError(mensaje);
    }

    /**
     * Muestra un mensaje en el área de chat del juego.
     * 
     * @param msg Mensaje a mostrar
     */
    public void mostrarMensajeChatJuego(String msg) {
        controlPrincipal.mostrarMensajeChatJuego(msg);
    }

    /**
     * Bloquea los controles de entrada del chat y coordenadas en la interfaz de juego.
     */
    public void bloquearEntradaTextoChatJuego() {
        controlPrincipal.bloquearEntradaTextoChatJuego();
    }

    /**
     * Habilita los controles de entrada del chat y coordenadas en la interfaz de juego.
     */
    public void permitirEntradaTextoChatJuego() {
        controlPrincipal.permitirEntradaTextoChatJuego();
    }

    /**
     * Envía las coordenadas de dos cartas seleccionadas por el jugador al servidor.
     * 
     * @param x1 Coordenada X de la primera carta
     * @param y1 Coordenada Y de la primera carta
     * @param x2 Coordenada X de la segunda carta
     * @param y2 Coordenada Y de la segunda carta
     * @throws IOException Si ocurre un error en el envío
     */
    public void enviarPosicionCartas(int x1, int y1, int x2, int y2) throws IOException {
        threadCliente.enviarPosicionCartas(x1, y1, x2, y2);
    }

    /**
     * Retorna si se está esperando la primera coordenada del par.
     * 
     * @return true si se espera la primera coordenada, false en caso contrario
     */
    public boolean isEsperandoPrimera() {
        return controlPrincipal.isEsperandoPrimera();
    }

    /**
     * Define si se debe esperar la primera coordenada de un nuevo turno.
     * 
     * @param esperandoPrimera true para indicar que se espera la primera coordenada
     */
    public void setEsperandoPrimera(boolean esperandoPrimera) {
        controlPrincipal.setEsperandoPrimera(esperandoPrimera);
    }
}