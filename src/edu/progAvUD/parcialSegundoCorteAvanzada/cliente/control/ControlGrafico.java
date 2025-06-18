package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.cliente.vista.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Clase encargada del control gráfico del cliente.
 *
 * Esta clase conecta la interfaz gráfica (`VentanaPrincipal`) con las acciones
 * del usuario, gestionando eventos y comunicándose con el `ControlPrincipal`.
 * Es responsable de:
 * - Cambiar entre paneles
 * - Validar acciones del usuario
 * - Mostrar mensajes
 * - Capturar coordenadas del juego
 *
 * Autor: Andres Felipe
 */
public class ControlGrafico implements ActionListener {

    private ControlPrincipal controlPrincipal;
    private VentanaPrincipal ventanaPrincipal;

    private int coordenadaX1, coordenadaY1;

    /**
     * Constructor que inicializa la interfaz y registra los eventos.
     *
     * @param controlPrincipal Referencia al controlador principal
     */
    public ControlGrafico(ControlPrincipal controlPrincipal) {
        this.controlPrincipal = controlPrincipal;
        this.ventanaPrincipal = new VentanaPrincipal();

        // Mostrar panel inicial por defecto
        ventanaPrincipal.mostrarPanel(ventanaPrincipal.panelInicial);

        // Registrar escuchas de botones
        ventanaPrincipal.panelInicial.jButtonPropiedadesSocket.addActionListener(this);
        ventanaPrincipal.panelLogin.jButtonInciarSesion.addActionListener(this);
        ventanaPrincipal.panelJuegoChat.jButtonEnviar.addActionListener(this);
    }

    /**
     * Maneja los eventos de los botones de la interfaz.
     *
     * @param e Evento de acción generado
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventanaPrincipal.panelInicial.jButtonPropiedadesSocket) {
            controlPrincipal.cargarDatosSocket();
            controlPrincipal.crearCliente();
            controlPrincipal.conectarAServer();
            ventanaPrincipal.mostrarPanel(ventanaPrincipal.panelLogin);
        }
        if (e.getSource() == ventanaPrincipal.panelLogin.jButtonInciarSesion) {
            String usuario = ventanaPrincipal.panelLogin.jTextFieldUsuario.getText();
            char[] contrasenaChars = ventanaPrincipal.panelLogin.jPasswordField.getPassword();
            String contrasena = new String(contrasenaChars);

            String respuesta = controlPrincipal.enviarCredencialesCliente(usuario, contrasena);

            if (respuesta.equalsIgnoreCase("logeado")) {
                ventanaPrincipal.mostrarMensajeExito("Has iniciado sesión con éxito");
                ventanaPrincipal.mostrarPanel(ventanaPrincipal.panelJuegoChat);
                bloquearEntradaTextoChatJuego();
                controlPrincipal.crearThreadCliente();
            } else if (respuesta.equalsIgnoreCase("noLogeado")) {
                ventanaPrincipal.mostrarMensajeError("Credenciales incorrectas, intente de nuevo");
            } else if (respuesta.equals("")) {
                ventanaPrincipal.mostrarMensajeError("No aplica");
            } else if (respuesta.equalsIgnoreCase("conectado")) {
                ventanaPrincipal.mostrarMensajeError("Ya se encuentra logueado dentro del sistema");
            }
        }
        if (e.getSource() == ventanaPrincipal.panelJuegoChat.jButtonEnviar) {
            pedirCoordenadasCartas();
        }
    }

    /**
     * Solicita al usuario que seleccione un archivo de propiedades para cargar configuración.
     *
     * @return Archivo seleccionado
     */
    public File pedirArchivoPropiedades() {
        return ventanaPrincipal.pedirArchivoPropiedades();
    }

    /**
     * Muestra un mensaje de error en la interfaz.
     *
     * @param mensaje Mensaje de error
     */
    public void mostrarMensajeError(String mensaje) {
        ventanaPrincipal.mostrarMensajeError(mensaje);
    }

    /**
     * Muestra un mensaje de éxito en la interfaz.
     *
     * @param mensaje Mensaje de éxito
     */
    public void mostrarMensajeExito(String mensaje) {
        ventanaPrincipal.mostrarMensajeExito(mensaje);
    }

    /**
     * Muestra un mensaje en el chat del juego.
     *
     * @param msg Mensaje a mostrar
     */
    public void mostrarMensajeChatJuego(String msg) {
        ventanaPrincipal.panelJuegoChat.mostrarMensajeChatJuego(msg);
    }

    /**
     * Deshabilita la entrada del chat del juego (spinners y botón de enviar).
     */
    public void bloquearEntradaTextoChatJuego() {
        ventanaPrincipal.panelJuegoChat.jButtonEnviar.setEnabled(false);
        ventanaPrincipal.panelJuegoChat.jSpinnerCoordenadaX.setEnabled(false);
        ventanaPrincipal.panelJuegoChat.jSpinnerCoordenadaY.setEnabled(false);
        ventanaPrincipal.panelJuegoChat.repaint();
        ventanaPrincipal.panelJuegoChat.revalidate();
    }

    /**
     * Habilita la entrada del chat del juego (spinners y botón de enviar).
     */
    public void permitirEntradaTextoChatJuego() {
        ventanaPrincipal.panelJuegoChat.jButtonEnviar.setEnabled(true);
        ventanaPrincipal.panelJuegoChat.jSpinnerCoordenadaX.setEnabled(true);
        ventanaPrincipal.panelJuegoChat.jSpinnerCoordenadaY.setEnabled(true);
        ventanaPrincipal.panelJuegoChat.repaint();
        ventanaPrincipal.panelJuegoChat.revalidate();
    }

    /**
     * Solicita las coordenadas de las cartas seleccionadas por el usuario.
     * Primero guarda la primera coordenada, luego la segunda y las envía al servidor.
     */
    public void pedirCoordenadasCartas() {
        int x = (Integer) ventanaPrincipal.panelJuegoChat.jSpinnerCoordenadaX.getValue();
        int y = (Integer) ventanaPrincipal.panelJuegoChat.jSpinnerCoordenadaY.getValue();

        if (controlPrincipal.isEsperandoPrimera()) {
            // Primera coordenada
            coordenadaX1 = x;
            coordenadaY1 = y;
            ventanaPrincipal.panelJuegoChat.mostrarMensajeChatJuego(
                    "Primera coordenada: (" + x + "," + y + ")");
            ventanaPrincipal.panelJuegoChat.jSpinnerCoordenadaX.setValue(1);
            ventanaPrincipal.panelJuegoChat.jSpinnerCoordenadaY.setValue(1);
            controlPrincipal.setEsperandoPrimera(false);
            ventanaPrincipal.panelJuegoChat.mostrarMensajeChatJuego(
                    "Ahora ingresa la segunda coordenada y presiona 'Enviar'.");
        } else {
            // Segunda coordenada
            int x2 = x;
            int y2 = y;
            ventanaPrincipal.panelJuegoChat.mostrarMensajeChatJuego(
                    "Segunda coordenada: (" + x2 + "," + y2 + ")");
            ventanaPrincipal.panelJuegoChat.jSpinnerCoordenadaX.setValue(1);
            ventanaPrincipal.panelJuegoChat.jSpinnerCoordenadaY.setValue(1);
            try {
                controlPrincipal.enviarPosicionCartas(coordenadaX1, coordenadaY1, x2, y2);
            } catch (IOException ex) {
                mostrarMensajeError("Ocurrió un error al mandar las coordenadas");
            }
            controlPrincipal.setEsperandoPrimera(true);
        }
    }
}
