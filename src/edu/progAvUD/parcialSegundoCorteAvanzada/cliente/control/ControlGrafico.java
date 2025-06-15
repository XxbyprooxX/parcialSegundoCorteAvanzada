package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.cliente.vista.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 *
 * @author Andres Felipe
 */
public class ControlGrafico implements ActionListener {

    private ControlPrincipal controlPrincipal;
    private VentanaPrincipal ventanaPrincipal;

    public ControlGrafico(ControlPrincipal controlPrincipal) {
        this.controlPrincipal = controlPrincipal;
        this.ventanaPrincipal = new VentanaPrincipal();

        ventanaPrincipal.mostrarPanel(ventanaPrincipal.panelInicial);

        ventanaPrincipal.panelInicial.jButtonPropiedadesSocket.addActionListener(this);
        ventanaPrincipal.panelLogin.jButtonInciarSesion.addActionListener(this);

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
            } else if (respuesta.equalsIgnoreCase("noLogeado")) {
                ventanaPrincipal.mostrarMensajeError("Credenciales incorrectas intente de nuevo");
            } else if (respuesta.equals("")) {
                ventanaPrincipal.mostrarMensajeError("No aplica");
            } else if (respuesta.equalsIgnoreCase("conectado")){
                ventanaPrincipal.mostrarMensajeError("Ya se encuentra logeado dentro del sistema");
            }
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

}
