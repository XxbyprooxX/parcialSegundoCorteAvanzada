package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * DAO de Jugador con validación mejorada para usuarios únicos
 *
 * @author Cristianlol789
 */
public class JugadorDAO {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    /**
     * Constructor por defecto, inicializa las variables como null.
     */
    public JugadorDAO() {
        this.connection = null;
        this.preparedStatement = null;
        this.resultSet = null;
    }

    /**
     * Verifica si ya existe un jugador con el usuario especificado
     *
     * @param usuario el usuario a verificar
     * @return true si el usuario ya existe, false en caso contrario
     * @throws SQLException Si ocurre un error al ejecutar la consulta
     */
    public boolean existeUsuario(String usuario) throws SQLException {
        String consulta = "SELECT COUNT(*) FROM jugadores WHERE LOWER(TRIM(usuario)) = LOWER(TRIM(?))";
        connection = ConexionBD.getConnection();
        preparedStatement = connection.prepareStatement(consulta);
        preparedStatement.setString(1, usuario);
        resultSet = preparedStatement.executeQuery();

        boolean existe = false;
        if (resultSet.next()) {
            existe = resultSet.getInt(1) > 0;
        }

        // Cerrar recursos
        if (resultSet != null) {
            resultSet.close();
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        ConexionBD.desconectar();

        return existe;
    }

    /**
     * Verifica si ya existe un jugador con la cédula especificada
     *
     * @param cedula la cédula a verificar
     * @return true si la cédula ya existe, false en caso contrario
     * @throws SQLException Si ocurre un error al ejecutar la consulta
     */
    public boolean existeCedula(String cedula) throws SQLException {
        if (cedula == null || cedula.trim().isEmpty()) {
            return false; // Si no hay cédula, no puede estar duplicada
        }
        
        String consulta = "SELECT COUNT(*) FROM jugadores WHERE TRIM(cedula) = TRIM(?)";
        connection = ConexionBD.getConnection();
        preparedStatement = connection.prepareStatement(consulta);
        preparedStatement.setString(1, cedula);
        resultSet = preparedStatement.executeQuery();

        boolean existe = false;
        if (resultSet.next()) {
            existe = resultSet.getInt(1) > 0;
        }

        // Cerrar recursos
        if (resultSet != null) {
            resultSet.close();
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        ConexionBD.desconectar();

        return existe;
    }

    /**
     * Consulta un jugador por usuario usando PreparedStatement para evitar
     * inyección SQL
     *
     * @param usuario Identificador del jugador
     * @param jugador Objeto JugadorVO que será llenado con los datos
     * encontrados
     * @return JugadorVO con los datos del jugador si se encuentra
     * @throws SQLException Si ocurre un error al ejecutar la consulta
     */
    public JugadorVO consultarUsuarioJugador(String usuario, JugadorVO jugador) throws SQLException {
        String consulta = "SELECT * FROM jugadores WHERE LOWER(TRIM(usuario)) = LOWER(TRIM(?))";
        connection = ConexionBD.getConnection();
        preparedStatement = connection.prepareStatement(consulta);
        preparedStatement.setString(1, usuario);
        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            jugador.setNombreJugador(resultSet.getString("nombreJugador"));
            jugador.setCedula(resultSet.getString("cedula"));
            jugador.setUsuario(resultSet.getString("usuario"));
            jugador.setContrasena(resultSet.getString("contrasena"));
        }

        // Cerrar recursos
        if (resultSet != null) {
            resultSet.close();
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        ConexionBD.desconectar();

        return jugador;
    }

    /**
     * Consulta la cantidad total de jugadores en la base de datos
     *
     * @return Número de jugadores registrados
     * @throws SQLException Si ocurre un error al ejecutar la consulta
     */
    public int consultarCantidadJugadores() throws SQLException {
        String consulta = "SELECT COUNT(*) FROM jugadores";
        connection = ConexionBD.getConnection();
        preparedStatement = connection.prepareStatement(consulta);
        resultSet = preparedStatement.executeQuery();

        int numero = 0;
        if (resultSet.next()) {
            numero = resultSet.getInt(1);
        }

        // Cerrar recursos
        if (resultSet != null) {
            resultSet.close();
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        ConexionBD.desconectar();

        return numero;
    }

    /**
     * Obtiene la lista completa de jugadores registrados en la base de datos
     *
     * @return Lista de objetos JugadorVO
     * @throws SQLException Si ocurre un error al ejecutar la consulta
     */
    public ArrayList<JugadorVO> darListaJugadores() throws SQLException {
        String consulta = "SELECT * FROM jugadores";
        connection = ConexionBD.getConnection();
        preparedStatement = connection.prepareStatement(consulta);
        resultSet = preparedStatement.executeQuery();

        ArrayList<JugadorVO> jugadores = new ArrayList<>();
        while (resultSet.next()) {
            JugadorVO jugador = new JugadorVO();
            jugador.setNombreJugador(resultSet.getString("nombreJugador"));
            jugador.setCedula(resultSet.getString("cedula"));
            jugador.setUsuario(resultSet.getString("usuario"));
            jugador.setContrasena(resultSet.getString("contrasena"));
            jugadores.add(jugador);
        }

        // Cerrar recursos
        if (resultSet != null) {
            resultSet.close();
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        ConexionBD.desconectar();

        return jugadores;
    }

    /**
     * Inserta un nuevo jugador en la base de datos usando PreparedStatement
     * con validación previa de usuario único
     *
     * @param jugador Objeto JugadorVO con la información del jugador a
     * registrar
     * @throws SQLException Si ocurre un error al insertar o si el usuario ya existe
     */
    public void insertarJugador(JugadorVO jugador) throws SQLException {
        // Validar que el usuario no exista antes de insertar
        if (existeUsuario(jugador.getUsuario())) {
            throw new SQLException("El usuario '" + jugador.getUsuario() + "' ya existe en la base de datos");
        }
        
        // Validar que la cédula no exista (si no está vacía)
        if (jugador.getCedula() != null && !jugador.getCedula().trim().isEmpty()) {
            if (existeCedula(jugador.getCedula())) {
                throw new SQLException("La cédula '" + jugador.getCedula() + "' ya existe en la base de datos");
            }
        }

        String insercion = "INSERT INTO jugadores (nombreJugador, cedula, usuario, contrasena) VALUES (?, ?, ?, ?)";
        connection = ConexionBD.getConnection();
        preparedStatement = connection.prepareStatement(insercion);

        preparedStatement.setString(1, jugador.getNombreJugador());
        preparedStatement.setString(2, jugador.getCedula());
        preparedStatement.setString(3, jugador.getUsuario());
        preparedStatement.setString(4, jugador.getContrasena());

        preparedStatement.executeUpdate();

        if (preparedStatement != null) {
            preparedStatement.close();
        }
        ConexionBD.desconectar();
    }
}