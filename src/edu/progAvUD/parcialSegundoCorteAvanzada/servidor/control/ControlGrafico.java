package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.vista.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Gestiona las interacciones de la interfaz gráfica de usuario y actúa como intermediario
 * entre la vista (VentanaPrincipal) y la lógica principal de la aplicación (ControlPrincipal).
 * Maneja las acciones del usuario desde la GUI y actualiza la vista basándose en el estado de la aplicación.
 *
 * @author Andres Felipe
 */
public class ControlGrafico implements ActionListener {

    private ControlPrincipal controlPrincipal;
    private VentanaPrincipal ventanaPrincipal;

    /**
     * Construye una nueva instancia de ControlGrafico.
     * Inicializa la ventana principal y configura los 'action listeners' para varios botones
     * dentro del panel inicial. También gestiona la visibilidad inicial y
     * los estados habilitados de ciertos elementos de la GUI.
     *
     * @param controlPrincipal La clase de control principal que maneja la lógica de la aplicación.
     */
    public ControlGrafico(ControlPrincipal controlPrincipal) {
        this.controlPrincipal = controlPrincipal;
        this.ventanaPrincipal = new VentanaPrincipal(this);
        // Agrega 'action listeners' para los botones del panel inicial
        ventanaPrincipal.panelInicial.jButtonPropiedadesBD.addActionListener(this);
        ventanaPrincipal.panelInicial.jButtonPropiedadesJugadores.addActionListener(this);
        ventanaPrincipal.panelInicial.jButtonPropiedadesSockets.addActionListener(this);
        // Deshabilita ciertos botones inicialmente
        ventanaPrincipal.panelInicial.jButtonPropiedadesJugadores.setEnabled(false);
        ventanaPrincipal.panelInicial.jButtonPropiedadesSockets.setEnabled(false);
        // Muestra el panel inicial
        ventanaPrincipal.mostrarPanel(ventanaPrincipal.panelInicial);

        // Configura el botón "Empezar Juego" en el panel de la consola del servidor
        ventanaPrincipal.panelConsolaServidor.jButtonEmpezarJuego.setVisible(false);
        ventanaPrincipal.panelConsolaServidor.jButtonEmpezarJuego.addActionListener(this);

    }

    /**
     * Muestra un mensaje de error al usuario a través de la ventana principal.
     *
     * @param mensaje La cadena del mensaje de error a mostrar.
     */
    public void mostrarMensajeError(String mensaje) {
        ventanaPrincipal.mostrarMensajeError(mensaje);
    }

    /**
     * Muestra un mensaje de éxito al usuario a través de la ventana principal.
     *
     * @param mensaje La cadena del mensaje de éxito a mostrar.
     */
    public void mostrarMensajeExito(String mensaje) {
        ventanaPrincipal.mostrarMensajeExito(mensaje);
    }

    /**
     * Solicita al usuario que seleccione un archivo de propiedades. Este
     * archivo puede contener configuraciones necesarias para el programa.
     *
     * @return El archivo seleccionado por el usuario.
     */
    public File pedirArchivoPropiedades() {
        return ventanaPrincipal.pedirArchivoPropiedades();
    }

    /**
     * Muestra un cuadro de diálogo de entrada para que el usuario ingrese un dato faltante.
     *
     * @param datoFaltante Una descripción del dato que el usuario necesita ingresar.
     * @return El texto ingresado por el usuario.
     */
    public String mostrarJOptionEscribirDatoFaltante(String datoFaltante) {
        return ventanaPrincipal.mostrarJOptionEscribirDatoFaltante(datoFaltante);
    }

    /**
     * Maneja los eventos de acción desencadenados por las interacciones del usuario con los componentes de la GUI.
     * Este método determina la fuente del evento y llama al método apropiado
     * en `ControlPrincipal` o actualiza la `VentanaPrincipal` en consecuencia.
     *
     * @param e El objeto ActionEvent que contiene información sobre el evento.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Maneja la acción para el botón "Propiedades BD"
        if (e.getSource() == ventanaPrincipal.panelInicial.jButtonPropiedadesBD) {
            controlPrincipal.cargarDatosBD(); // Carga los datos de la base de datos
            ventanaPrincipal.panelInicial.jButtonPropiedadesSockets.setEnabled(true); // Habilita el botón de Sockets
            ventanaPrincipal.panelInicial.jButtonPropiedadesBD.setEnabled(false); // Deshabilita el botón de BD
        }
        // Maneja la acción para el botón "Propiedades Jugadores"
        if (e.getSource() == ventanaPrincipal.panelInicial.jButtonPropiedadesJugadores) {
            ventanaPrincipal.panelInicial.jButtonPropiedadesJugadores.setEnabled(false); // Deshabilita el botón de Jugadores
            controlPrincipal.cargarDatosJugadoresPropiedades(); // Carga las propiedades de los jugadores
            ventanaPrincipal.mostrarPanel(ventanaPrincipal.panelConsolaServidor); // Muestra el panel de la consola del servidor
            controlPrincipal.empezarServer(); // Inicia el servidor
        }
        // Maneja la acción para el botón "Propiedades Sockets"
        if (e.getSource() == ventanaPrincipal.panelInicial.jButtonPropiedadesSockets) {
            controlPrincipal.cargarPropiedadesSockets(); // Carga las propiedades de los sockets
            ventanaPrincipal.panelInicial.jButtonPropiedadesJugadores.setEnabled(true); // Habilita el botón de Jugadores
            ventanaPrincipal.panelInicial.jButtonPropiedadesSockets.setEnabled(false); // Deshabilita el botón de Sockets
        }
        // Maneja la acción para el botón "Empezar Juego" en la consola del servidor
        if (e.getSource() == ventanaPrincipal.panelConsolaServidor.jButtonEmpezarJuego) {
            controlPrincipal.asignarOrdenMatrizCartas(); // Asigna el orden de la matriz de cartas
            anadirCartasJuego(); // Añade las cartas al panel de juego
            ponerImagenCartas(); // Establece las imágenes para las cartas
            controlPrincipal.iniciarJuego(); // Inicia la lógica del juego
            ventanaPrincipal.mostrarPanel(ventanaPrincipal.panelJuego); // Muestra el panel de juego
        }

    }

    /**
     * Muestra un mensaje en el área de la consola gráfica del servidor.
     *
     * @param mensaje La cadena de texto a mostrar en el área de la consola.
     */
    public void mostrarMensajeConsolaServidor(String mensaje) {
        ventanaPrincipal.mostrarMensajeConsolaServidor(mensaje);
    }

    /**
     * Controla la visibilidad del botón "Empezar Juego" en el panel de la consola del servidor.
     *
     * @param estado Un valor booleano; 'true' para hacer el botón visible, 'false' para ocultarlo.
     */
    public void ocultarBotonIniciarJuego(boolean estado) {
        ventanaPrincipal.panelConsolaServidor.jButtonEmpezarJuego.setVisible(estado);
    }

    /**
     * Añade el número especificado de botones de cartas al panel de juego.
     * Este método itera 40 veces, añadiendo un botón por cada carta.
     */
    public void anadirCartasJuego() {
        for (int i = 0; i < 40; i++) {
            ventanaPrincipal.panelJuego.anadirBoton(i);
        }
    }

    /**
     * Establece las imágenes para todas las cartas en el panel de juego basándose en una matriz predefinida.
     * Recupera la disposición de las cartas de `ControlPrincipal` y aplica las
     * imágenes correspondientes a cada botón de carta en la `VentanaPrincipal`.
     */
    public void ponerImagenCartas() {
        int idCarta;
        int idImagen;
        int[][] matrizCartas = controlPrincipal.getMatrizCartas(); // Obtiene la matriz de cartas de ControlPrincipal
        for (int fila = 0; fila < 5; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                idCarta = fila * 8 + columna; // Calcula el ID único para cada carta
                idImagen = matrizCartas[fila][columna]; // Obtiene el ID de la imagen para la carta actual
                ventanaPrincipal.panelJuego.ponerImagenCarta(idCarta, idImagen); // Establece la imagen en el botón de la carta
            }

        }
    }

    /**
     * Selecciona visualmente una carta específica en el panel de juego.
     *
     * @param idCarta El identificador único de la carta a seleccionar.
     */
    public void seleccionarCarta(int idCarta){
        ventanaPrincipal.panelJuego.seleccionarCarta(idCarta);
    }

    /**
     * Deselecciona visualmente una carta específica en el panel de juego.
     *
     * @param idCarta El identificador único de la carta a deseleccionar.
     */
    public void deseleccionarCarta(int idCarta){
        ventanaPrincipal.panelJuego.deseleccionarCarta(idCarta);
    }

    /**
     * Actualiza el panel de estadísticas en la ventana del juego con el número actual de intentos,
     * el número de parejas encontradas y el nombre del jugador actual.
     *
     * @param numeroIntentos La representación en cadena del total de intentos realizados.
     * @param numeroParejas La representación en cadena del número de parejas encontradas.
     * @param nombreUsuario El nombre del jugador actual.
     */
    public void actualizarPanelEstadisticas(String numeroIntentos, String numeroParejas, String nombreUsuario){
        ventanaPrincipal.panelJuego.jLabelNumeroIntentos.setText(numeroIntentos);
        ventanaPrincipal.panelJuego.jLabelNumeroParejas.setText(numeroParejas);
        ventanaPrincipal.panelJuego.jLabelJugador.setText(nombreUsuario);
    }
}