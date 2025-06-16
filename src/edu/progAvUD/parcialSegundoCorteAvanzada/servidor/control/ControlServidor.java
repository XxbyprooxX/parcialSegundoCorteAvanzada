package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.Servidor;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author Cristianlol789
 */
public class ControlServidor {

    private ControlPrincipal controlPrincipal;

    /**
     * Set que mantiene registro de los usuarios que han iniciado sesión
     */
    private static Set<String> usuariosConectados;

    /**
     * Lista de clientes actualmente conectados al servidor. Cada cliente se
     * representa mediante un hilo de tipo {@link ThreadServidor}.
     */
    private static Vector<ThreadServidor> clientesActivos;

    private static int cantidadClientesLogeados;

    /**
     * Turno actualmente activo en el servidor
     */
    private int turnoActivo = 1;

    /**
     * Número total de pares de cartas en el juego (configurable)
     */
    private int totalPares = 20;

    /**
     * Número de pares encontrados hasta ahora
     */
    private int paresEncontrados = 0;

    /**
     * Constructor que recibe la instancia del controlador principal.
     *
     * @param controlPrincipal Instancia que representa la ventana principal de
     * control del servidor.
     */
    public ControlServidor(ControlPrincipal controlPrincipal) {
        this.controlPrincipal = controlPrincipal;
        clientesActivos = new Vector<>();
        usuariosConectados = new HashSet<>();
        cantidadClientesLogeados = 0;
    }

    /**
     * Método que inicia los sockets del servidor y espera por conexiones de
     * clientes. Por cada pareja de conexiones (dos sockets), se crea un hilo
     * para manejar al cliente. Los clientes conectados se agregan al vector de
     * usuarios activos.
     */
    public void runServer() {
        ServerSocket server1 = null;
        ServerSocket server2 = null;
        boolean listening = true;
        try {
            server1 = new ServerSocket(Servidor.getPUERTO_1());
            server2 = new ServerSocket(Servidor.getPUERTO_2());
            controlPrincipal.mostrarMensajeConsolaServidor(".::Servidor activo :");
            controlPrincipal.mostrarMensajeConsolaServidor("Sistema de turnos iniciado");

            while (listening) {
                Socket socket1 = null;
                Socket socket2 = null;
                try {
                    controlPrincipal.mostrarMensajeConsolaServidor("Esperando Usuarios");
                    socket1 = server1.accept();
                    socket2 = server2.accept();
                } catch (IOException e) {
                    controlPrincipal.mostrarMensajeConsolaServidor("Accept failed: " + server1 + ", " + e.getMessage());
                    continue;
                }

                ThreadServidor usuario = new ThreadServidor(socket1, socket2, this);
                agregarCliente(usuario); // Usar el método con control de turnos
                usuario.start(); // inicia el hilo que manejará la comunicación con este cliente
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Se puede mostrar mensaje en consola si se desea: controlPrincipal.mostrarMensajeConsolaServidor("error :" + e);
        }
    }

    /**
     * Método para agregar un cliente a la lista de conectados con control de
     * turnos
     *
     * @param threadCliente el hilo del cliente a agregar
     */
    public synchronized void agregarCliente(ThreadServidor threadCliente) {
        clientesActivos.add(threadCliente);
        controlPrincipal.mostrarMensajeConsolaServidor("Cliente agregado: " + threadCliente);
        controlPrincipal.mostrarMensajeConsolaServidor("Total de clientes conectados: " + clientesActivos.size());
        controlPrincipal.mostrarMensajeConsolaServidor("Turno asignado: " + threadCliente.getNumeroTurno());
    }

    /**
     * Método para remover un cliente de la lista de conectados
     *
     * @param threadCliente el hilo del cliente a remover
     */
    public synchronized void removerCliente(ThreadServidor threadCliente) {
        clientesActivos.remove(threadCliente);

        // Desregistrar el usuario del control de sesiones
        String nombreUsuario = threadCliente.getServidor().getNombreUsuario();
        desregistrarUsuarioConectado(nombreUsuario);

        controlPrincipal.mostrarMensajeConsolaServidor("Cliente removido: " + threadCliente.getInformacionCliente());
        controlPrincipal.mostrarMensajeConsolaServidor("Total de clientes restantes: " + clientesActivos.size());

        // Si se desconecta el cliente con el turno activo, avanzar al siguiente
        if (threadCliente.getNumeroTurno() == turnoActivo) {
            controlPrincipal.mostrarMensajeConsolaServidor("Cliente con turno activo se desconectó, avanzando turno...");
            avanzarSiguienteTurno();
        }
    }

    /**
     * Obtiene el turno actualmente activo
     *
     * @return el número del turno activo
     */
    public synchronized int getTurnoActivo() {
        return this.turnoActivo;
    }

    /**
     * Registra un usuario como conectado
     *
     * @param usuario nombre del usuario que se conectó
     * @return true si se registró exitosamente, false si ya estaba conectado
     */
    public synchronized boolean registrarUsuarioConectado(String usuario) {
        if (usuariosConectados.contains(usuario)) {
            return false;
        }
        usuariosConectados.add(usuario);
        controlPrincipal.mostrarMensajeConsolaServidor(
                "Usuario '" + usuario + "' registrado como conectado. Total usuarios activos: " + usuariosConectados.size()
        );
        return true;
    }

    /**
     * Desregistra un usuario cuando se desconecta
     *
     * @param usuario nombre del usuario que se desconectó
     */
    public synchronized void desregistrarUsuarioConectado(String usuario) {
        if (!usuario.isBlank() && usuariosConectados.remove(usuario)) {
            controlPrincipal.mostrarMensajeConsolaServidor(
                    "Usuario '" + usuario + "' desconectado. Total usuarios activos: " + usuariosConectados.size()
            );
        }
    }

    /**
     * Obtiene la lista de usuarios actualmente conectados
     *
     * @return Set con los nombres de usuarios conectados
     */
    public synchronized Set<String> getUsuariosConectados() {
        return new HashSet<>(usuariosConectados);
    }

    /**
     * Muestra todos los usuarios conectados en la consola
     */
    public void mostrarUsuariosConectados() {
        controlPrincipal.mostrarMensajeConsolaServidor("=== USUARIOS CONECTADOS ===");
        if (usuariosConectados.isEmpty()) {
            controlPrincipal.mostrarMensajeConsolaServidor("No hay usuarios conectados");
        } else {
            for (String usuario : usuariosConectados) {
                controlPrincipal.mostrarMensajeConsolaServidor("- " + usuario);
            }
        }
        controlPrincipal.mostrarMensajeConsolaServidor("Total: " + usuariosConectados.size());
        controlPrincipal.mostrarMensajeConsolaServidor("==========================");
    }

    /**
     * Avanza al siguiente turno (para uso general)
     */
    public synchronized void avanzarSiguienteTurno() {
        int siguienteTurno = encontrarSiguienteJugadorActivo();
        if (siguienteTurno != -1) {
            this.turnoActivo = siguienteTurno;
            controlPrincipal.mostrarMensajeConsolaServidor("Turno activo actualizado a: " + this.turnoActivo);
            notificarCambioTurno();
        }
    }

    /**
     * Avanza al siguiente turno específicamente para Concentrese Solo avanza
     * cuando el jugador falla, de lo contrario mantiene el turno
     */
    public synchronized void avanzarSiguienteTurnoConcentrese() {
        // Encontrar el siguiente jugador conectado
        int siguienteTurno = encontrarSiguienteJugadorActivo();

        if (siguienteTurno != -1) {
            this.turnoActivo = siguienteTurno;
            controlPrincipal.mostrarMensajeConsolaServidor("Turno de Concentrese pasa a: " + this.turnoActivo);
            notificarCambioTurno();

            // Enviar "pedirCoordenadas" al cliente con el turno activo
            for (ThreadServidor cliente : clientesActivos) {
                if (cliente.getNumeroTurno() == this.turnoActivo) {
                    try {
                        cliente.getServidor().getServidorInformacionSalida1().writeUTF("pedirCoordenadas");
                        cliente.getServidor().getServidorInformacionSalida1().flush();
                    } catch (Exception e) {
                        controlPrincipal.mostrarMensajeConsolaServidor("Error al enviar pedirCoordenadas: " + e.getMessage());
                    }
                    break;
                }
            }
        } else {
            controlPrincipal.mostrarMensajeConsolaServidor("No hay más jugadores activos");
        }
    }

    /**
     * Encuentra el siguiente jugador activo para Concentrese
     *
     * @return número de turno del siguiente jugador, o -1 si no hay más
     */
    private int encontrarSiguienteJugadorActivo() {
        if (clientesActivos.isEmpty()) {
            return -1;
        }

        // Buscar el siguiente turno circular
        int menorTurnoMayor = Integer.MAX_VALUE; // Para wrap-around
        int menorTurnoTotal = Integer.MAX_VALUE; // Para encontrar el menor absoluto

        for (ThreadServidor cliente : clientesActivos) {
            int turnoCliente = cliente.getNumeroTurno();

            // Encontrar el siguiente turno mayor al actual
            if (turnoCliente > this.turnoActivo && turnoCliente < menorTurnoMayor) {
                menorTurnoMayor = turnoCliente;
            }

            // Encontrar el menor turno absoluto (para wrap-around)
            if (turnoCliente < menorTurnoTotal) {
                menorTurnoTotal = turnoCliente;
            }
        }

        // Si hay un turno mayor, usarlo; si no, hacer wrap-around al menor
        return (menorTurnoMayor != Integer.MAX_VALUE) ? menorTurnoMayor : menorTurnoTotal;
    }

    /**
     * Notifica a todos los clientes conectados sobre el cambio de turno
     */
    private void notificarCambioTurno() {
        for (ThreadServidor cliente : clientesActivos) {
            try {
                cliente.gestionarTurnosConcentrese(); // Cambiar al método específico de Concentrese
            } catch (Exception e) {
                controlPrincipal.mostrarMensajeConsolaServidor("Error al notificar cambio de turno a cliente: " + e.getMessage());
            }
        }
    }

    /**
     * Muestra todos los clientes conectados y sus turnos
     */
    public void mostrarClientesConectados() {
        controlPrincipal.mostrarMensajeConsolaServidor("=== CLIENTES CONECTADOS ===");
        controlPrincipal.mostrarMensajeConsolaServidor("Turno activo: " + turnoActivo);

        if (clientesActivos.isEmpty()) {
            controlPrincipal.mostrarMensajeConsolaServidor("No hay clientes conectados");
        } else {
            for (int i = 0; i < clientesActivos.size(); i++) {
                ThreadServidor cliente = clientesActivos.get(i);
                String estado = (cliente.getNumeroTurno() == turnoActivo) ? " [TURNO ACTIVO]" : " [ESPERANDO]";
                controlPrincipal.mostrarMensajeConsolaServidor((i + 1) + ". " + cliente.getInformacionCliente() + estado);
            }
        }
        controlPrincipal.mostrarMensajeConsolaServidor("========================");
    }

    /**
     * Reinicia el sistema de turnos
     */
    public synchronized void reiniciarTurnos() {
        this.turnoActivo = 1;
        controlPrincipal.mostrarMensajeConsolaServidor("Sistema de turnos reiniciado - Turno activo: " + turnoActivo);
        notificarCambioTurno();
    }

    /**
     * Fuerza el avance al siguiente turno (útil para administración manual)
     */
    public void forzarSiguienteTurno() {
        controlPrincipal.mostrarMensajeConsolaServidor("Forzando avance de turno por administrador...");
        avanzarSiguienteTurno();
    }

    /**
     * Establece manualmente un turno específico
     *
     * @param numeroTurno el turno al que se quiere cambiar
     */
    public synchronized void establecerTurno(int numeroTurno) {
        if (numeroTurno > 0) {
            this.turnoActivo = numeroTurno;
            controlPrincipal.mostrarMensajeConsolaServidor("Turno establecido manualmente a: " + numeroTurno);
            notificarCambioTurno();
        } else {
            controlPrincipal.mostrarMensajeConsolaServidor("Error: El número de turno debe ser mayor a 0");
        }
    }

    /**
     * Método mejorado para verificar parejas con mejor logging
     */
    public boolean verificarPareja(int x1, int y1, String carta1, int x2, int y2, String carta2) {

        if (x1 == x2 && y1 == y2) {
            return false;
        }

        boolean esPareja = carta1 != null && carta1.equals(carta2);

        if (esPareja) {
            paresEncontrados++;
            controlPrincipal.mostrarMensajeConsolaServidor(
                    "Progreso: " + paresEncontrados + "/" + totalPares + " pares encontrados"
            );
            controlPrincipal.mostrarMensajeError("No se puede enviar el mensaje");
        } else {
            controlPrincipal.mostrarMensajeConsolaServidor(
                    "No es pareja: '" + carta1 + "' ≠ '" + carta2 + "'"
            );
        }

        controlPrincipal.mostrarMensajeConsolaServidor("=========================");
        return esPareja;
    }

    /**
     * Método auxiliar para validar si las coordenadas están dentro del rango
     * válido (puedes personalizar según tu tablero)
     *
     * @param x coordenada X
     * @param y coordenada Y
     * @return true si las coordenadas son válidas
     */
    public boolean coordenadasValidas(int x, int y) {
        // Aquí puedes definir el tamaño de tu tablero
        // Por ejemplo, para un tablero de 8x5 (40 cartas = 20 pares)
        return x >= 0 && x < 8 && y >= 0 && y < 5;
    }

    /**
     * Verifica si el juego de Concentrese ha terminado (todas las cartas
     * emparejadas)
     *
     * @return true si se encontraron todos los pares
     */
    public boolean verificarJuegoTerminado() {
        return paresEncontrados >= totalPares;
    }

    /**
     * Termina el juego de Concentrese y declara ganadores
     */
    public void terminarJuego() {
        controlPrincipal.mostrarMensajeConsolaServidor("¡JUEGO TERMINADO! Todas las cartas han sido emparejadas");

        // Calcular puntuaciones y determinar ganador
        mostrarResultadosFinales();

        // Notificar a todos los clientes que el juego terminó
        for (ThreadServidor cliente : clientesActivos) {
            try {
                cliente.getServidor().getServidorInformacionSalida1().writeUTF("juegoTerminado");
                cliente.getServidor().getServidorInformacionSalida1().flush();
            } catch (IOException e) {
                controlPrincipal.mostrarMensajeConsolaServidor("Error al notificar fin de juego: " + e.getMessage());
            }
        }
    }

    /**
     * Muestra los resultados finales del juego
     */
    private void mostrarResultadosFinales() {
        controlPrincipal.mostrarMensajeConsolaServidor("=== RESULTADOS FINALES ===");
        controlPrincipal.mostrarMensajeConsolaServidor("Total de pares encontrados: " + paresEncontrados);
        controlPrincipal.mostrarMensajeConsolaServidor("Juego completado exitosamente");
        controlPrincipal.mostrarMensajeConsolaServidor("========================");
    }

    /**
     * Reinicia el juego de Concentrese
     */
    public void reiniciarJuegoConcentrese() {
        paresEncontrados = 0;
        turnoActivo = 1;
        controlPrincipal.mostrarMensajeConsolaServidor("Juego de Concentrese reiniciado");

        // Notificar a todos los clientes
        for (ThreadServidor cliente : clientesActivos) {
            try {
                cliente.getServidor().getServidorInformacionSalida1().writeUTF("juegoReiniciado");
                cliente.getServidor().getServidorInformacionSalida1().flush();
            } catch (IOException e) {
                controlPrincipal.mostrarMensajeConsolaServidor("Error al notificar reinicio: " + e.getMessage());
            }
        }

        notificarCambioTurno();
    }

    /**
     * Establece el número total de pares para el juego
     *
     * @param totalPares número de pares de cartas
     */
    public void configurarTotalPares(int totalPares) {
        if (totalPares > 0) {
            this.totalPares = totalPares;
            controlPrincipal.mostrarMensajeConsolaServidor("Total de pares configurado: " + totalPares);
        }
    }

    /**
     * Obtiene el número de pares encontrados
     *
     * @return número de pares encontrados
     */
    public int getParesEncontrados() {
        return paresEncontrados;
    }

    /**
     * Obtiene el total de pares del juego
     *
     * @return total de pares
     */
    public int getTotalPares() {
        return totalPares;
    }

    /**
     * Envía un mensaje de texto a la consola del servidor.
     *
     * @param mensaje Mensaje que se desea mostrar en la interfaz de la consola.
     */
    public void mostrarMensajeConsolaServidor(String mensaje) {
        controlPrincipal.mostrarMensajeConsolaServidor(mensaje);
    }

    /**
     * Obtiene el vector que contiene todos los hilos de clientes activos
     * conectados al servidor.
     *
     * @return Vector de clientes activos.
     */
    public static Vector<ThreadServidor> getClientesActivos() {
        return clientesActivos;
    }

    /**
     * Reemplaza la lista de clientes activos por otra.
     *
     * @param clientesActivos Nuevo vector de clientes activos.
     */
    public static void setClientesActivos(Vector<ThreadServidor> clientesActivos) {
        ControlServidor.clientesActivos = clientesActivos;
    }

    public void asignarIps(String puerto1, String puerto2) {
        try {
            int puerto1Int = Integer.parseInt(puerto1);
            int puerto2Int = Integer.parseInt(puerto2);
            Servidor.setPUERTO_1(puerto1Int);
            Servidor.setPUERTO_2(puerto2Int);
        } catch (NumberFormatException e) {

        }
    }

    public boolean buscarUsuarioYContrasenaExistente(String usuario, String contrasena) {
        return controlPrincipal.buscarUsuarioYContrasenaExistente(usuario, contrasena);
    }

    /**
     * Obtiene el tipo de carta en una posición específica de la matriz
     *
     * @param x coordenada X (columna)
     * @param y coordenada Y (fila)
     * @return el tipo de carta como String, o null si las coordenadas son
     * inválidas
     */
    public String obtenerTipoCartaEnPosicion(int x, int y) {
        try {
            int[][] matrizCartas = controlPrincipal.getMatrizCartas();
            if (y < 0 || y >= matrizCartas.length || x < 0 || x >= matrizCartas[0].length) {
                return "";
            }
            int valorCarta = matrizCartas[y][x];
            return String.valueOf(valorCarta);

        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Verifica si un usuario ya está conectado
     *
     * @param usuario nombre del usuario a verificar
     * @return true si el usuario ya está conectado, false en caso contrario
     */
    public synchronized boolean usuarioYaConectado(String usuario) {
        return usuariosConectados.contains(usuario);
    }

    public String obtenerJugadorPorCredenciales(String usuario) {
        return controlPrincipal.obtenerJugadorPorCredenciales(usuario);
    }

    public void verificarJugadoresMostrarBotonJugar() {
        if (cantidadClientesLogeados >= 2) {
            controlPrincipal.ocultarBotonIniciarJuego(true);
        } else {
            controlPrincipal.ocultarBotonIniciarJuego(false);
        }
    }

    public static int getCantidadClientesLogeados() {
        return cantidadClientesLogeados;
    }

    public static void setCantidadClientesLogeados(int cantidadClientesLogeados) {
        ControlServidor.cantidadClientesLogeados = cantidadClientesLogeados;
    }

    public void iniciarJuego() {

        for (ThreadServidor cliente : clientesActivos) {
            try {
                cliente.getServidor().getServidorInformacionSalida1().writeUTF("pedirCoordenadas");
                cliente.getServidor().getServidorInformacionSalida1().flush();
            } catch (IOException e) {
                controlPrincipal.mostrarMensajeConsolaServidor("Error al notificar reinicio: " + e.getMessage());
            }
        }
    }
    
    public void seleccionarCarta(int idCarta){
        controlPrincipal.seleccionarCarta(idCarta);
    }
    
    public void deseleccionarCarta(int idCarta){
        controlPrincipal.deseleccionarCarta(idCarta);
    }
}
