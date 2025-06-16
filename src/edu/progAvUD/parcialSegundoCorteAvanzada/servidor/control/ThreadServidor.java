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
    private static AtomicInteger contadorTurnos = new AtomicInteger(1);

    /**
     * Número de turno asignado a este cliente
     */
    private int numeroTurno;

    private int[] estadisticas;

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
        this.estadisticas = new int[3];
        estadisticas[0] = 0;
        estadisticas[1] = 0;
        estadisticas[2] = 0;
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
    private synchronized int asignarTurno() {
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

        try {
            DataOutputStream salida1 = this.servidor.getServidorInformacionSalida1();
            salida1.writeInt(turnoActivo);
            salida1.flush();
        } catch (IOException ex) {

        }
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

            salida1.writeUTF("pedirCoordenadas");
            salida1.flush();

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
     *
     * @param razon es el motivo por el cual fallo
     */
    public void manejarFallo(String razon) {
        try {
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
                salida1.writeUTF("fallo," + razon);
                salida1.flush();
            }

            controlServidor.avanzarSiguienteTurnoConcentrese();
            salida1.writeUTF("pedirCoordenadas");
            salida1.flush();

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
     * @return devuelve la carta
     */
    public String procesarJugadaConcentrese(int x1, int y1) {

        String tipoCarta1 = controlServidor.obtenerTipoCartaEnPosicion(x1 - 1, y1 - 1);
        return tipoCarta1;
    }

    public void compararCartas(String tipoCarta1, String tipoCarta2, int x1, int y1, int x2, int y2) {

        estadisticas[0] = estadisticas[0] + 1;

        if (tipoCarta1.equals("") || tipoCarta2.equals("")) {
            manejarFallo("Estas coordenadas estaban fuera del rango");
            actualizarPorcentajeAciertos();
            return;
        }

        boolean esPareja = controlServidor.verificarPareja(x1, y1, tipoCarta1, x2, y2, tipoCarta2);

        if (esPareja) {
            estadisticas[1] = estadisticas[1] + 1;
            actualizarPorcentajeAciertos();
            manejarAcierto();
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (x1 >= 1 && x1 <= 8 && y1 >= 1 && y1 <= 5) {
                int idCarta1 = (y1 - 1) * 8 + (x1 - 1);
                controlServidor.deseleccionarCarta(idCarta1);
            }
            if (x2 >= 1 && x2 <= 8 && y2 >= 1 && y2 <= 5) {
                int idCarta2 = (y2 - 1) * 8 + (x2 - 1);
                controlServidor.deseleccionarCarta(idCarta2);
            }
            actualizarPorcentajeAciertos();
            manejarFallo("No son parejas");
        }
    }

    public void actualizarPorcentajeAciertos() {
        estadisticas[2] = Math.round((estadisticas[1] / estadisticas[0]) * 100);
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
                        controlServidor.actualizarPanelEstadisticas(this);
                        try {
                            System.out.println("Comando: " + comando);
                            int x1 = Integer.parseInt(partes[1]);
                            int y1 = Integer.parseInt(partes[2]);
                            System.out.println("Cordenadas de la carta 1: " + x1 + ", " + y1);

                            String tipoCata1 = procesarJugadaConcentrese(x1, y1);

                            if (x1 >= 1 && x1 <= 8 && y1 >= 1 && y1 <= 5) {
                                int idCarta1 = (y1 - 1) * 8 + (x1 - 1);
                                controlServidor.seleccionarCarta(idCarta1);
                            }

                            mensaje = entrada.readUTF();
                            partes = mensaje.split(",");

                            System.out.println("Mensaje: " + mensaje);
                            int x2 = Integer.parseInt(partes[1]);
                            int y2 = Integer.parseInt(partes[2]);
                            System.out.println("Cordenadas de la carta 2: " + x2 + ", " + y2);
                            String tipoCata2 = procesarJugadaConcentrese(x2, y2);

                            if (x2 >= 1 && x2 <= 8 && y2 >= 1 && y2 <= 5) {
                                int idCarta2 = (y2 - 1) * 8 + (x2 - 1);
                                controlServidor.seleccionarCarta(idCarta2);
                            }

                            compararCartas(tipoCata1, tipoCata2, x1, y1, x2, y2);
                            controlServidor.actualizarPanelEstadisticas(this);
                        } catch (NumberFormatException e) {
                            manejarFallo("Escribio letras en vez de numeros");
                        }

                        break;

                    case "consultarTurno":
                        if (controlServidor.getTurnoActivo() == numeroTurno){
                            controlServidor.actualizarPanelEstadisticas(this);
                        }
                        verificarTurnoActivo();
                        break;

                    case "login":
                        String usuario = partes[1];
                        String contrasena = partes[2];

                        // Primero verificar si el usuario ya está conectado
                        if (controlServidor.usuarioYaConectado(usuario)) {
                            controlServidor.mostrarMensajeConsolaServidor(
                                    "Intento de login fallido: Usuario '" + usuario + "' ya está conectado"
                            );
                            salida1.writeUTF("yaConectado");
                            salida1.flush();
                            break;
                        }

                        // Verificar credenciales
                        boolean jugadorExiste = controlServidor.buscarUsuarioYContrasenaExistente(usuario, contrasena);

                        if (jugadorExiste && asignarJugador(usuario)) {
                            // Intentar registrar el usuario como conectado
                            if (controlServidor.registrarUsuarioConectado(usuario)) {
                                servidor.setNombreUsuario(usuario);
                                this.numeroTurno = asignarTurno();

                                salida1.writeUTF("valido");
                                salida1.flush();

                                controlServidor.mostrarMensajeConsolaServidor(
                                        "Login exitoso para usuario: " + usuario + " (Turno: " + this.numeroTurno + ")"
                                );

                                gestionarTurnosConcentrese();
                                ControlServidor.setCantidadClientesLogeados(ControlServidor.getCantidadClientesLogeados() + 1);
                                salida1.writeInt(numeroTurno);
                                salida1.flush();
                                controlServidor.verificarJugadoresMostrarBotonJugar();

                            } else {
                                controlServidor.mostrarMensajeConsolaServidor(
                                        "Error: Usuario '" + usuario + "' ya estaba registrado como conectado"
                                );
                                salida1.writeUTF("yaConectado");
                                salida1.flush();
                            }
                        } else {
                            controlServidor.mostrarMensajeConsolaServidor(
                                    "Login fallido: Credenciales incorrectas para usuario: " + usuario
                            );
                            salida1.writeUTF("invalido");
                            salida1.flush();
                        }
                        break;
                    case "pedirDatosJugador":
                        salida1.writeUTF("" + estadisticas[0] + "," + estadisticas[1] + "," + estadisticas[2]);
                }
            }

        } catch (IOException e) {
            controlServidor.mostrarMensajeConsolaServidor("Cliente " + servidor.getNombreUsuario() + " desconectado");
            ControlServidor.setCantidadClientesLogeados(ControlServidor.getCantidadClientesLogeados() - 1);
            controlServidor.verificarJugadoresMostrarBotonJugar();
            controlServidor.removerCliente(this);
        }
    }

    public int[] getEstadisticas() {
        return estadisticas;
    }

}
