package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de gestionar la conexión a una base de datos usando JDBC.
 * Permite establecer y cerrar la conexión, así como configurar los datos de acceso.
 * 
 * Autor: Andres Felipe
 */
public class ConexionBD {

    // Objeto que representa la conexión a la base de datos
    private static Connection connection;

    // URL de conexión a la base de datos (por ejemplo: "jdbc:mysql://localhost:3306/miBD")
    private static String URLBD;

    // Nombre de usuario para conectarse a la base de datos
    private static String usuario;

    // Contraseña correspondiente al usuario
    private static String contrasena;

    /**
     * Método para obtener la conexión a la base de datos.
     * Si ya existe, la sobreescribe con una nueva conexión.
     * 
     * @return Objeto Connection para interactuar con la base de datos.
     * @throws SQLException Si ocurre un error al establecer la conexión.
     */
    public static Connection getConnection() throws SQLException {
        connection = DriverManager.getConnection(URLBD, usuario, contrasena);
        return connection; 
    }

    /**
     * Método para cerrar la conexión a la base de datos.
     * En realidad, solo pone la referencia a null (no cierra explícitamente).
     */
    public static void desconectar() {
        connection = null;
    }

    /**
     * Método para establecer la URL de la base de datos.
     * 
     * @param URLBD Cadena con la URL de conexión JDBC.
     */
    public static void setURLBD(String URLBD) {
        ConexionBD.URLBD = URLBD;
    }

    /**
     * Método para establecer el nombre de usuario de la base de datos.
     * 
     * @param usuario Nombre del usuario.
     */
    public static void setUsuario(String usuario) {
        ConexionBD.usuario = usuario;
    }

    /**
     * Método para establecer la contraseña del usuario de la base de datos.
     * 
     * @param contrasena Contraseña del usuario.
     */
    public static void setContrasena(String contrasena) {
        ConexionBD.contrasena = contrasena;
    }
}