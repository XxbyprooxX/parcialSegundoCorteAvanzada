package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.Servidor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Cristianlol789
 */
public class ThreadServidor extends Thread {

    /**
     * Objeto que representa la conexión con el cliente
     */
    private Servidor servidor;
    /**
     * Controlador principal del servidor para acceder a la consola y a la lista
     * de usuarios
     */
    private ControlServidor controlServidor;

    /**
     * Contador estático para asignar turnos únicos a cada cliente
     */
    private static AtomicInteger contadorTurnos = new AtomicInteger(1);

    /**
     * Número de turno asignado a este cliente
     */
    private int numeroTurno;

    /**
     * Constructor del hilo servidor que inicia la conexión con los clientes.
     *
     * @param socketCliente1 Socket de comunicación para entrada/salida del
     * cliente.
     * @param socketCliente2 Socket adicional para enviar mensajes desde el
     * servidor.
     * @param controlServidor Referencia al controlador general del servidor.
     */
    public ThreadServidor(Socket socketCliente1, Socket socketCliente2, ControlServidor controlServidor) {
        String nombreUsuario = "";
        this.servidor = new Servidor(socketCliente1, socketCliente2, nombreUsuario);
        this.controlServidor = controlServidor;
        // Asignar turno automáticamente al crear el hilo
        this.numeroTurno = asignarTurno();
    }

    /**
     * Método que asigna un turno único a cada cliente conectado
     *
     * @return El número de turno asignado
     */
    private int asignarTurno() {
        int turno = contadorTurnos.getAndIncrement();
        controlServidor.mostrarMensajeConsolaServidor("Cliente conectado - Turno asignado: " + turno);
        return turno;
    }

    /**
     * Método que verifica los clientes conectados y gestiona sus turnos para
     * Concentrese
     */
    public void gestionarTurnosConcentrese() {
        try {
            // Enviar información del turno al cliente
            DataOutputStream salida1 = this.servidor.getServidorInformacionSalida1();
            if (salida1 != null) {
                salida1.writeUTF("TURNO_ASIGNADO:" + this.numeroTurno);
                salida1.flush();
            }

            // Mostrar información en la consola del servidor
            controlServidor.mostrarMensajeConsolaServidor(
                    "Cliente: " + servidor.getNombreUsuario()
                    + " | Turno: " + this.numeroTurno
                    + " | Estado: Conectado"
            );

            // Verificar si es el turno de este cliente
            verificarTurnoActivo();

        } catch (IOException e) {
            controlServidor.mostrarMensajeConsolaServidor(
                    "Error al gestionar turno del cliente " + servidor.getNombreUsuario() + ": " + e.getMessage()
            );
        }
    }

    /**
     * Método que verifica si es el turno activo del cliente
     */
    public void verificarTurnoActivo() {
        try {
            // Obtener el turno actual que debe estar activo
            int turnoActivo = controlServidor.getTurnoActivo();

            if (this.numeroTurno == turnoActivo) {
                // Es el turno de este cliente
                DataOutputStream salida1 = this.servidor.getServidorInformacionSalida1();
                if (salida1 != null) {
                    salida1.writeUTF("ES_TU_TURNO");
                    salida1.flush();
                }
                controlServidor.mostrarMensajeConsolaServidor(
                        "Turno activo: Cliente " + servidor.getNombreUsuario() + " (Turno #" + this.numeroTurno + ")"
                );
            } else {
                // No es su turno, debe esperar
                DataOutputStream salida1 = this.servidor.getServidorInformacionSalida1();
                if (salida1 != null) {
                    salida1.writeUTF("ESPERAR_TURNO:" + turnoActivo);
                    salida1.flush();
                }
            }
        } catch (IOException e) {
            controlServidor.mostrarMensajeConsolaServidor(
                    "Error al verificar turno activo: " + e.getMessage()
            );
        }
    }

    /**
     * Método que maneja cuando el jugador acierta una pareja en Concentrese El
     * jugador mantiene su turno
     */
    public void manejarAcierto() {
        try {
            controlServidor.mostrarMensajeConsolaServidor(
                    "¡" + servidor.getNombreUsuario() + " acertó! Mantiene su turno #" + this.numeroTurno
            );

            // El jugador mantiene el turno, solo notificar el acierto
            DataOutputStream salida1 = this.servidor.getServidorInformacionSalida1();
            if (salida1 != null) {
                salida1.writeUTF("ACIERTO_MANTIENE_TURNO");
                salida1.flush();
            }

            // Verificar si el juego ha terminado (todas las cartas emparejadas)
            if (controlServidor.verificarJuegoTerminado()) {
                controlServidor.terminarJuego();
            }

        } catch (IOException e) {
            controlServidor.mostrarMensajeConsolaServidor(
                    "Error al manejar acierto: " + e.getMessage()
            );
        }
    }

    /**
     * Método que maneja cuando el jugador falla en Concentrese El turno pasa al
     * siguiente jugador
     */
    public void manejarFallo() {
        try {
            controlServidor.mostrarMensajeConsolaServidor(
                    servidor.getNombreUsuario() + " falló. Turno pasa al siguiente jugador"
            );

            // Notificar al cliente que falló
            DataOutputStream salida1 = this.servidor.getServidorInformacionSalida1();
            if (salida1 != null) {
                salida1.writeUTF("FALLO_PIERDE_TURNO");
                salida1.flush();
            }

            // Avanzar al siguiente turno
            controlServidor.avanzarSiguienteTurnoConcentrese();

        } catch (IOException e) {
            controlServidor.mostrarMensajeConsolaServidor(
                    "Error al manejar fallo: " + e.getMessage()
            );
        }
    }

    /**
     * Método que procesa una jugada de Concentrese con coordenadas y cartas
     *
     * @param x1 Coordenada X de la primera carta
     * @param y1 Coordenada Y de la primera carta
     * @param carta1 Contenido de la primera carta como String
     * @param x2 Coordenada X de la segunda carta
     * @param y2 Coordenada Y de la segunda carta
     * @param carta2 Contenido de la segunda carta como String
     */
    public void procesarJugadaConcentrese(int x1, int y1, String carta1, int x2, int y2, String carta2) {
        // Verificar que es el turno del jugador
        if (this.numeroTurno != controlServidor.getTurnoActivo()) {
            try {
                DataOutputStream salida1 = this.servidor.getServidorInformacionSalida1();
                if (salida1 != null) {
                    salida1.writeUTF("ERROR_NO_ES_TU_TURNO");
                    salida1.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        controlServidor.mostrarMensajeConsolaServidor(
                servidor.getNombreUsuario() + " jugó: Pos1(" + x1 + "," + y1 + ")=" + carta1 
                + " y Pos2(" + x2 + "," + y2 + ")=" + carta2
        );

        // Validar si las cartas forman una pareja
        boolean esPareja = controlServidor.verificarPareja(x1, y1, carta1, x2, y2, carta2);
        
        if (esPareja) {
            manejarAcierto();
        } else {
            manejarFallo();
        }
    }

    /**
     * Método que obtiene información del cliente y su turno
     *
     * @return String con la información del cliente
     */
    public String getInformacionCliente() {
        return "Cliente: " + servidor.getNombreUsuario()
                + " | Turno: " + this.numeroTurno
                + " | IP: " + servidor.getServidorCliente1().getInetAddress().getHostAddress();
    }

    /**
     * Getter para el número de turno
     *
     * @return el número de turno asignado
     */
    public int getNumeroTurno() {
        return this.numeroTurno;
    }

    /**
     * Getter para el objeto Servidor
     *
     * @return el objeto servidor asociado
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Método que se ejecuta cuando se inicia el hilo. Establece los flujos de
     * entrada/salida y maneja el ciclo de escucha del cliente.
     */
    @Override
    public void run() {
        controlServidor.mostrarMensajeConsolaServidor(".::Esperando Mensajes :");
        try {
            DataInputStream entrada = new DataInputStream(this.servidor.getServidorCliente1().getInputStream());
            this.servidor.setServidorInformacionEntrada1(entrada);
            DataOutputStream salida1 = new DataOutputStream(this.servidor.getServidorCliente1().getOutputStream());
            this.servidor.setServidorInformacionSalida1(salida1);
            DataOutputStream salida2 = new DataOutputStream(this.servidor.getServidorCliente2().getOutputStream());
            this.servidor.setServidorInformacionSalida2(salida2);
            servidor.setNombreUsuario(entrada.readUTF());

            // Gestionar turnos después de establecer la conexión
            gestionarTurnosConcentrese();

            // Ciclo principal de escucha de mensajes del cliente
            String mensaje;
            while ((mensaje = entrada.readUTF()) != null) {
                String[] partes = mensaje.split(":");
                String comando = partes[0];

                switch (comando) {
                    case "JUGADA_CONCENTRESE":
                        // Formato: JUGADA_CONCENTRESE:x1:y1:carta1:x2:y2:carta2
                        if (partes.length == 7) {
                            try {
                                int x1 = Integer.parseInt(partes[1]);
                                int y1 = Integer.parseInt(partes[2]);
                                String carta1 = partes[3];
                                int x2 = Integer.parseInt(partes[4]);
                                int y2 = Integer.parseInt(partes[5]);
                                String carta2 = partes[6];
                                
                                procesarJugadaConcentrese(x1, y1, carta1, x2, y2, carta2);
                            } catch (NumberFormatException e) {
                                controlServidor.mostrarMensajeConsolaServidor(
                                    "Error: Coordenadas inválidas recibidas de " + servidor.getNombreUsuario()
                                );
                                // Enviar error al cliente
                                salida1 = this.servidor.getServidorInformacionSalida1();
                                if (salida1 != null) {
                                    salida1.writeUTF("ERROR_COORDENADAS_INVALIDAS");
                                    salida1.flush();
                                }
                            }
                        } else {
                            controlServidor.mostrarMensajeConsolaServidor(
                                "Error: Formato de jugada inválido de " + servidor.getNombreUsuario()
                            );
                            // Enviar error al cliente
                            salida1 = this.servidor.getServidorInformacionSalida1();
                            if (salida1 != null) {
                                salida1.writeUTF("ERROR_FORMATO_JUGADA");
                                salida1.flush();
                            }
                        }
                        break;

                    case "CONSULTAR_TURNO":
                        verificarTurnoActivo();
                        break;

                    case "CONSULTAR_ESTADO_JUEGO":
                        controlServidor.enviarEstadoJuego(this);
                        break;

                    default:
                        // Procesar otros mensajes del cliente
                        controlServidor.mostrarMensajeConsolaServidor(
                                "Mensaje de " + servidor.getNombreUsuario() + ": " + mensaje
                        );
                        break;
                }
            }

        } catch (IOException e) {
            controlServidor.mostrarMensajeConsolaServidor(
                    "Cliente " + servidor.getNombreUsuario() + " desconectado"
            );
            // Remover cliente de la lista al desconectarse
            controlServidor.removerCliente(this);
            e.printStackTrace();
        } finally {
            controlServidor.removerCliente(this);
        }
    }
}