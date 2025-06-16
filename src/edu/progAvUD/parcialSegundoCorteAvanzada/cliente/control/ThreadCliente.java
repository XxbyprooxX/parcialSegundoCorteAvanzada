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
            turno = entrada.readInt();
            System.out.println("Turno: " + turno);
        } catch (IOException ex) {
            controlCliente.mostrarMensajeError("Ocurrio un erroe en el turno");
        }

        while (true) {

            try {
                String opcione = entrada.readUTF();

                switch (opcione) {
                    case "pedirCoordenadas":
                        System.out.println("Se pidieron Coordenadas");
                        salida.writeUTF("consultarTurno");
                        int turnoActual = entrada.readInt();
                        System.out.println("Turno Actual: " + turnoActual);

                        enviarPosicionCartas(turnoActual);

                        break;
                    default:
                       ;
                }

            } catch (IOException ex) {
                controlCliente.mostrarMensajeError("Ocurrio un erroe en opcione de ThreadHilo");
                System.exit(0);
            }

        }

    }

    public void enviarPosicionCartas(int turnoActual) throws IOException {
        if (turnoActual != turno) {
            controlCliente.bloquearEntradaTextoChatJuego();
            return;
        }

        controlCliente.permitirEntradaTextoChatJuego();

        // Solicitar primera coordenada
        controlCliente.mostrarMensajeChatJuego("Es tu turno. Ingresa la coordenada en x de la primera carta");
        esperarEntradaCoordenada();

        // Solicitar segunda coordenada
        controlCliente.mostrarMensajeChatJuego("Ingresa la coordenada en x de la segunda carta");
        esperarEntradaCoordenada();
    }

    private void esperarEntradaCoordenada() throws IOException {
        while (true) {
            if (controlCliente.getPasoActivoCoordenadas() == 2) {
                String mensajeSalida ="eleccionJugador," + controlCliente.getCoordenadasCartas();
                System.out.println("MensajeSalida: " + mensajeSalida);
                salida.writeUTF(mensajeSalida);
                controlCliente.setPasoActivoCoordenadas(0);
                controlCliente.setCoordenadasCartas("");
                break; // termina el bucle una vez enviada la coordenada
            }
            try {
                Thread.sleep(100); // pausa para no saturar CPU
            } catch (InterruptedException ex) {
                controlCliente.mostrarMensajeError("Error al esperar coordenada: " + ex.getMessage());
                Thread.currentThread().interrupt(); // buena práctica al capturar InterruptedException
                break;
            }
        }
    }

}
