package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.JugadorVO;
import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.Servidor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * **`ThreadServidor`**: Un hilo dedicado a gestionar la comunicación y la
 * lógica del juego para un cliente individual conectado al servidor. Cada
 * instancia de esta clase representa la sesión de un jugador y maneja sus
 * acciones durante el juego "Concéntrese".
 *
 * @author Cristianlol789
 */
public class ThreadServidor extends Thread {

    /**
     * Objeto que encapsula los **sockets de comunicación** (entrada/salida) con
     * el cliente.
     */
    private Servidor servidor;

    /**
     * El objeto **`JugadorVO`** asignado a este hilo, que representa al jugador
     * después de un inicio de sesión exitoso.
     */
    private JugadorVO jugadorAsignado;

    /**
     * Una referencia al **`ControlServidor`** principal, permitiendo el acceso
     * a la consola del servidor y a la lista de usuarios conectados.
     */
    private ControlServidor controlServidor;

    /**
     * Un **contador estático** y atómico para asignar turnos únicos y
     * secuenciales a cada cliente conectado.
     */
    private static AtomicInteger contadorTurnos = new AtomicInteger(1);

    /**
     * El **número de turno** específico asignado a este cliente para el juego.
     */
    private int numeroTurno;

    /**
     * Un arreglo para almacenar las **estadísticas del jugador** durante la
     * partida: `estadisticas[0]`: Número total de intentos de voltear cartas.
     * `estadisticas[1]`: Número de parejas acertadas. `estadisticas[2]`:
     * Porcentaje de aciertos (calculado).
     */
    private int[] estadisticas;

    /**
     * Constructor para inicializar un nuevo hilo de servidor para un cliente.
     * Configura la comunicación con los sockets proporcionados y establece las
     * estadísticas iniciales del jugador.
     *
     * @param socketCliente1 El primer socket para la comunicación de
     * entrada/salida principal del cliente.
     * @param socketCliente2 El segundo socket, utilizado para enviar mensajes
     * específicos desde el servidor.
     * @param controlServidor La instancia del controlador principal del
     * servidor.
     */
    public ThreadServidor(Socket socketCliente1, Socket socketCliente2, ControlServidor controlServidor) {
        String nombreUsuario = ""; // El nombre de usuario se establecerá después del login
        this.servidor = new Servidor(socketCliente1, socketCliente2, nombreUsuario);
        this.controlServidor = controlServidor;
        this.estadisticas = new int[3];
        // Inicializa las estadísticas a cero
        estadisticas[0] = 0; // Intentos totales
        estadisticas[1] = 0; // Parejas resueltas
        estadisticas[2] = 0; // Porcentaje de aciertos
    }

    /**
     * Asigna un objeto `JugadorVO` a este hilo después de una autenticación
     * exitosa del usuario. Intenta recuperar la información del jugador de la
     * base de datos o lista de jugadores del servidor.
     *
     * @param usuario El nombre de usuario utilizado para buscar el jugador.
     * @return `true` si el jugador se asignó correctamente, `false` en caso
     * contrario (ej. usuario no encontrado).
     */
    private boolean asignarJugador(String usuario) {
        try {
            // Se asume que obtenerJugadorPorCredenciales devuelve una cadena formateada como "nombre,apellido,usuario,contraseña"
            String[] infoJugador = controlServidor.obtenerJugadorPorCredenciales(usuario).split(",");

            if (infoJugador != null && infoJugador.length >= 4) { // Asegurarse de que la cadena tiene suficientes partes
                // Se crea el objeto JugadorVO con la información obtenida
                this.jugadorAsignado = new JugadorVO(infoJugador[0], infoJugador[1], infoJugador[2], infoJugador[3], 0, 0, 0);
                // Reiniciar las estadísticas de la partida para el nuevo jugador asignado
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
     * Asigna un **turno único y consecutivo** a cada cliente conectado. Utiliza
     * un `AtomicInteger` para garantizar la asignación segura de turnos en
     * entornos concurrentes.
     *
     * @return El número de turno asignado a este cliente.
     */
    private synchronized int asignarTurno() {
        int turno = contadorTurnos.getAndIncrement();
        controlServidor.mostrarMensajeConsolaServidor(
                "Cliente " + servidor.getNombreUsuario() + " conectado - Turno asignado: " + turno
        );
        return turno;
    }

    /**
     * Muestra información relevante sobre el cliente y su turno en la consola
     * del servidor. Esto incluye el nombre del cliente, su turno asignado, el
     * turno activo global y si es su turno actualmente.
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
     * Envía el **turno activo actual** del juego a este cliente. Esto permite
     * al cliente saber si es su turno o el de otro jugador.
     */
    public void verificarTurnoActivo() {
        int turnoActivo = controlServidor.getTurnoActivo();

        try {
            DataOutputStream salida1 = this.servidor.getServidorInformacionSalida1();
            if (salida1 != null) {
                salida1.writeInt(turnoActivo);
                salida1.flush();
            }
        } catch (IOException ex) {
            controlServidor.mostrarMensajeConsolaServidor("Error al enviar turno activo a " + servidor.getNombreUsuario() + ": " + ex.getMessage());
        }
    }

    /**
     * Maneja la lógica cuando el jugador acierta una pareja en el juego
     * "Concéntrese". El jugador mantiene su turno y se actualizan sus
     * estadísticas.
     */
    public void manejarAcierto() {
        try {
            // Actualizar estadísticas del jugador
            if (jugadorAsignado != null) {
                jugadorAsignado.setCantidadParejasResueltas(jugadorAsignado.getCantidadParejasResueltas() + 1);
                // El número de intentos también se incrementa con cada jugada, independientemente de si acierta o falla
                jugadorAsignado.setCantidadIntentos(jugadorAsignado.getCantidadIntentos() + 1);
            }

            String nombreMostrar = (jugadorAsignado != null) ? jugadorAsignado.getNombreJugador() : servidor.getNombreUsuario();
            controlServidor.mostrarMensajeConsolaServidor(
                    "¡" + nombreMostrar + " acertó! Mantiene su turno #" + this.numeroTurno
                    + " | Parejas resueltas: " + (jugadorAsignado != null ? jugadorAsignado.getCantidadParejasResueltas() : "N/A")
            );

            // Notificar al cliente que ha acertado
            DataOutputStream salida1 = this.servidor.getServidorInformacionSalida1();
            if (salida1 != null) {
                salida1.writeUTF("acerto");
                salida1.flush();
            }

            // Verificar si el juego ha terminado después del acierto
            if (controlServidor.verificarJuegoTerminado()) {
                controlServidor.terminarJuego();
            }

        } catch (IOException e) {
            controlServidor.mostrarMensajeConsolaServidor(
                    "Error al manejar acierto para " + servidor.getNombreUsuario() + ": " + e.getMessage()
            );
        }
    }

    /**
     * Maneja la lógica cuando el jugador falla en encontrar una pareja en
     * "Concéntrese". El turno pasa al siguiente jugador y se actualizan las
     * estadísticas.
     *
     * @param razon La razón por la cual la jugada resultó en un fallo.
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

            // Notificar al cliente que falló, incluyendo la razón
            DataOutputStream salida1 = this.servidor.getServidorInformacionSalida1();
            if (salida1 != null) {
                salida1.writeUTF("fallo," + razon);
                salida1.flush();
            }
            
            // Avanzar al siguiente turno
            controlServidor.avanzarSiguienteTurnoConcentrese();

        } catch (IOException e) {
            controlServidor.mostrarMensajeConsolaServidor(
                    "Error al manejar fallo para " + servidor.getNombreUsuario() + ": " + e.getMessage()
            );
        }
    }

    /**
     * Procesa la selección de una carta por parte del jugador en el juego
     * "Concéntrese". Verifica si la carta ya está emparejada y devuelve el tipo
     * de carta.
     *
     * @param x1 Coordenada X (columna) de la carta seleccionada.
     * @param y1 Coordenada Y (fila) de la carta seleccionada.
     * @return El tipo de carta en la posición especificada, o "emparejada" si
     * ya lo está.
     */
    public String procesarJugadaConcentrese(int x1, int y1) {
        // Las coordenadas del cliente suelen ser 1-based, mientras que los arrays son 0-based
        if (controlServidor.esCartaYaEmparejada(x1 - 1, y1 - 1)) {
            return "emparejada";
        }
        String tipoCarta1 = controlServidor.obtenerTipoCartaEnPosicion(x1 - 1, y1 - 1);
        return tipoCarta1;
    }

    /**
     * Compara dos cartas seleccionadas por el jugador para determinar si forman
     * una pareja. Gestiona el acierto o el fallo, actualiza las estadísticas y
     * el turno.
     *
     * @param tipoCarta1 El tipo de la primera carta seleccionada.
     * @param tipoCarta2 El tipo de la segunda carta seleccionada.
     * @param x1 Coordenada X de la primera carta.
     * @param y1 Coordenada Y de la primera carta.
     * @param x2 Coordenada X de la segunda carta.
     * @param y2 Coordenada Y de la segunda carta.
     */
    public void compararCartas(String tipoCarta1, String tipoCarta2, int x1, int y1, int x2, int y2) {

        estadisticas[0] = estadisticas[0] + 1;

        if (tipoCarta1.equals("") || tipoCarta2.equals("")) {
            // Esto podría indicar coordenadas fuera de rango o un error en la obtención del tipo de carta
            manejarFallo("Coordenadas fuera de rango o error al obtener tipo de carta.");
            actualizarPorcentajeAciertos();
            return;
        }

        boolean esPareja = controlServidor.verificarPareja(x1, y1, tipoCarta1, x2, y2, tipoCarta2);

        if (esPareja) {
            estadisticas[1] = estadisticas[1] + 1; // Incrementar parejas acertadas
            actualizarPorcentajeAciertos();
            manejarAcierto();
        } else {
            try {
                // Pequeña pausa para que el cliente pueda ver las cartas antes de que se volteen de nuevo
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restaurar el estado de interrupción
                controlServidor.mostrarMensajeConsolaServidor("Hilo interrumpido durante la espera de fallo.");
            }
            // Deseleccionar (voltear) las cartas si no son pareja
            // Asegurarse de que las coordenadas estén dentro del rango antes de deseleccionar
            if (x1 >= 1 && x1 <= 8 && y1 >= 1 && y1 <= 5) {
                int idCarta1 = (y1 - 1) * 8 + (x1 - 1);
                controlServidor.deseleccionarCarta(idCarta1);
            }
            if (x2 >= 1 && x2 <= 8 && y2 >= 1 && y2 <= 5) {
                int idCarta2 = (y2 - 1) * 8 + (x2 - 1);
                controlServidor.deseleccionarCarta(idCarta2);
            }
            actualizarPorcentajeAciertos();
            manejarFallo("Las cartas seleccionadas no forman una pareja.");
        }
        controlServidor.actualizarPanelEstadisticas(this);
    }

    /**
     * Calcula y actualiza el porcentaje de aciertos del jugador. Se almacena en
     * `estadisticas[2]`. Si no hay intentos (`estadisticas[0] == 0`), el
     * porcentaje es 0 para evitar división por cero.
     */
    public void actualizarPorcentajeAciertos() {
        if (estadisticas[0] > 0) {
            estadisticas[2] = Math.round(((float) estadisticas[1] / estadisticas[0]) * 100);
        } else {
            estadisticas[2] = 0; // Si no hay intentos, el porcentaje de aciertos es 0
        }
    }

    /**
     * Obtiene una cadena de texto con información relevante del cliente
     * conectado.
     *
     * @return Una `String` que contiene el nombre de usuario, el número de
     * turno y la dirección IP del cliente.
     */
    public String getInformacionCliente() {
        return "Cliente: " + servidor.getNombreUsuario()
                + " | Turno: " + this.numeroTurno
                + " | IP: " + servidor.getServidorCliente1().getInetAddress().getHostAddress();
    }

    /**
     * Devuelve el número de turno asignado a este cliente.
     *
     * @return El número de turno.
     */
    public int getNumeroTurno() {
        return this.numeroTurno;
    }

    /**
     * Devuelve el objeto `Servidor` asociado a este hilo, que contiene los
     * sockets de comunicación.
     *
     * @return El objeto `Servidor`.
     */
    public Servidor getServidor() {
        return this.servidor;
    }

    /**
     * Retorna las estadísticas actuales del jugador asociado a este hilo.
     *
     * @return Un arreglo de enteros donde: `[0]` es el total de intentos, `[1]`
     * es el número de parejas resueltas, `[2]` es el porcentaje de aciertos.
     */
    public int[] getEstadisticas() {
        return estadisticas;
    }

    /**
     * El método **`run()`** es el punto de entrada principal para la ejecución
     * del hilo. Establece los flujos de entrada y salida de datos con el
     * cliente y entra en un bucle infinito para escuchar los comandos enviados
     * por el cliente y procesarlos.
     *
     * Los comandos manejados incluyen: - **`eleccionJugador`**: Procesa la
     * selección de cartas de un jugador en el juego "Concéntrese". -
     * **`consultarTurno`**: Permite al cliente verificar si es su turno. -
     * **`login`**: Autentica al usuario y le asigna un turno si el login es
     * exitoso. - **`pedirDatosJugador`**: Envía al cliente las estadísticas
     * actuales del jugador. - **`pedirGanador`**: Envía la información del
     * ganador del juego. - **`siguienteTurno`**: Inicia el juego (probablemente
     * después de que todos los jugadores se hayan conectado).
     *
     * Si la conexión con el cliente se interrumpe (`IOException`), se muestra
     * un mensaje en la consola, se reduce el contador de clientes conectados,
     * se verifica la posibilidad de mostrar el botón de jugar y se remueve al
     * cliente de la lista de clientes activos del servidor.
     */
    @Override
    public void run() {
        controlServidor.mostrarMensajeConsolaServidor(".::Esperando Mensajes del cliente (" + servidor.getNombreUsuario() + ") ::.");
        try {
            // Configurar los flujos de entrada y salida de datos
            DataInputStream entrada = new DataInputStream(this.servidor.getServidorCliente1().getInputStream());
            this.servidor.setServidorInformacionEntrada1(entrada);
            DataOutputStream salida1 = new DataOutputStream(this.servidor.getServidorCliente1().getOutputStream());
            this.servidor.setServidorInformacionSalida1(salida1);

            // Bucle principal para escuchar y procesar mensajes del cliente
            while (true) {
                String mensaje = entrada.readUTF(); // Lee el mensaje del cliente
                String[] partes = mensaje.split(","); // Divide el mensaje en partes por coma
                String comando = partes[0]; // El primer elemento es el comando

                switch (comando) {
                    case "eleccionJugador":
                        // Actualiza el panel de estadísticas en la interfaz del servidor
                        controlServidor.actualizarPanelEstadisticas(this);
                        try {
                            // Primera carta seleccionada
                            int x1 = Integer.parseInt(partes[1]);
                            int y1 = Integer.parseInt(partes[2]);
                            String tipoCarta1 = procesarJugadaConcentrese(x1, y1);

                            // Lee el segundo mensaje para la segunda carta seleccionada
                            mensaje = entrada.readUTF();
                            partes = mensaje.split(",");

                            // Segunda carta seleccionada
                            int x2 = Integer.parseInt(partes[1]);
                            int y2 = Integer.parseInt(partes[2]);
                            String tipoCarta2 = procesarJugadaConcentrese(x2, y2);

                            // Selecciona las cartas en el modelo del servidor (las voltea visiblemente)
                            // Se asume que las coordenadas son 1-based desde el cliente, se ajustan a 0-based para el array
                            if (x1 >= 1 && x1 <= 8 && y1 >= 1 && y1 <= 5) {
                                int idCarta1 = (y1 - 1) * 8 + (x1 - 1);
                                controlServidor.seleccionarCarta(idCarta1);
                            }
                            if (x2 >= 1 && x2 <= 8 && y2 >= 1 && y2 <= 5) {
                                int idCarta2 = (y2 - 1) * 8 + (x2 - 1);
                                controlServidor.seleccionarCarta(idCarta2);
                            }

                            // Si alguna de las cartas ya estaba emparejada, manejar como fallo
                            if (tipoCarta1.equals("emparejada") || tipoCarta2.equals("emparejada")) {
                                // Deseleccionar las cartas que sí se pudieron seleccionar para evitar que se queden visibles
                                if (!tipoCarta1.equals("emparejada") && x1 >= 1 && x1 <= 8 && y1 >= 1 && y1 <= 5) {
                                    int idCarta1ToDeselect = (y1 - 1) * 8 + (x1 - 1);
                                    controlServidor.deseleccionarCarta(idCarta1ToDeselect);
                                }
                                if (!tipoCarta2.equals("emparejada") && x2 >= 1 && x2 <= 8 && y2 >= 1 && y2 <= 5) {
                                    int idCarta2ToDeselect = (y2 - 1) * 8 + (x2 - 1);
                                    controlServidor.deseleccionarCarta(idCarta2ToDeselect);
                                }
                                manejarFallo("Una o ambas cartas ya estaban emparejadas.");
                                break; // Salir del switch para esperar el siguiente comando
                            }

                            // Compara las cartas y gestiona el acierto o fallo
                            compararCartas(tipoCarta1, tipoCarta2, x1, y1, x2, y2);
                            controlServidor.actualizarPanelEstadisticas(this); // Actualiza estadísticas después de la jugada

                        } catch (NumberFormatException e) {
                            manejarFallo("Se esperaba un número para las coordenadas, pero se recibió texto.");
                            controlServidor.mostrarMensajeConsolaServidor("Error de formato numérico en coordenadas del cliente " + servidor.getNombreUsuario() + ": " + e.getMessage());
                        }
                        break;

                    case "consultarTurno":
                        // Si es el turno de este cliente, actualiza el panel de estadísticas
                        if (controlServidor.getTurnoActivo() == numeroTurno) {
                            controlServidor.actualizarPanelEstadisticas(this);
                        }
                        verificarTurnoActivo(); // Envía el turno activo al cliente
                        break;

                    case "login":
                        String usuario = partes[1];
                        String contrasena = partes[2];

                        // Paso 1: Verificar si el usuario ya está conectado al servidor
                        if (controlServidor.usuarioYaConectado(usuario)) {
                            controlServidor.mostrarMensajeConsolaServidor(
                                    "Intento de login fallido para '" + usuario + "': El usuario ya está conectado."
                            );
                            salida1.writeUTF("yaConectado"); // Notifica al cliente que ya está conectado
                            salida1.flush();
                            break; // Sale del switch
                        }

                        // Paso 2: Verificar las credenciales (usuario y contraseña)
                        boolean jugadorExiste = controlServidor.buscarUsuarioYContrasenaExistente(usuario, contrasena);

                        if (jugadorExiste && asignarJugador(usuario)) {
                            // Paso 3: Intentar registrar al usuario como conectado
                            if (controlServidor.registrarUsuarioConectado(usuario)) {
                                servidor.setNombreUsuario(usuario); // Establece el nombre de usuario en el objeto Servidor
                                this.numeroTurno = asignarTurno(); // Asigna un turno único
                                salida1.writeUTF("valido"); // Notifica al cliente que el login fue exitoso
                                salida1.flush();

                                controlServidor.mostrarMensajeConsolaServidor(
                                        "Login exitoso para usuario: " + usuario + " (Turno: " + this.numeroTurno + ")"
                                );
                                gestionarTurnosConcentrese(); // Muestra información del turno en consola

                                // Incrementa el contador de clientes logeados y verifica si se puede iniciar el juego
                                ControlServidor.setCantidadClientesLogeados(ControlServidor.getCantidadClientesLogeados() + 1);

                                salida1.writeInt(numeroTurno); // Envía el número de turno asignado al cliente
                                salida1.flush();
                                controlServidor.verificarJugadoresMostrarBotonJugar(); // Permite al servidor decidir si mostrar el botón de jugar
                            } else {
                                // Esto ocurriría si hay una condición de carrera o un error lógico
                                controlServidor.mostrarMensajeConsolaServidor(
                                        "Error: Usuario '" + usuario + "' ya estaba registrado como conectado inesperadamente."
                                );
                                salida1.writeUTF("yaConectado");
                                salida1.flush();
                            }
                        } else {
                            // Login fallido por credenciales incorrectas
                            controlServidor.mostrarMensajeConsolaServidor(
                                    "Login fallido: Credenciales incorrectas para usuario: " + usuario
                            );
                            salida1.writeUTF("invalido"); // Notifica al cliente que las credenciales son inválidas
                            salida1.flush();
                        }
                        break;

                    case "pedirDatosJugador":
                        // Envía las estadísticas actuales del jugador al cliente
                        salida1.writeUTF("" + estadisticas[0] + "," + estadisticas[1] + "," + estadisticas[2]);
                        break;

                    case "pedirGanador":
                        // Solicita al controlador principal la información del ganador y la envía al cliente
                        String infoGanador = controlServidor.enviarGanador();
                        salida1.writeUTF(infoGanador);
                        break;

                    case "siguienteTurno":
                        // Inicia el juego, presumiblemente después de que todos los jugadores se han unido y el administrador lo ha indicado
                        controlServidor.iniciarJuego();
                        break;

                    default:
                        controlServidor.mostrarMensajeConsolaServidor("Comando desconocido recibido de " + servidor.getNombreUsuario() + ": " + comando);
                        break;
                }
            }
        } catch (IOException e) {
            // Manejo de la desconexión del cliente
            controlServidor.mostrarMensajeConsolaServidor("Cliente " + servidor.getNombreUsuario() + " desconectado. Error: " + e.getMessage());
            // Reduce el contador de clientes logeados y actualiza el estado del servidor
            ControlServidor.setCantidadClientesLogeados(ControlServidor.getCantidadClientesLogeados() - 1);
            controlServidor.verificarJugadoresMostrarBotonJugar(); // Vuelve a verificar si el botón de jugar debe estar visible
            controlServidor.removerCliente(this); // Remueve este hilo (cliente) de la lista de clientes activos
        } finally {
            // Asegurarse de cerrar los sockets en caso de una desconexión o error
            try {
                if (servidor.getServidorCliente1() != null && !servidor.getServidorCliente1().isClosed()) {
                    servidor.getServidorCliente1().close();
                }
                if (servidor.getServidorCliente2() != null && !servidor.getServidorCliente2().isClosed()) {
                    servidor.getServidorCliente2().close();
                }
            } catch (IOException e) {
                controlServidor.mostrarMensajeConsolaServidor("Error al cerrar sockets del cliente " + servidor.getNombreUsuario() + ": " + e.getMessage());
            }
        }
    }
}