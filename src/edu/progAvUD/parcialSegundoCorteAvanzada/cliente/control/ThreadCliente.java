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
            controlCliente.mostrarMensajeChatJuego("Esperando a empezar el juego, espere que el servidor empiece el juego");
            turno = entrada.readInt();
            System.out.println("Turno: " + turno);
        } catch (IOException ex) {
            controlCliente.mostrarMensajeError("Ocurrio un erroe en el turno");
        }

        while (true) {

            try {
                String opcione = entrada.readUTF();
                String[] partesOpcion = opcione.split(",");
                int turnoActual;

                switch (partesOpcion[0]) {
                    case "pedirCoordenadas":
                        System.out.println("Se pidieron Coordenadas");

                        salida.writeUTF("consultarTurno");
                        turnoActual = entrada.readInt();
                        System.out.println("Turno Actual: " + turnoActual);

                        enviarPosicionCartas(turnoActual);
                        break;
                    case "acerto":
                        salida.writeUTF("consultarTurno");
                        turnoActual = entrada.readInt();
                        System.out.println("Turno Actual: " + turnoActual);
                        
                        if (turnoActual != turno) {
                            return;
                        }

                        controlCliente.mostrarMensajeChatJuego("Acertaste, vuelve a ingresar otras Coordenadas");
                        salida.writeUTF("pedirDatosJugador");
                        String[] datos = entrada.readUTF().split(",");

                        controlCliente.mostrarMensajeChatJuego("Intentos realizados:" + datos[0]
                                + " | Cantidad de Aciertos: " + datos[1]
                                + " | Porcentaje de eficiencia :" + datos[2]);

                        break;
                    case "fallo":
                        salida.writeUTF("consultarTurno");
                        turnoActual = entrada.readInt();
                        System.out.println("Turno Actual: " + turnoActual);
                        
                        if (turnoActual != turno) {
                            return;
                        }

                        controlCliente.mostrarMensajeChatJuego("Fallaste, debido a que " + partesOpcion[1] + ", se paso el turno al siguiente jugador");

                        salida.writeUTF("pedirDatosJugador");
                        String[] datos1 = entrada.readUTF().split(",");

                        controlCliente.mostrarMensajeChatJuego("Intentos realizados:" + datos1[0]
                                + " | Cantidad de Aciertos: " + datos1[1]
                                + " | Porcentaje de eficiencia :" + datos1[2]);

                        break;
                    case "juegoTerminado":

                        salida.writeUTF("pedirGanador");
                        controlCliente.mostrarMensajeChatJuego(entrada.readUTF());

                        break;
                    default:
                        controlCliente.mostrarMensajeError("La opcion no esta dentro de las opciones");
                        break;
                }

            } catch (IOException ex) {
                controlCliente.mostrarMensajeError("Ocurrio un error en opcione de ThreadHilo");
                System.exit(0);
            }

        }

    }

    public synchronized void enviarPosicionCartas(int turnoActual) throws IOException {
        if (turnoActual != turno) {
            controlCliente.bloquearEntradaTextoChatJuego();
            return;
        }

        // PRIMERA coordenada
        controlCliente.permitirEntradaTextoChatJuego();
        controlCliente.mostrarMensajeChatJuego("Es tu turno. Ingresa la coordenada X de la primera carta");
        esperarEntradaCoordenada();

        // SEGUNDA coordenada
        controlCliente.permitirEntradaTextoChatJuego();                  // ← aquí
        controlCliente.mostrarMensajeChatJuego("Ingresa la coordenada X de la segunda carta");
        esperarEntradaCoordenada();
    }

    private void esperarEntradaCoordenada() throws IOException {
        while (true) {
            if (controlCliente.getPasoActivoCoordenadas() == 2) {
                String mensajeSalida = "eleccionJugador," + controlCliente.getCoordenadasCartas();
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
                break;
            }
        }
    }

}
