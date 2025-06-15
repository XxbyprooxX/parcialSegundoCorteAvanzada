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
                controlPrincipal.crearThreadCliente();
            } else if (respuesta.equalsIgnoreCase("noLogeado")) {
                ventanaPrincipal.mostrarMensajeError("Credenciales incorrectas intente de nuevo");
            } else if (respuesta.equals("")) {
                ventanaPrincipal.mostrarMensajeError("No aplica");
            } else if (respuesta.equalsIgnoreCase("conectado")) {
                ventanaPrincipal.mostrarMensajeError("Ya se encuentra logeado dentro del sistema");
            }
        }
        if(e.getSource() == ventanaPrincipal.panelJuegoChat.jButtonEnviar){
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
    
    public void bloquearEntradaTextoChatJuego(){
        ventanaPrincipal.panelJuegoChat.jButtonEnviar.setEnabled(false);
        ventanaPrincipal.panelJuegoChat.jTextFieldMensaje.setEnabled(false); 
    }
    
    public void permitirEntradaTextoChatJuego(){
        ventanaPrincipal.panelJuegoChat.jButtonEnviar.setEnabled(true);
        ventanaPrincipal.panelJuegoChat.jTextFieldMensaje.setEnabled(true);
    }
    

    public void pedirCoordenadasCartas() {
        String texto = ventanaPrincipal.panelJuegoChat.jTextFieldMensaje.getText().trim();
        if (texto.isBlank()) {
            return;
        }
        ventanaPrincipal.panelJuegoChat.mostrarMensajeChatJuego("Tú: " + texto);
        ventanaPrincipal.panelJuegoChat.jTextFieldMensaje.setText("");
        try {
            int valor = Integer.parseInt(texto);
            if (controlPrincipal.getPasoActivoCoordenadas() == 0) { //cordenada en x primera carta
                String coordenadas = controlPrincipal.getCoordenadasCartas();
                coordenadas += valor+ ",";
                controlPrincipal.setCoordenadasCartas(coordenadas);
                
                controlPrincipal.setPasoActivoCoordenadas(1);
                ventanaPrincipal.panelJuegoChat.mostrarMensajeChatJuego("Ok. Ahora ingresa la cordenada en y de la primera carta");
            } else if(controlPrincipal.getPasoActivoCoordenadas()==1){// cordenada en y primera carta
                String coordenadas = controlPrincipal.getCoordenadasCartas();
                coordenadas += valor+ ",";
                controlPrincipal.setCoordenadasCartas(coordenadas);
                
                controlPrincipal.setPasoActivoCoordenadas(2);
                ventanaPrincipal.panelJuegoChat.mostrarMensajeChatJuego("Ok. Ahora ingresa la cordenada en x de la segunda carta");
            } else if(controlPrincipal.getPasoActivoCoordenadas()==2){ // coordenada en x segunda carta
                
                String coordenadas = controlPrincipal.getCoordenadasCartas();
                coordenadas += valor+ ",";
                controlPrincipal.setCoordenadasCartas(coordenadas);
                
                controlPrincipal.setPasoActivoCoordenadas(3);
                ventanaPrincipal.panelJuegoChat.mostrarMensajeChatJuego("Ok. Ahora ingresa la cordenada en y de la segunda carta");
            } else if(controlPrincipal.getPasoActivoCoordenadas()== 3){ // coordenada en y segunda carta
                String coordenadas = controlPrincipal.getCoordenadasCartas();
                coordenadas += valor;
                controlPrincipal.setCoordenadasCartas(coordenadas);
                controlPrincipal.setPasoActivoCoordenadas(4);
            }
        } catch (NumberFormatException e) {
            ventanaPrincipal.panelJuegoChat.mostrarMensajeChatJuego("Por favor, ingresa un numero valido");
        }

    }

}
