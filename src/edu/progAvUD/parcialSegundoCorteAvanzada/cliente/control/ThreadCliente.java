/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.control;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Andres Felipe
 */
public class ThreadCliente extends Thread {

    private DataInputStream entrada;
    private DataOutputStream salida;
    private int turno;
    private ControlCliente controlCliente;

    public ThreadCliente(DataInputStream entrada, DataOutputStream salida, ControlCliente controlCliente) {
        this.entrada = entrada;
        this.salida = salida;
        this.controlCliente = controlCliente;
    }

    @Override
    public void run() {

        try {
            this.turno = entrada.readInt();
        } catch (IOException ex) {
            controlCliente.mostrarMensajeError("Ocurrio un error al leer el turno");
        }

        String llegada;
        while (true) {
            try {
                // Leer el código de operación enviado por el servidor
                llegada = entrada.readUTF();
                String[] llegadas = llegada.split(",");

                if (llegadas[0].equals(turno + "")) {
                    switch (llegadas[1]) {
                        case "pedirCoordenadas":
                            controlCliente.mostrarMensajeChatJuego("Es tu turno. Escribe la cordenada en x de la primera carta a escoger: ");
                            controlCliente.permitirEntradaTextoChatJuego();
                            if (controlCliente.getPasoActivoCoordenadas() == 4) {
                                salida.writeUTF("eleccionJugador," + controlCliente.getCoordenadasCartas());
                            }
                            break;
                        case "Hola":
                            
                            break;
                    }

                } else {
                    controlCliente.bloquearEntradaTextoChatJuego();
                }

            } catch (IOException e) {
                // Manejo de error si ocurre una excepción de entrada/salida
                controlCliente.mostrarMensajeError("Error en la comunicación con el servidor");
                System.exit(0); // Finaliza la aplicación
                break;
            }
        }

        // Mensaje mostrado si se rompe el bucle (generalmente por desconexión)
        controlCliente.mostrarMensajeError("Se desconectó el servidor");
    }

}
