package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.control;

import java.io.DataInputStream;
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
            
            System.out.println("Cliente leyendo el turno asignado desde servidor " + turno);
        } catch (IOException ex) {
            controlCliente.mostrarMensajeError("Ocurrio un erroe en el turno");
        }

        while (true) {

            try {
                String opcion = entrada.readUTF();
                
                String[] partesOpcion = opcion.split(",");
                int turnoActual;
                
                System.out.println("Se leen la opcion que llega desde el servidor" + opcion);

                switch (partesOpcion[0]) {
                    case "pedirCoordenadas":

                        salida.writeUTF("consultarTurno");
                        turnoActual = entrada.readInt();
                        
                        System.out.println("Se consulta el turno actual");
                        
                        System.out.println("Se lee el turno actual de quien esta jugando " + turnoActual);

                        enviarPosicionCartas(turnoActual);
                        break;
                    case "acerto":
                        salida.writeUTF("consultarTurno");
                        turnoActual = entrada.readInt();
                        
                        System.out.println("Se consulta el turno actual");
                        System.out.println("Se lee el turno actual de quien esta jugando " + turnoActual);
                        if (turnoActual != turno) {
                            return;
                        }

                        controlCliente.mostrarMensajeChatJuego("Acertaste, vuelve a ingresar otras Coordenadas");
                        salida.writeUTF("pedirDatosJugador");
                        
                        System.out.println("Se piden los datos del jugador ");
                        String datosA = entrada.readUTF();
                        String[] datosAA = datosA.split(",");
                        
                        System.out.println("Se reciben los datos del jugador que acerto " + datosA);

                        controlCliente.mostrarMensajeChatJuego("Intentos realizados:" + datosAA[0]
                                + " | Cantidad de Aciertos: " + datosAA[1]
                                + " | Porcentaje de eficiencia :" + datosAA[2]);
                        
                        salida.writeUTF("siguienteTurno");
                        
                        System.out.println("Se manda que se pase al siguiente turno");
                        break;
                    case "fallo":
                        
                        salida.writeUTF("consultarTurno");
                        turnoActual = entrada.readInt();
                        
                        System.out.println("Se consulta el turno actual");
                        System.out.println("Se lee el turno actual de quien esta jugando " + turnoActual);
                        
                        if (turnoActual != turno) {
                            return;
                        }

                        controlCliente.mostrarMensajeChatJuego("Fallaste, debido a que " + partesOpcion[1] + ", se paso el turno al siguiente jugador");

                        salida.writeUTF("pedirDatosJugador");
                        
                        System.out.println("Se piden los datos del jugador");
                        
                        String datosB = entrada.readUTF();
                        String[] datosBB = datosB.split(",");
                        
                        System.out.println("Se piden los datos del jugador que fallo " + datosB);

                        controlCliente.mostrarMensajeChatJuego("Intentos realizados:" + datosBB[0]
                                + " | Cantidad de Aciertos: " + datosBB[1]
                                + " | Porcentaje de eficiencia :" + datosBB[2]);
                        
                        salida.writeUTF("siguienteTurno");
                        
                        System.out.println("Se manda a que pase al siguiente turno");

                        break;
                    case "juegoTerminado":

                        salida.writeUTF("pedirGanador");
                        controlCliente.mostrarMensajeChatJuego(entrada.readUTF());
                        
                        System.out.println("Se consulta el ganador");
                        System.out.println("Se recibio el juego termino");

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
                salida.writeUTF(mensajeSalida);
                
                System.out.println("Se envian las coordenadas del cliente ");
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
