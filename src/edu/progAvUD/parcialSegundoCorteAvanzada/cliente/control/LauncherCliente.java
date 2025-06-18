package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.control;

/**
 * Clase principal de arranque del cliente.
 * 
 * La clase {@code LauncherCliente} contiene el método {@code main}, que es el
 * punto de entrada para ejecutar la aplicación cliente. Su única función es
 * iniciar el controlador principal {@code ControlPrincipal}, el cual
 * gestiona toda la lógica de la aplicación del lado del cliente, incluida
 * la interfaz gráfica y la conexión con el servidor.
 * 
 * Autor: Andres Felipe
 */
public class LauncherCliente {
    
    /**
     * Método principal que lanza la aplicación cliente.
     * 
     * @param args Argumentos de línea de comandos (no se utilizan en este caso).
     */
    public static void main(String[] args) {
        // Inicia el controlador principal del cliente
        new ControlPrincipal();
    }
    
}