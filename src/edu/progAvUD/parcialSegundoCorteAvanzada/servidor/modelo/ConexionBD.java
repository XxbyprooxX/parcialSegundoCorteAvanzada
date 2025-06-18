package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * La clase **`ConexionBD`** es una utilidad estática diseñada para gestionar la conexión a una base de datos utilizando JDBC.
 * Proporciona métodos para establecer, obtener y cerrar una única conexión a la base de datos, así como para configurar
 * los parámetros de acceso como la URL, el usuario y la contraseña.
 *
 * @author Andres Felipe
 */
public class ConexionBD {

    /**
     * Un objeto estático de tipo {@link Connection} que representa la **única conexión activa** a la base de datos.
     * Se mantiene estático para asegurar que solo haya una conexión abierta en un momento dado, siguiendo
     * un patrón de diseño similar a un Singleton (aunque no implementado formalmente como tal).
     */
    private static Connection connection;

    /**
     * Una cadena estática que almacena la **URL de conexión** a la base de datos.
     * Por ejemplo: `"jdbc:mysql://localhost:3306/miBD"`.
     */
    private static String URLBD;

    /**
     * Una cadena estática que guarda el **nombre de usuario** para la autenticación en la base de datos.
     */
    private static String usuario;

    /**
     * Una cadena estática que contiene la **contraseña** correspondiente al usuario de la base de datos.
     */
    private static String contrasena;

    /**
     * Obtiene una conexión a la base de datos. Si ya existe una conexión previa, esta será cerrada
     * y reemplazada por una nueva conexión, asegurando que siempre se devuelva una conexión activa y fresca.
     *
     * Es crucial que los parámetros de conexión (URL, usuario, contraseña) hayan sido previamente
     * establecidos usando los métodos `setURLBD`, `setUsuario` y `setContrasena`.
     *
     * @return Un objeto {@link Connection} listo para interactuar con la base de datos.
     * @throws SQLException Si ocurre un error al intentar establecer la conexión con la base de datos,
     * por ejemplo, credenciales incorrectas, base de datos no disponible, o URL inválida.
     */
    public static Connection getConnection() throws SQLException {
        // Antes de establecer una nueva conexión, se recomienda cerrar la existente si está abierta
        // Esto evita fugas de conexiones si se llama repetidamente sin desconectar explícitamente
        if (connection != null && !connection.isClosed()) {
            connection.close(); // Cierra la conexión anterior para evitar acumulación
        }
        // Establece una nueva conexión utilizando los parámetros configurados
        connection = DriverManager.getConnection(URLBD, usuario, contrasena);
        return connection;
    }

    /**
     * Cierra la conexión actual a la base de datos.
     * Este método invoca el método `close()` del objeto {@link Connection} para liberar los recursos
     * asociados y luego establece la referencia `connection` a `null` para indicar que no hay una
     * conexión activa.
     *
     * @throws SQLException Si ocurre un error al intentar cerrar la conexión.
     */
    public static void desconectar() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Conexión a la base de datos cerrada correctamente.");
        }
        connection = null; // Establece la referencia a null para indicar que no hay conexión activa
    }

    /**
     * Establece la **URL de conexión** para la base de datos.
     * Este método debe ser llamado antes de intentar obtener una conexión con {@link #getConnection()}.
     *
     * @param URLBD La cadena que representa la URL de conexión JDBC (e.g., "jdbc:mysql://localhost:3306/nombre_bd").
     */
    public static void setURLBD(String URLBD) {
        ConexionBD.URLBD = URLBD;
    }

    /**
     * Establece el **nombre de usuario** que se utilizará para la autenticación en la base de datos.
     * Este método debe ser llamado antes de intentar obtener una conexión con {@link #getConnection()}.
     *
     * @param usuario El nombre de usuario de la base de datos.
     */
    public static void setUsuario(String usuario) {
        ConexionBD.usuario = usuario;
    }

    /**
     * Establece la **contraseña** para el usuario de la base de datos.
     * Este método debe ser llamado antes de intentar obtener una conexión con {@link #getConnection()}.
     *
     * @param contrasena La contraseña del usuario de la base de datos.
     */
    public static void setContrasena(String contrasena) {
        ConexionBD.contrasena = contrasena;
    }
}