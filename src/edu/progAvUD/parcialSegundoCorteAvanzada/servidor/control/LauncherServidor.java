package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

/**
 * La clase `LauncherServidor` es el punto de entrada principal para iniciar la
 * aplicación del servidor. Su función es crear una instancia de
 * {@link ControlPrincipal}, que se encarga de inicializar la interfaz de
 * usuario del servidor y la lógica de control.
 *
 * @author Andres Felipe
 */
public class LauncherServidor {

    /**
     * El método `main` es el punto de inicio de la ejecución del servidor. Al
     * ser invocado, crea una nueva instancia de {@link ControlPrincipal}, lo
     * que a su vez pone en marcha toda la aplicación del servidor, incluyendo
     * su interfaz gráfica y la preparación para la gestión de clientes y el
     * juego.
     *
     * @param args Argumentos de la línea de comandos (no utilizados en esta
     * aplicación).
     */
    public static void main(String[] args) {
        // Al crear una nueva instancia de ControlPrincipal, se inicializa la ventana<
        new ControlPrincipal();
    }
}
