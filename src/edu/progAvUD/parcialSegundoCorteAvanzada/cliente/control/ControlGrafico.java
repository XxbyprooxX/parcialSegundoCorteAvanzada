package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.cliente.vista.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Andres Felipe
 */
public class ControlGrafico implements ActionListener {

    private ControlPrincipal controlPrincipal;
    private VentanaPrincipal ventanaPrincipal;

    private int coordenadaX1, coordenadaY1;

    public ControlGrafico(ControlPrincipal controlPrincipal) {
        this.controlPrincipal = controlPrincipal;
        this.ventanaPrincipal = new VentanaPrincipal();

        ventanaPrincipal.mostrarPanel(ventanaPrincipal.panelInicial);

        ventanaPrincipal.panelInicial.jButtonPropiedadesSocket.addActionListener(this);
        ventanaPrincipal.panelLogin.jButtonInciarSesion.addActionListener(this);

        ventanaPrincipal.panelJuegoChat.jButtonEnviar.addActionListener(this);

    }

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
                ventanaPrincipal.mostrarMensajeExito("Has iniciado sesion con exito");
                ventanaPrincipal.mostrarPanel(ventanaPrincipal.panelJuegoChat);
                bloquearEntradaTextoChatJuego();
                controlPrincipal.crearThreadCliente();
            } else if (respuesta.equalsIgnoreCase("noLogeado")) {
                ventanaPrincipal.mostrarMensajeError("Credenciales incorrectas intente de nuevo");
            } else if (respuesta.equals("")) {
                ventanaPrincipal.mostrarMensajeError("No aplica");
            } else if (respuesta.equalsIgnoreCase("conectado")) {
                ventanaPrincipal.mostrarMensajeError("Ya se encuentra logeado dentro del sistema");
            }
        }
        if (e.getSource() == ventanaPrincipal.panelJuegoChat.jButtonEnviar) {
            pedirCoordenadasCartas();
        }
    }

    public File pedirArchivoPropiedades() {
        return ventanaPrincipal.pedirArchivoPropiedades();

    }

    /**
     * Muestra un mensaje de error al usuario, generalmente mediante un cuadro
     * de diálogo.
     *
     * @param mensaje el mensaje de error que se desea mostrar.
     */
    public void mostrarMensajeError(String mensaje) {
        ventanaPrincipal.mostrarMensajeError(mensaje);
    }

    /**
     * Muestra un mensaje de éxito al usuario, generalmente cuando una acción se
     * realiza correctamente.
     *
     * @param mensaje el mensaje de éxito que se desea mostrar.
     */
    public void mostrarMensajeExito(String mensaje) {
        ventanaPrincipal.mostrarMensajeExito(mensaje);
    }

    public void mostrarMensajeChatJuego(String msg) {
        ventanaPrincipal.panelJuegoChat.mostrarMensajeChatJuego(msg);
    }

    public void bloquearEntradaTextoChatJuego() {
        ventanaPrincipal.panelJuegoChat.jButtonEnviar.setEnabled(false);
        ventanaPrincipal.panelJuegoChat.jSpinnerCoordenadaX.setEnabled(false);
        ventanaPrincipal.panelJuegoChat.jSpinnerCoordenadaY.setEnabled(false);
    }

    public void permitirEntradaTextoChatJuego() {
        ventanaPrincipal.panelJuegoChat.jButtonEnviar.setEnabled(true);
        ventanaPrincipal.panelJuegoChat.jSpinnerCoordenadaX.setEnabled(true);
        ventanaPrincipal.panelJuegoChat.jSpinnerCoordenadaY.setEnabled(true);
    }

    public void pedirCoordenadasCartas() {
        int x = (Integer) ventanaPrincipal.panelJuegoChat.jSpinnerCoordenadaX.getValue();
        int y = (Integer) ventanaPrincipal.panelJuegoChat.jSpinnerCoordenadaY.getValue();

        if (controlPrincipal.isEsperandoPrimera()) {
            // Primera coordenada
            coordenadaX1 = x;
            coordenadaY1 = y;
            ventanaPrincipal.panelJuegoChat.mostrarMensajeChatJuego(
                    "Primera coordenada: (" + x + "," + y + ")");
            // Reiniciar spinners para la segunda entrada
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
            try {
                controlPrincipal.enviarPosicionCartas(coordenadaX1, coordenadaY1, x2, y2);
                bloquearEntradaTextoChatJuego();
            } catch (IOException ex) {
                mostrarMensajeError("Ocurrió un error al mandar las coordenadas");
            }
            controlPrincipal.setEsperandoPrimera(true);
        }
    }

}
