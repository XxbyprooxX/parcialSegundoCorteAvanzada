package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.Servidor;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * La clase `ControlServidor` gestiona la lógica principal del servidor para el
 * juego de Concentrese (Memory Game). Se encarga de la gestión de clientes, el
 * sistema de turnos, el estado del juego (cartas emparejadas, progreso), y la
 * comunicación con la interfaz de usuario del servidor a través de
 * {@link ControlPrincipal}.
 *
 * @author Cristianlol789
 */
public class ControlServidor {

    private ControlPrincipal controlPrincipal;

    /**
     * Un conjunto estático para almacenar las coordenadas de las cartas que ya
     * han sido emparejadas exitosamente. Las coordenadas se guardan como
     * cadenas de texto en formato "x,y".
     */
    private static Set<String> cartasEmparejadas;

    /**
     * Conjunto que mantiene un registro de los nombres de usuario de los
     * clientes que han iniciado sesión y están actualmente conectados al
     * servidor.
     */
    private static Set<String> usuariosConectados;

    /**
     * Vector (una lista dinámica y segura para hilos) que contiene todos los
     * hilos de clientes activos conectados al servidor. Cada cliente está
     * representado por una instancia de {@link ThreadServidor}.
     */
    private static Vector<ThreadServidor> clientesActivos;

    /**
     * Contador estático que lleva el número de clientes que han completado el
     * proceso de inicio de sesión.
     */
    private static int cantidadClientesLogeados;

    /**
     * El número de turno que está activo actualmente en el juego.
     */
    private int turnoActivo;

    /**
     * El número total de pares de cartas que existen en el juego. Este valor es
     * configurable.
     */
    private int totalPares;

    /**
     * El número de pares de cartas que han sido encontrados y emparejados hasta
     * el momento en el juego actual.
     */
    private int paresEncontrados;

    /**
     * Constructor de la clase `ControlServidor`. Inicializa las colecciones y
     * el estado inicial del juego.
     *
     * @param controlPrincipal La instancia del controlador principal, que
     * representa la ventana principal de control del servidor y su interfaz de
     * usuario.
     */
    public ControlServidor(ControlPrincipal controlPrincipal) {
        this.controlPrincipal = controlPrincipal;
        clientesActivos = new Vector<>();
        usuariosConectados = new HashSet<>();
        cartasEmparejadas = new HashSet<>();
        cantidadClientesLogeados = 0;
        totalPares = 20;
        paresEncontrados = 0;
        turnoActivo = 1;
    }

    /**
     * Inicia el servidor, abriendo sockets en puertos predefinidos y esperando
     * por conexiones de clientes. Por cada par de conexiones de socket que se
     * aceptan, se crea un nuevo hilo {@link ThreadServidor} para manejar la
     * comunicación con el cliente y se añade a la lista de clientes activos.
     */
    public void runServer() {
        ServerSocket server1 = null;
        ServerSocket server2 = null;
        boolean listening = true; // Indica si el servidor debe seguir escuchando nuevas conexiones.
        try {
            // Inicializa los ServerSockets en los puertos definidos en la clase Servidor.
            server1 = new ServerSocket(Servidor.getPUERTO_1());
            server2 = new ServerSocket(Servidor.getPUERTO_2());
            controlPrincipal.mostrarMensajeConsolaServidor(".::Servidor activo :");
            controlPrincipal.mostrarMensajeConsolaServidor("Sistema de turnos iniciado");

            while (listening) {
                Socket socket1 = null;
                Socket socket2 = null;
                try {
                    controlPrincipal.mostrarMensajeConsolaServidor("Esperando Usuarios");
                    // Espera y acepta conexiones de dos sockets para cada nuevo cliente.
                    socket1 = server1.accept();
                    socket2 = server2.accept();
                } catch (IOException e) {
                    controlPrincipal.mostrarMensajeConsolaServidor("Accept failed: " + server1 + ", " + e.getMessage());
                    continue; // Continúa el bucle para seguir esperando conexiones a pesar del error.
                }

                // Crea un nuevo hilo para manejar al cliente con los sockets aceptados.
                ThreadServidor usuario = new ThreadServidor(socket1, socket2, this);
                agregarCliente(usuario); // Agrega el cliente a la lista activa y asigna el turno.
                usuario.start(); // Inicia el hilo para que comience a manejar la comunicación con el cliente.
            }
        } catch (IOException e) {
            // Se puede mostrar mensaje en consola si se desea: controlPrincipal.mostrarMensajeConsolaServidor("error :" + e);
        }
    }

    /**
     * Agrega un nuevo cliente a la lista de clientes conectados y registrados.
     * Este método es sincronizado para asegurar la seguridad de los hilos al
     * modificar la colección compartida.
     *
     * @param threadCliente El hilo {@link ThreadServidor} que representa al
     * cliente a agregar.
     */
    public synchronized void agregarCliente(ThreadServidor threadCliente) {
        clientesActivos.add(threadCliente);
        controlPrincipal.mostrarMensajeConsolaServidor("Cliente agregado: " + threadCliente);
        controlPrincipal.mostrarMensajeConsolaServidor("Total de clientes conectados: " + clientesActivos.size());
        controlPrincipal.mostrarMensajeConsolaServidor("Turno asignado: " + threadCliente.getNumeroTurno());
    }

    /**
     * Verifica si una carta en las coordenadas dadas ya ha sido emparejada y
     * volteada. Este método es sincronizado para manejar el acceso concurrente
     * al conjunto de cartas emparejadas.
     *
     * @param x La coordenada X (columna) de la carta.
     * @param y La coordenada Y (fila) de la carta.
     * @return `true` si la carta ya está emparejada, `false` en caso contrario.
     */
    public synchronized boolean esCartaYaEmparejada(int x, int y) {
        String coordenada = x + "," + y;
        return cartasEmparejadas.contains(coordenada);
    }

    /**
     * Remueve un cliente de la lista de clientes activos y desregistra su
     * nombre de usuario. Si el cliente desconectado tenía el turno activo, el
     * juego avanza automáticamente al siguiente turno. Este método es
     * sincronizado para asegurar la seguridad de los hilos.
     *
     * @param threadCliente El hilo {@link ThreadServidor} del cliente a
     * remover.
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

        if (clientesActivos.size() == 1) {
            controlPrincipal.mostrarMensajeError("Solo queda un jugador");
            System.exit(0);
        }
    }

    /**
     * Obtiene el número del turno que está activo actualmente en el juego. Este
     * método es sincronizado para garantizar que se devuelva el valor más
     * reciente y consistente.
     *
     * @return El número entero del turno activo.
     */
    public synchronized int getTurnoActivo() {
        return this.turnoActivo;
    }

    /**
     * Registra un usuario como conectado al servidor. Este método es
     * sincronizado para evitar condiciones de carrera al modificar el conjunto
     * de usuarios conectados.
     *
     * @param usuario El nombre de usuario que se va a registrar.
     * @return `true` si el usuario se registró exitosamente (es decir, no
     * estaba conectado previamente), `false` si el usuario ya estaba conectado.
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
     * Desregistra un usuario de la lista de conectados cuando se desconecta del
     * servidor. Este método es sincronizado para manejar el acceso concurrente
     * al conjunto de usuarios conectados.
     *
     * @param usuario El nombre de usuario que se desconectó.
     */
    public synchronized void desregistrarUsuarioConectado(String usuario) {
        // Verifica que el nombre de usuario no esté en blanco y que realmente se elimine del conjunto.
        if (!usuario.isBlank() && usuariosConectados.remove(usuario)) {
            controlPrincipal.mostrarMensajeConsolaServidor(
                    "Usuario '" + usuario + "' desconectado. Total usuarios activos: " + usuariosConectados.size()
            );
        }
    }

    /**
     * Obtiene una copia (HashSet) de la lista de nombres de usuarios que están
     * actualmente conectados al servidor. Este método es sincronizado para
     * asegurar la consistencia al acceder a la colección de usuarios
     * conectados.
     *
     * @return Un {@link Set<String>} que contiene los nombres de usuarios
     * conectados.
     */
    public synchronized Set<String> getUsuariosConectados() {
        return new HashSet<>(usuariosConectados);
    }

    /**
     * Muestra en la consola del servidor una lista de todos los usuarios
     * actualmente conectados, incluyendo el total de usuarios.
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
     * Avanza el sistema de turnos al siguiente jugador activo en el juego. Este
     * método es sincronizado para gestionar el cambio de turno de forma segura.
     * Si encuentra un siguiente jugador, actualiza el `turnoActivo` y notifica
     * a todos los clientes.
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
     * Avanza al siguiente turno específicamente para el juego de Concentrese.
     * Después de avanzar el turno, envía un mensaje "pedirCoordenadas" al
     * cliente cuyo turno está activo, para que este cliente pueda realizar su
     * jugada. Este método es sincronizado para controlar el flujo de turnos.
     */
    public synchronized void avanzarSiguienteTurnoConcentrese() {
        // Encontrar el siguiente jugador conectado
        int siguienteTurno = encontrarSiguienteJugadorActivo();

        if (siguienteTurno != -1) {
            this.turnoActivo = siguienteTurno;
            controlPrincipal.mostrarMensajeConsolaServidor("Turno de Concentrese pasa a: " + this.turnoActivo);
            notificarCambioTurno();

            for (ThreadServidor cliente : clientesActivos) {

                if (cliente.getNumeroTurno() == turnoActivo) {
                    actualizarPanelEstadisticas(cliente);
                }
                break;
            }

        } else {
            controlPrincipal.mostrarMensajeConsolaServidor("No hay más jugadores activos");
        }
    }

    /**
     * Encuentra el número de turno del siguiente jugador activo en la secuencia
     * circular.
     *
     * @return El número de turno del siguiente jugador, o -1 si no hay
     * jugadores activos.
     */
    private int encontrarSiguienteJugadorActivo() {
        if (clientesActivos.isEmpty()) {
            return -1; // No hay jugadores, no hay siguiente turno.
        }

        // Variables para encontrar el siguiente turno circular
        int menorTurnoMayor = Integer.MAX_VALUE; // Almacena el menor turno que sea mayor al turno activo actual.
        int menorTurnoTotal = Integer.MAX_VALUE; // Almacena el menor turno absoluto entre todos los clientes activos.

        for (ThreadServidor cliente : clientesActivos) {
            int turnoCliente = cliente.getNumeroTurno();

            // Busca el siguiente turno que sea mayor al turno activo actual.
            if (turnoCliente > this.turnoActivo && turnoCliente < menorTurnoMayor) {
                menorTurnoMayor = turnoCliente;
            }

            // Busca el menor turno absoluto entre todos los clientes (para el caso de "wrap-around").
            if (turnoCliente < menorTurnoTotal) {
                menorTurnoTotal = turnoCliente;
            }
        }

        // Si se encontró un turno mayor al actual, ese es el siguiente. De lo contrario, se hace "wrap-around"
        // y el siguiente turno es el menor de todos los turnos activos.
        return (menorTurnoMayor != Integer.MAX_VALUE) ? menorTurnoMayor : menorTurnoTotal;
    }

    /**
     * Notifica a todos los clientes conectados sobre el cambio de turno. Cada
     * cliente recibe una señal para gestionar su estado de turno según la
     * lógica del juego Concentrese.
     */
    private void notificarCambioTurno() {
        for (ThreadServidor cliente : clientesActivos) {
            try {
                // Llama al método específico del hilo del cliente para gestionar los turnos de Concentrese.
                cliente.gestionarTurnosConcentrese();
            } catch (Exception e) {
                controlPrincipal.mostrarMensajeConsolaServidor("Error al notificar cambio de turno a cliente: " + e.getMessage());
            }
        }
    }

    /**
     * Muestra en la consola del servidor una lista de todos los clientes
     * conectados, indicando su información y si tienen el turno activo o están
     * esperando.
     */
    public void mostrarClientesConectados() {
        controlPrincipal.mostrarMensajeConsolaServidor("=== CLIENTES CONECTADOS ===");
        controlPrincipal.mostrarMensajeConsolaServidor("Turno activo: " + turnoActivo);

        if (clientesActivos.isEmpty()) {
            controlPrincipal.mostrarMensajeConsolaServidor("No hay clientes conectados");
        } else {
            for (int i = 0; i < clientesActivos.size(); i++) {
                ThreadServidor cliente = clientesActivos.get(i);
                // Determina el estado del cliente (turno activo o esperando).
                String estado = (cliente.getNumeroTurno() == turnoActivo) ? " [TURNO ACTIVO]" : " [ESPERANDO]";
                controlPrincipal.mostrarMensajeConsolaServidor((i + 1) + ". " + cliente.getInformacionCliente() + estado);
            }
        }
        controlPrincipal.mostrarMensajeConsolaServidor("========================");
    }

    /**
     * Reinicia el sistema de turnos, estableciendo el turno activo al número 1.
     * Este método es sincronizado.
     */
    public synchronized void reiniciarTurnos() {
        this.turnoActivo = 1;
        controlPrincipal.mostrarMensajeConsolaServidor("Sistema de turnos reiniciado - Turno activo: " + turnoActivo);
        notificarCambioTurno();
    }

    /**
     * Fuerza el avance del turno al siguiente jugador activo. Útil para la
     * administración manual del juego por parte del servidor.
     */
    public void forzarSiguienteTurno() {
        controlPrincipal.mostrarMensajeConsolaServidor("Forzando avance de turno por administrador...");
        avanzarSiguienteTurno();
    }

    /**
     * Establece manualmente un turno específico en el juego. Este método es
     * sincronizado.
     *
     * @param numeroTurno El número de turno al que se desea cambiar. Debe ser
     * mayor que 0.
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
     * Verifica si dos cartas seleccionadas forman una pareja. Realiza un
     * seguimiento del progreso del juego y registra las cartas emparejadas.
     *
     * @param x1 Coordenada X de la primera carta seleccionada.
     * @param y1 Coordenada Y de la primera carta seleccionada.
     * @param carta1 El tipo o valor de la primera carta.
     * @param x2 Coordenada X de la segunda carta seleccionada.
     * @param y2 Coordenada Y de la segunda carta seleccionada.
     * @param carta2 El tipo o valor de la segunda carta.
     * @return `true` si las dos cartas forman una pareja y sus coordenadas son
     * diferentes, `false` en caso contrario.
     */
    public boolean verificarPareja(int x1, int y1, String carta1, int x2, int y2, String carta2) {

        // Evita que se considere una pareja si se seleccionó la misma carta dos veces.
        if (x1 == x2 && y1 == y2) {
            return false;
        }

        // Compara si el valor de las cartas es el mismo.
        boolean esPareja = carta1 != null && carta1.equals(carta2);

        if (esPareja) {
            paresEncontrados++; // Incrementa el contador de pares encontrados.
            // Agrega las coordenadas de las cartas emparejadas al conjunto para evitar seleccionarlas de nuevo.
            cartasEmparejadas.add((x1 - 1) + "," + (y1 - 1));
            cartasEmparejadas.add((x2 - 1) + "," + (y2 - 1));

            controlPrincipal.mostrarMensajeConsolaServidor(
                    "Progreso: " + paresEncontrados + "/" + totalPares + " pares encontrados"
            );
        } else {
            controlPrincipal.mostrarMensajeConsolaServidor(
                    "No es pareja: '" + carta1 + "' ≠ '" + carta2 + "'"
            );
        }

        controlPrincipal.mostrarMensajeConsolaServidor("=========================");
        return esPareja;
    }

    /**
     * Método auxiliar para validar si las coordenadas de una carta están dentro
     * del rango válido del tablero de juego.
     *
     * @param x La coordenada X (columna) a verificar.
     * @param y La coordenada Y (fila) a verificar.
     * @return `true` si las coordenadas son válidas y están dentro de los
     * límites del tablero, `false` en caso contrario.
     */
    public boolean coordenadasValidas(int x, int y) {
        // Para un tablero de 8x5 (40 cartas = 20 pares), las coordenadas válidas son de 0 a 7 para X y 0 a 4 para Y.
        return x >= 0 && x < 8 && y >= 0 && y < 5;
    }

    /**
     * Verifica si el juego de Concentrese ha terminado, lo cual ocurre cuando
     * todas las cartas han sido emparejadas.
     *
     * @return `true` si el número de pares encontrados es igual o mayor al
     * total de pares, indicando que el juego ha terminado.
     */
    public boolean verificarJuegoTerminado() {
        return paresEncontrados >= totalPares;
    }

    /**
     * Termina el juego de Concentrese. Declara el fin del juego, muestra los
     * resultados finales y notifica a todos los clientes conectados que el
     * juego ha finalizado.
     */
    public void terminarJuego() {
        controlPrincipal.mostrarMensajeConsolaServidor("¡JUEGO TERMINADO! Todas las cartas han sido emparejadas");

        // Calcula puntuaciones y determina ganadores (la lógica específica de puntuación puede estar en ThreadServidor).
        mostrarResultadosFinales();

        // Notifica a todos los clientes conectados que el juego ha terminado.
        for (ThreadServidor cliente : clientesActivos) {
            try {
                cliente.getServidor().getServidorInformacionSalida1().writeUTF("juegoTerminado");
                cliente.getServidor().getServidorInformacionSalida1().flush(); // Asegura que el mensaje se envíe inmediatamente.

            } catch (IOException e) {
                controlPrincipal.mostrarMensajeConsolaServidor("Error al notificar fin de juego: " + e.getMessage());
            }
        }
    }

    /**
     * Muestra los resultados finales del juego en la consola del servidor,
     * incluyendo el total de pares encontrados y un mensaje de éxito.
     */
    private void mostrarResultadosFinales() {
        controlPrincipal.mostrarMensajeConsolaServidor("=== RESULTADOS FINALES ===");
        controlPrincipal.mostrarMensajeConsolaServidor("Total de pares encontrados: " + paresEncontrados);
        controlPrincipal.mostrarMensajeConsolaServidor("Juego completado exitosamente");
        controlPrincipal.mostrarMensajeConsolaServidor("========================");
    }

    /**
     * Reinicia el estado del juego de Concentrese, reseteando el contador de
     * pares encontrados, el turno activo y vaciando el conjunto de cartas
     * emparejadas. Notifica a todos los clientes que el juego ha sido
     * reiniciado.
     */
    public void reiniciarJuegoConcentrese() {
        paresEncontrados = 0; // Reinicia el contador de pares.
        turnoActivo = 1;      // Reinicia el turno al primer jugador.
        cartasEmparejadas.clear(); // Limpia las cartas emparejadas.
        controlPrincipal.mostrarMensajeConsolaServidor("Juego de Concentrese reiniciado");

        // Notifica a todos los clientes sobre el reinicio del juego.
        for (ThreadServidor cliente : clientesActivos) {
            try {
                cliente.getServidor().getServidorInformacionSalida1().writeUTF("juegoReiniciado");
                cliente.getServidor().getServidorInformacionSalida1().flush();
            } catch (IOException e) {
                controlPrincipal.mostrarMensajeConsolaServidor("Error al notificar reinicio: " + e.getMessage());
            }
        }

        notificarCambioTurno(); // Notifica el cambio de turno inicial después del reinicio.
    }

    /**
     * Establece el número total de pares que el juego de Concentrese debe
     * tener. Este valor se usa para determinar cuándo ha terminado el juego.
     *
     * @param totalPares El número entero de pares de cartas que conformarán el
     * juego.
     */
    public void configurarTotalPares(int totalPares) {
        if (totalPares > 0) {
            this.totalPares = totalPares;
            controlPrincipal.mostrarMensajeConsolaServidor("Total de pares configurado: " + totalPares);
        }
    }

    /**
     * Obtiene el número de pares de cartas que han sido encontrados hasta el
     * momento en el juego actual.
     *
     * @return El número entero de pares encontrados.
     */
    public int getParesEncontrados() {
        return paresEncontrados;
    }

    /**
     * Obtiene el número total de pares de cartas configurado para el juego.
     *
     * @return El número entero total de pares.
     */
    public int getTotalPares() {
        return totalPares;
    }

    /**
     * Envía un mensaje de texto a la consola de la interfaz de usuario del
     * servidor.
     *
     * @param mensaje El {@link String} que se desea mostrar en la consola del
     * servidor.
     */
    public void mostrarMensajeConsolaServidor(String mensaje) {
        controlPrincipal.mostrarMensajeConsolaServidor(mensaje);
    }

    /**
     * Obtiene el {@link Vector} que contiene todos los hilos de clientes
     * activos actualmente conectados al servidor.
     *
     * @return Un {@link Vector<ThreadServidor>} de clientes activos.
     */
    public static Vector<ThreadServidor> getClientesActivos() {
        return clientesActivos;
    }

    /**
     * Reemplaza la lista actual de clientes activos con un nuevo
     * {@link Vector}.
     *
     * @param clientesActivos El nuevo {@link Vector<ThreadServidor>} de
     * clientes activos.
     */
    public static void setClientesActivos(Vector<ThreadServidor> clientesActivos) {
        ControlServidor.clientesActivos = clientesActivos;
    }

    /**
     * Asigna los números de puerto para las conexiones del servidor,
     * convirtiendo las cadenas de texto a enteros y estableciéndolos en la
     * clase {@link Servidor}. Se incluye un manejo básico de excepciones para
     * `NumberFormatException`.
     *
     * @param puerto1 La cadena de texto que representa el primer número de
     * puerto.
     * @param puerto2 La cadena de texto que representa el segundo número de
     * puerto.
     */
    public void asignarIps(String puerto1, String puerto2) {
        try {
            int puerto1Int = Integer.parseInt(puerto1);
            int puerto2Int = Integer.parseInt(puerto2);
            Servidor.setPUERTO_1(puerto1Int);
            Servidor.setPUERTO_2(puerto2Int);
        } catch (NumberFormatException e) {
            // En caso de que las cadenas no sean números válidos, no se realiza ninguna acción específica,
            // pero se podría añadir un mensaje de error a la consola del servidor.
        }
    }

    /**
     * Busca la existencia de un usuario y contraseña en el sistema de
     * autenticación, delegando esta tarea al controlador principal.
     *
     * @param usuario El nombre de usuario a buscar.
     * @param contrasena La contraseña asociada al usuario.
     * @return `true` si el usuario con la contraseña dada existe y es válido,
     * `false` en caso contrario.
     */
    public boolean buscarUsuarioYContrasenaExistente(String usuario, String contrasena) {
        return controlPrincipal.buscarUsuarioYContrasenaExistente(usuario, contrasena);
    }

    /**
     * Obtiene el tipo de carta (representado como un String de su valor entero)
     * en una posición específica de la matriz de cartas del juego.
     *
     * @param x La coordenada X (columna) de la carta.
     * @param y La coordenada Y (fila) de la carta.
     * @return El tipo de carta como {@link String}, o una cadena vacía si las
     * coordenadas son inválidas o si ocurre algún error durante la
     * recuperación.
     */
    public String obtenerTipoCartaEnPosicion(int x, int y) {
        try {
            int[][] matrizCartas = controlPrincipal.getMatrizCartas();
            // Verifica que las coordenadas estén dentro de los límites de la matriz.
            if (y < 0 || y >= matrizCartas.length || x < 0 || x >= matrizCartas[0].length) {
                return ""; // Retorna cadena vacía para coordenadas inválidas.
            }
            int valorCarta = matrizCartas[y][x];
            return String.valueOf(valorCarta); // Convierte el valor entero de la carta a String.

        } catch (Exception e) {
            // Captura cualquier otra excepción y retorna una cadena vacía.
            return "";
        }
    }

    /**
     * Verifica si un usuario con el nombre dado ya se encuentra conectado al
     * servidor. Este método es sincronizado para asegurar la consistencia al
     * acceder a la colección de usuarios conectados.
     *
     * @param usuario El nombre de usuario a verificar.
     * @return `true` si el usuario ya está conectado, `false` en caso
     * contrario.
     */
    public synchronized boolean usuarioYaConectado(String usuario) {
        return usuariosConectados.contains(usuario);
    }

    /**
     * Obtiene la información del jugador a partir de sus credenciales (nombre
     * de usuario), delegando la operación al controlador principal.
     *
     * @param usuario El nombre de usuario del jugador.
     * @return Una cadena de texto con la información del jugador, o null si no
     * se encuentra.
     */
    public String obtenerJugadorPorCredenciales(String usuario) {
        return controlPrincipal.obtenerJugadorPorCredenciales(usuario);
    }

    /**
     * Verifica la cantidad de clientes que han iniciado sesión. Si hay dos o
     * más clientes logeados, habilita un botón para iniciar el juego en la
     * interfaz de usuario del servidor; de lo contrario, lo deshabilita.
     */
    public void verificarJugadoresMostrarBotonJugar() {
        if (cantidadClientesLogeados >= 2) {
            controlPrincipal.ocultarBotonIniciarJuego(true); // Oculta el botón (en realidad, lo habilita si el nombre del método significa "hacer visible").
        } else {
            controlPrincipal.ocultarBotonIniciarJuego(false); // Muestra el botón (o lo deshabilita).
        }
    }

    /**
     * Obtiene el número actual de clientes que han iniciado sesión en el
     * servidor.
     *
     * @return El número entero de clientes logeados.
     */
    public static int getCantidadClientesLogeados() {
        return cantidadClientesLogeados;
    }

    /**
     * Establece el número de clientes que han iniciado sesión en el servidor.
     *
     * @param cantidadClientesLogeados El nuevo número de clientes logeados.
     */
    public static void setCantidadClientesLogeados(int cantidadClientesLogeados) {
        ControlServidor.cantidadClientesLogeados = cantidadClientesLogeados;
    }

    /**
     * Inicia el juego de Concentrese. Itera sobre los clientes activos,
     * actualiza las estadísticas del panel para el cliente con el turno activo,
     * y envía el comando "pedirCoordenadas" a todos los clientes, indicando que
     * el juego ha comenzado y que deben esperar su turno para realizar una
     * jugada.
     */
    public void iniciarJuego() {
        for (ThreadServidor cliente : clientesActivos) {
            try {
                
                if (cliente.getNumeroTurno() == turnoActivo) {
                    actualizarPanelEstadisticas(cliente);
                }
                
                cliente.getServidor().getServidorInformacionSalida1().writeUTF("pedirCoordenadas");
                cliente.getServidor().getServidorInformacionSalida1().flush();

            } catch (IOException e) {
                controlPrincipal.mostrarMensajeConsolaServidor("Error al notificar inicio de juego: " + e.getMessage());
            }
        }
    }

    /**
     * Notifica al controlador principal que una carta ha sido seleccionada,
     * probablemente para actualizar la interfaz gráfica del servidor.
     *
     * @param idCarta El identificador único de la carta seleccionada.
     */
    public void seleccionarCarta(int idCarta) {
        controlPrincipal.seleccionarCarta(idCarta);
    }

    /**
     * Notifica al controlador principal que una carta ha sido deseleccionada,
     * probablemente para actualizar la interfaz gráfica del servidor.
     *
     * @param idCarta El identificador único de la carta deseleccionada.
     */
    public void deseleccionarCarta(int idCarta) {
        controlPrincipal.deseleccionarCarta(idCarta);
    }

    /**
     * Actualiza el panel de estadísticas en la interfaz de usuario del servidor
     * con los datos de un cliente específico.
     *
     * @param threadServidor El hilo {@link ThreadServidor} del cliente cuyas
     * estadísticas se van a actualizar.
     */
    public void actualizarPanelEstadisticas(ThreadServidor threadServidor) {
        int[] estadisticas = threadServidor.getEstadisticas(); // [0] = intentos, [1] = parejas, [2] = porcentaje de acierto.
        String numeroIntentos = String.valueOf(estadisticas[0]);
        String numeroParejas = String.valueOf(estadisticas[1]);
        String nombreUsuario = threadServidor.getServidor().getNombreUsuario();
        controlPrincipal.actualizarPanelEstadisticas(numeroIntentos, numeroParejas, nombreUsuario);
    }

    /**
     * Determina y retorna una cadena de texto con la información del ganador o
     * ganadores del juego. El ganador se determina por el porcentaje de acierto
     * más alto. En caso de empate, se listan todos los jugadores empatados.
     *
     * @return Una {@link String} que contiene los nombres de los jugadores
     * ganadores y su porcentaje de acierto.
     */
    public String enviarGanador() {
        ArrayList<ThreadServidor> ganadores = new ArrayList<>();
        int mayorPorcentaje = -1; // Inicializa con un valor bajo para encontrar el porcentaje más alto.

        // Itera sobre todos los clientes activos para encontrar el porcentaje de acierto más alto.
        for (ThreadServidor cliente : clientesActivos) {
            int[] estadisticas = cliente.getEstadisticas();
            int porcentajeAcierto = estadisticas[2]; // Asume que el índice 2 contiene el porcentaje de acierto.

            if (porcentajeAcierto > mayorPorcentaje) {
                mayorPorcentaje = porcentajeAcierto;
                ganadores.clear(); // Limpia la lista de ganadores anteriores si se encuentra un nuevo porcentaje más alto.
                ganadores.add(cliente); // Agrega al nuevo ganador.
            } else if (porcentajeAcierto == mayorPorcentaje) {
                ganadores.add(cliente); // Agrega clientes si hay un empate.
            }
        }

        // Construye la cadena de texto con la información de los ganadores.
        StringBuilder info = new StringBuilder();
        info.append("Ganador(es) con un porcentaje de acierto del ").append(mayorPorcentaje).append("%:\n");

        for (ThreadServidor ganador : ganadores) {
            info.append("- ").append(ganador.getInformacionCliente()).append("\n");
        }

        return info.toString();
    }

    public void setParesEncontrados(int paresEncontrados) {
        this.paresEncontrados = paresEncontrados;
    }
    
}
