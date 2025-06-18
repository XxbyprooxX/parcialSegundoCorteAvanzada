package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.vista;

import java.io.File; // Necesario para manejar operaciones con archivos, especialmente con JFileChooser.
import javax.swing.JFileChooser; // Proporciona un cuadro de diálogo para que el usuario seleccione un archivo.
import javax.swing.JOptionPane; // Utilizado para mostrar cuadros de diálogo estándar como advertencias, errores o mensajes informativos.
import javax.swing.JPanel; // Contenedor liviano genérico. Sirve como base para construir componentes de interfaz personalizados.
import javax.swing.filechooser.FileNameExtensionFilter; // Permite filtrar los tipos de archivos mostrados en un JFileChooser.

/**
 * La clase {@code VentanaPrincipal} extiende {@code javax.swing.JFrame} y actúa
 * como la ventana principal de la aplicación cliente del juego "Concéntrese".
 *
 * Esta ventana se encarga de mostrar dinámicamente los distintos paneles de la
 * interfaz gráfica, como el panel de inicio de sesión, el panel inicial y el
 * panel del juego con chat, de acuerdo con el estado actual de la aplicación.
 *
 * Funciona como contenedor central de los módulos gráficos del cliente.
 *
 * @author Andres Felipe
 */
public class VentanaPrincipal extends javax.swing.JFrame {

    /**
     * Instancia del panel de inicio de sesión, donde el usuario ingresa sus
     * credenciales. Este panel es normalmente la primera vista mostrada al
     * iniciar la aplicación.
     */
    public PanelLogin panelLogin;

    /**
     * Instancia del panel inicial, que puede mostrar opciones generales antes
     * de iniciar el juego o información de bienvenida. También puede funcionar
     * como sala de espera o pantalla de configuración.
     */
    public PanelInicial panelInicial;

    /**
     * Instancia del panel del juego y chat, que integra la interfaz del juego
     * "Concéntrese" junto con una funcionalidad de chat para comunicación entre
     * los jugadores. Es la vista principal durante una partida activa.
     */
    public PanelJuegoChat panelJuegoChat;

    /**
     * Constructor de la clase {@code VentanaPrincipal}. Inicializa los
     * componentes gráficos de la ventana principal, crea las instancias de los
     * paneles y hace visible la ventana al usuario.
     */
    public VentanaPrincipal() {
        initComponents(); // Inicializa los componentes visuales definidos en el diseñador de interfaces.
        this.panelLogin = new PanelLogin(); // Crea el panel de inicio de sesión.
        this.panelJuegoChat = new PanelJuegoChat(); // Crea el panel del juego con chat.
        this.panelInicial = new PanelInicial(); // Crea el panel inicial o de bienvenida.
        setVisible(true); // Hace visible la ventana principal.
    }

    /**
     * Muestra un cuadro de diálogo con un mensaje informativo de éxito.
     *
     * @param mensaje Texto que se mostrará al usuario.
     */
    public void mostrarMensajeExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra un cuadro de diálogo con un mensaje de error.
     *
     * @param mensaje Texto que se mostrará al usuario.
     */
    public void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Reemplaza el contenido actual de la ventana principal por un nuevo panel.
     * Ajusta el tamaño de la ventana, la centra en pantalla y actualiza su
     * contenido.
     *
     * @param panel Panel que se desea mostrar como vista principal.
     */
    public void mostrarPanel(JPanel panel) {
        setContentPane(panel); // Establece el nuevo panel como contenido principal.
        pack(); // Ajusta el tamaño de la ventana al contenido.
        setLocationRelativeTo(null); // Centra la ventana en la pantalla.
        revalidate(); // Actualiza el diseño de los componentes.
        repaint(); // Redibuja la ventana con el nuevo contenido.
    }

    /**
     * Abre un cuadro de diálogo para que el usuario seleccione un archivo con
     * extensión .properties. El cuadro de selección se inicializa en el
     * directorio correspondiente a los archivos de configuración del cliente.
     *
     * @return Archivo seleccionado por el usuario, o {@code null} si se cancela
     * la operación.
     */
    public File pedirArchivoPropiedades() {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "/src/edu/progAvUD/parcialSegundoCorteAvanzada/cliente/data");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos .properties", "properties"));
        fileChooser.showOpenDialog(this); // Muestra el diálogo de selección de archivo.
        return fileChooser.getSelectedFile(); // Retorna el archivo seleccionado.
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
