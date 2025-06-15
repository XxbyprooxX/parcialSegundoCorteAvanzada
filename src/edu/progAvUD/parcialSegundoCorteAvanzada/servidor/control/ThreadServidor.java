package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.JugadorVO;
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
     * Jugador asignado a este hilo después del login exitoso
     */
    private JugadorVO jugadorAsignado;

    /**
     * Controlador principal del servidor para acceder a la consola y a la lista
     * de usuarios
     */
    private ControlServidor controlServidor;

    /**
     * Contador estático para asignar turnos únicos a cada cliente
     */
    private static AtomicInteger contadorTurnos;

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
        contadorTurnos = new AtomicInteger(1);
        this.servidor = new Servidor(socketCliente1, socketCliente2, nombreUsuario);
        this.controlServidor = controlServidor;
        // Asignar turno automáticamente al crear el hilo
    }

    /**
     * Método que asigna un jugador después del login exitoso
     *
     * @param usuario Nombre de usuario para buscar el jugador
     * @return true si se asignó correctamente, false en caso contrario
     */
    private boolean asignarJugador(String usuario) {
        try {
            // Buscar el jugador en la base de datos o lista de jugadores
            String[] infoJugador = controlServidor.obtenerJugadorPorCredenciales(usuario).split(",");

            if (infoJugador != null) {
                this.jugadorAsignado = new JugadorVO(infoJugador[0], infoJugador[1], infoJugador[2], infoJugador[3], 0, 0, 0);
                // Resetear las estadísticas del jugador para la nueva partida
                this.jugadorAsignado.setCantidadIntentos(0);
                this.jugadorAsignado.setCantidadParejasResueltas(0);

                controlServidor.mostrarMensajeConsolaServidor(
                        "Jugador asignado: " + infoJugador[0]
                        + " (Usuario: " + infoJugador[2] + ")"
                );
                return true;
            }
        } catch (Exception e) {
            controlServidor.mostrarMensajeConsolaServidor(
                    "Error al asignar jugador: " + e.getMessage()
            );
        }
        return false;
    }

    /**
     * Método que asigna un turno único a cada cliente conectado
     *
     * @return El número de turno asignado
     */
    private int asignarTurno() {
        int turno = contadorTurnos.getAndIncrement();
        controlServidor.mostrarMensajeConsolaServidor(
                "Cliente " + servidor.getNombreUsuario() + " conectado - Turno asignado: " + turno
        );
        return turno;
    }

    /**
     * Método que verifica los clientes conectados y gestiona sus turnos para
     * Concentrese
     */
    public void gestionarTurnosConcentrese() {
        // Mostrar información en la consola del servidor
        controlServidor.mostrarMensajeConsolaServidor(
                "Cliente: " + servidor.getNombreUsuario()
                + " | Turno asignado: " + this.numeroTurno
                + " | Turno activo global: " + controlServidor.getTurnoActivo()
                + " | Es mi turno: " + (this.numeroTurno == controlServidor.getTurnoActivo())
        );
    }

    /**
     * Método que verifica si es el turno activo del cliente
     */
    public void verificarTurnoActivo() {
        // Obtener el turno actual que debe estar activo
        int turnoActivo = controlServidor.getTurnoActivo();
        boolean esMiTurno = (this.numeroTurno == turnoActivo);

        // Enviar información específica del cliente
        controlServidor.mostrarMensajeConsolaServidor(
                "Cliente " + servidor.getNombreUsuario()
                + " - Turno propio: " + this.numeroTurno
                + " - Turno activo: " + turnoActivo
                + " - Es mi turno: " + esMiTurno
        );
    }

    /**
     * Método que maneja cuando el jugador acierta una pareja en Concentrese El
     * jugador mantiene su turno
     */
    public void manejarAcierto() {
        try {
            // Actualizar estadísticas del jugador
            if (jugadorAsignado != null) {
                jugadorAsignado.setCantidadParejasResueltas(jugadorAsignado.getCantidadParejasResueltas() + 1);
                jugadorAsignado.setCantidadIntentos(jugadorAsignado.getCantidadIntentos() + 1);
            }

            String nombreMostrar = (jugadorAsignado != null) ? jugadorAsignado.getNombreJugador() : servidor.getNombreUsuario();
            controlServidor.mostrarMensajeConsolaServidor(
                    "¡" + nombreMostrar + " acertó! Mantiene su turno #" + this.numeroTurno
                    + " | Parejas resueltas: " + (jugadorAsignado != null ? jugadorAsignado.getCantidadParejasResueltas() : "N/A")
            );

            // El jugador mantiene el turno, solo notificar el acierto
            DataOutputStream salida1 = this.servidor.getServidorInformacionSalida1();
            if (salida1 != null) {
                salida1.writeUTF("acerto");
                salida1.flush();
            }

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
            // Actualizar estadísticas del jugador
            if (jugadorAsignado != null) {
                jugadorAsignado.setCantidadIntentos(jugadorAsignado.getCantidadIntentos() + 1);
            }

            String nombreMostrar = (jugadorAsignado != null) ? jugadorAsignado.getNombreJugador() : servidor.getNombreUsuario();
            controlServidor.mostrarMensajeConsolaServidor(
                    nombreMostrar + " falló. Turno pasa al siguiente jugador"
                    + " | Intentos: " + (jugadorAsignado != null ? jugadorAsignado.getCantidadIntentos() : "N/A")
            );

            // Notificar al cliente que falló
            DataOutputStream salida1 = this.servidor.getServidorInformacionSalida1();
            if (salida1 != null) {
                salida1.writeUTF("fallo");
                salida1.flush();
            }

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
     * @param x2 Coordenada X de la segunda carta
     * @param y2 Coordenada Y de la segunda carta
     */
    public void procesarJugadaConcentrese(int x1, int y1, int x2, int y2) {

        String tipoCarta1 = null;
        String tipoCarta2 = null;

        boolean esPareja = controlServidor.verificarPareja(x1, y1, tipoCarta1, x2, y2, tipoCarta2);

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

            while (true) {
                String mensaje = entrada.readUTF();
                String[] partes = mensaje.split(",");
                String comando = partes[0];

                switch (comando) {
                    case "eleccionJugador":
                        int x1 = Integer.parseInt(partes[1]);
                        int y1 = Integer.parseInt(partes[2]);
                        int x2 = Integer.parseInt(partes[4]);
                        int y2 = Integer.parseInt(partes[5]);
                        procesarJugadaConcentrese(x1, y1, x2, y2);
                        break;

                    case "consultarTurno":
                        verificarTurnoActivo();
                        break;

                    case "consultarEstadoJuego":
                        controlServidor.enviarEstadoJuego(this);
                        break;

                    case "login":
                        String usuario = partes[1];
                        String contrasena = partes[2];
                        boolean jugadorExiste = controlServidor.buscarUsuarioYContrasenaExistente(usuario, contrasena);

                        if (jugadorExiste && asignarJugador(usuario)) {
                            servidor.setNombreUsuario(usuario);
                            this.numeroTurno = asignarTurno();

                            salida1.writeUTF("valido");
                            salida1.flush();

                            gestionarTurnosConcentrese();
                            ControlServidor.setCantidadClientesLogeados(ControlServidor.getCantidadClientesLogeados() + 1);
                            controlServidor.verificarJugadoresMostrarBotonJugar();

                        } else {
                            salida1.writeUTF("invalido");
                            salida1.flush();
                        }
                        break;
                }
            }

        } catch (IOException e) {
            controlServidor.mostrarMensajeConsolaServidor("Cliente " + servidor.getNombreUsuario() + " desconectado");
            ControlServidor.setCantidadClientesLogeados(ControlServidor.getCantidadClientesLogeados() - 1);
            controlServidor.removerCliente(this);
        }
    }
}