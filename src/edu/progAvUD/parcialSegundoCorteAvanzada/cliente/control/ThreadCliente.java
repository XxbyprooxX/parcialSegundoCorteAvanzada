package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * La clase {@code ThreadCliente} es un hilo que gestiona la comunicación
 * entre el cliente y el servidor dentro del juego. Este hilo escucha
 * constantemente mensajes enviados por el servidor y responde a ellos
 * según la lógica del juego, permitiendo interacciones como turnos, aciertos,
 * fallos, y finalización del juego.
 * 
 * También administra el envío de coordenadas del jugador hacia el servidor
 * y controla la interfaz del usuario en función del estado del turno.
 * 
 * Autor: Andres Felipe
 */
public class ThreadCliente extends Thread {

    // Flujo de entrada desde el servidor
    private DataInputStream entrada;

    // Flujo de salida hacia el servidor
    private DataOutputStream salida;

    // Turno asignado a este cliente
    private int turno;

    // Turno actual del juego (determinado por el servidor)
    private int turnoActual;

    // Controlador general del cliente para manejar la interfaz y la lógica del cliente
    private ControlCliente controlCliente;

    /**
     * Constructor del hilo cliente.
     *
     * @param entrada Flujo de entrada desde el servidor.
     * @param salida Flujo de salida hacia el servidor.
     * @param controlCliente Instancia del controlador del cliente que maneja la lógica de IU.
     */
    public ThreadCliente(DataInputStream entrada, DataOutputStream salida, ControlCliente controlCliente) {
        this.entrada = entrada;
        this.salida = salida;
        this.controlCliente = controlCliente;
    }

    /**
     * Método principal del hilo. Ejecuta la lógica de escucha continua
     * para recibir instrucciones del servidor y responder a ellas.
     */
    @Override
    public void run() {
        try {
            controlCliente.mostrarMensajeChatJuego("Esperando a empezar el juego, espere que el servidor empiece el juego");
            turno = entrada.readInt();
            System.out.println("Cliente leyendo el turno asignado desde servidor " + turno);
        } catch (IOException ex) {
            controlCliente.mostrarMensajeError("Ocurrió un error al recibir el turno inicial.");
        }

        while (true) {
            try {
                String opcion = entrada.readUTF();
                String[] partesOpcion = opcion.split(",");

                System.out.println("Se lee la opción que llega desde el servidor: " + opcion);

                switch (partesOpcion[0]) {

                    case "pedirCoordenadas":
                        salida.writeUTF("consultarTurno");
                        turnoActual = entrada.readInt();
                        System.out.println("Turno actual recibido: " + turnoActual);
                        if (turno == turnoActual) {
                            controlCliente.permitirEntradaTextoChatJuego();
                            controlCliente.mostrarMensajeChatJuego("Es tu turno. Ingresa la primera coordenada:");
                            controlCliente.setEsperandoPrimera(true);
                        }
                        break;

                    case "acerto":
                        salida.writeUTF("consultarTurno");
                        turnoActual = entrada.readInt();
                        System.out.println("Turno actual después del acierto: " + turnoActual);

                        controlCliente.mostrarMensajeChatJuego("¡Acertaste! Ingresa otras coordenadas.");
                        salida.writeUTF("pedirDatosJugador");

                        String datosA = entrada.readUTF();
                        String[] datosAA = datosA.split(",");

                        controlCliente.mostrarMensajeChatJuego("Intentos realizados: " + datosAA[0]
                                + " | Aciertos: " + datosAA[1]
                                + " | Eficiencia: " + datosAA[2] + "%");

                        salida.writeUTF("siguienteTurno");
                        System.out.println("Se envía señal para pasar al siguiente turno.");
                        break;

                    case "fallo":
                        salida.writeUTF("consultarTurno");
                        turnoActual = entrada.readInt();
                        System.out.println("Turno actual después del fallo: " + turnoActual);

                        controlCliente.mostrarMensajeChatJuego("Fallaste porque " + partesOpcion[1]
                                + ". Se pasa el turno al siguiente jugador.");

                        salida.writeUTF("pedirDatosJugador");
                        String datosB = entrada.readUTF();
                        String[] datosBB = datosB.split(",");

                        controlCliente.mostrarMensajeChatJuego("Intentos realizados: " + datosBB[0]
                                + " | Aciertos: " + datosBB[1]
                                + " | Eficiencia: " + datosBB[2] + "%");

                        salida.writeUTF("siguienteTurno");
                        System.out.println("Se envía señal para pasar al siguiente turno.");
                        break;

                    case "juegoTerminado":
                        salida.writeUTF("pedirGanador");
                        controlCliente.mostrarMensajeChatJuego(entrada.readUTF());

                        System.out.println("El juego ha terminado. Ganador recibido.");
                        break;

                    default:
                        controlCliente.mostrarMensajeError("La opción recibida no es válida.");
                        break;
                }

            } catch (IOException ex) {
                controlCliente.mostrarMensajeError("Ocurrió un error al procesar las opciones del hilo cliente.");
                System.exit(0); // Se cierra el programa por error crítico
            }
        }
    }

    /**
     * Envía al servidor las coordenadas seleccionadas por el jugador.
     * Solo se ejecuta si es el turno actual del jugador.
     *
     * @param x1 Coordenada X de la primera carta.
     * @param y1 Coordenada Y de la primera carta.
     * @param x2 Coordenada X de la segunda carta.
     * @param y2 Coordenada Y de la segunda carta.
     * @throws IOException En caso de error en la escritura de datos al servidor.
     */
    public synchronized void enviarPosicionCartas(int x1, int y1, int x2, int y2) throws IOException {
        if (turnoActual != turno) {
            controlCliente.bloquearEntradaTextoChatJuego();
            return;
        }
        esperarEntradaCoordenada(x1, y1);
        esperarEntradaCoordenada(x2, y2);
    }

    /**
     * Envía una única coordenada al servidor.
     *
     * @param x Coordenada X.
     * @param y Coordenada Y.
     * @throws IOException En caso de error en el envío de datos.
     */
    private void esperarEntradaCoordenada(int x, int y) throws IOException {
        String mensajeSalida = String.format("eleccionJugador,%d,%d", x, y);
        salida.writeUTF(mensajeSalida);
        System.out.println("Se envían las coordenadas: " + mensajeSalida);
    }
}
