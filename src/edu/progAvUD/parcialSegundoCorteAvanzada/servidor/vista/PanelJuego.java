package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.vista;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import java.awt.GridLayout; // Importar GridLayout para un mejor manejo de la disposición de botones

/**
 * La clase **`PanelJuego`** extiende `javax.swing.JPanel` y representa la
 * interfaz visual del tablero de juego del "Concéntrese" en el lado del
 * servidor. Contiene una colección de botones (`JToggleButton`) que actúan como
 * las cartas del juego.
 *
 * Esta clase se encarga de la creación, visualización y manipulación de las
 * cartas en la interfaz gráfica del servidor, permitiendo mostrar el estado
 * actual del juego.
 *
 * @author Andres Felipe
 */
public class PanelJuego extends javax.swing.JPanel {

    /**
     * Un arreglo de `JToggleButton` que representa las cartas individuales en
     * el tablero de juego. Cada `JToggleButton` corresponde a una carta que
     * puede ser "volteada" (seleccionada) o "boca abajo" (deseleccionada).
     */
    public JToggleButton[] cartas;

    /**
     * Creates new form PanelJuego. Constructor de la clase `PanelJuego`.
     * Inicializa los componentes de la interfaz de usuario generados por el
     * constructor de formularios y crea el arreglo de `JToggleButton` para
     * almacenar las 40 cartas del juego.
     */
    public PanelJuego() {
        initComponents();
        this.cartas = new JToggleButton[40]; // Se inicializa el arreglo para 40 cartas (8 columnas x 5 filas)
        // Opcional: Configurar un layout para jPanelBotones si no está configurado en initComponents()
        // Por ejemplo, para un tablero de 8x5:
        // jPanelBotones.setLayout(new GridLayout(5, 8)); 
    }

    /**
     * Añade un nuevo botón (`JToggleButton`) al panel de juego en la posición
     * especificada. El botón se inicializa con una imagen de "dorso" (carta
     * boca abajo).
     *
     * @param numeroBoton El índice en el arreglo `cartas` donde se almacenará y
     * añadirá el nuevo botón. Debe ser un valor entre 0 y 39.
     */
    public void anadirBoton(int numeroBoton) {
        JToggleButton carta = new JToggleButton();
        // Carga la imagen del dorso de la carta. Se asume que la ruta es correcta desde la raíz del proyecto.
        ImageIcon iconNormal = new ImageIcon(System.getProperty("user.dir") + "/src/edu/progAvUD/parcialSegundoCorteAvanzada/servidor/imagenes/dorso.png");
        carta.setIcon(iconNormal); // Establece la imagen por defecto (cuando la carta no está seleccionada)
        cartas[numeroBoton] = carta; // Almacena la referencia del botón en el arreglo
        jPanelBotones.add(carta); // Añade el botón al panel visual
        revalidate(); // Revalida el layout del contenedor para asegurar que el nuevo botón se muestre
    }

    /**
     * Establece la imagen que se mostrará cuando una carta sea seleccionada
     * (volteada). La imagen se carga dinámicamente según el `idImagen`
     * proporcionado.
     *
     * @param idCarta El índice de la carta en el arreglo `cartas` a la que se
     * le asignará la imagen.
     * @param idImagen El identificador numérico de la imagen de la carta (ej.,
     * "1.jpg", "2.jpg").
     */
    public void ponerImagenCarta(int idCarta, int idImagen) {
        JToggleButton carta = cartas[idCarta];
        // Carga la imagen específica de la carta según su ID.
        ImageIcon iconSeleccionado = new ImageIcon(System.getProperty("user.dir") + "/src/edu/progAvUD/parcialSegundoCorteAvanzada/servidor/imagenes/" + idImagen + ".jpg");
        carta.setSelectedIcon(iconSeleccionado); // Establece la imagen cuando la carta está seleccionada
        revalidate(); // Revalida el layout para asegurar la actualización visual
    }

    /**
     * Selecciona (voltea) visualmente una carta específica en el tablero de
     * juego. Esto hace que la carta muestre su imagen de `selectedIcon`.
     *
     * @param idCarta El índice de la carta en el arreglo `cartas` que se va a
     * seleccionar.
     */
    public void seleccionarCarta(int idCarta) {
        JToggleButton carta = cartas[idCarta];
        if (carta != null) { // Asegura que el botón existe
            carta.setSelected(true); // Marca el botón como seleccionado, mostrando su imagen
            revalidate(); // Revalida para actualizar la interfaz
        }
    }

    /**
     * Deselecciona (voltea boca abajo) visualmente una carta específica en el
     * tablero de juego. Esto hace que la carta vuelva a mostrar su imagen de
     * `icon` (el dorso).
     *
     * @param idCarta El índice de la carta en el arreglo `cartas` que se va a
     * deseleccionar.
     */
    public void deseleccionarCarta(int idCarta) {
        JToggleButton carta = cartas[idCarta];
        if (carta != null) { // Asegura que el botón existe
            carta.setSelected(false); // Marca el botón como no seleccionado, mostrando el dorso
            revalidate(); // Revalida para actualizar la interfaz
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabelJugador = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabelNumeroIntentos = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabelNumeroParejas = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanelBotones = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Jugador:");
        jPanel6.add(jLabel1);

        jLabelJugador.setText("jLabel2");
        jPanel6.add(jLabelJugador);

        jPanel1.add(jPanel6);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Numero de Intentos:");
        jPanel8.add(jLabel6);

        jLabelNumeroIntentos.setText("jLabel8");
        jPanel8.add(jLabelNumeroIntentos);

        jPanel1.add(jPanel8);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("Numero de Parejas:");
        jPanel7.add(jLabel9);

        jLabelNumeroParejas.setText("jLabel10");
        jPanel7.add(jLabelNumeroParejas);

        jPanel1.add(jPanel7);

        add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Concentrese");
        add(jLabel5, java.awt.BorderLayout.PAGE_START);

        jPanelBotones.setLayout(new java.awt.GridLayout(5, 8));
        add(jPanelBotones, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    public javax.swing.JLabel jLabelJugador;
    public javax.swing.JLabel jLabelNumeroIntentos;
    public javax.swing.JLabel jLabelNumeroParejas;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    public javax.swing.JPanel jPanelBotones;
    // End of variables declaration//GEN-END:variables
}
