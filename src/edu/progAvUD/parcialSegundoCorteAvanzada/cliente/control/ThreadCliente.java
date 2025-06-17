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
                System.out.println("Opcione:" + opcione);
                
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
                        
//                        if (turnoActual != turno) {
//                            return;
//                        }

                        controlCliente.mostrarMensajeChatJuego("Acertaste, vuelve a ingresar otras Coordenadas");
                        salida.writeUTF("pedirDatosJugador");
                        String datosA = entrada.readUTF();
                        System.out.println("Datos: "+datosA);
                        String[] datosAA = datosA.split(",");
                        
                        

                        controlCliente.mostrarMensajeChatJuego("Intentos realizados:" + datosAA[0]
                                + " | Cantidad de Aciertos: " + datosAA[1]
                                + " | Porcentaje de eficiencia :" + datosAA[2]);
                        
                        salida.writeUTF("siguienteTurno");

                        break;
                    case "fallo":
                        
                        salida.writeUTF("consultarTurno");
                        turnoActual = entrada.readInt();
                        System.out.println("Turno Actual: " + turnoActual);
                        
//                        if (turnoActual != turno) {
//                            return;
//                        }

                        controlCliente.mostrarMensajeChatJuego("Fallaste, debido a que " + partesOpcion[1] + ", se paso el turno al siguiente jugador");

                        salida.writeUTF("pedirDatosJugador");
                        
                        
                        String datosB = entrada.readUTF();
                        System.out.println("Datos: "+datosB);
                        String[] datosBB = datosB.split(",");

                        controlCliente.mostrarMensajeChatJuego("Intentos realizados:" + datosBB[0]
                                + " | Cantidad de Aciertos: " + datosBB[1]
                                + " | Porcentaje de eficiencia :" + datosBB[2]);
                        
                        salida.writeUTF("siguienteTurno");

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
