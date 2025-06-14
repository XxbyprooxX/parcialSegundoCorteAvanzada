package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
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
        String consulta = "SELECT * FROM jugadores WHERE usuario = ?";
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
     * Consulta jugadores por un factor específico usando PreparedStatement
     *
     * @param factorBusqueda el campo por el cual buscar (cedula, usuario, etc.)
     * @param datoBuscado valor a buscar
     * @return ArrayList con los jugadores encontrados
     * @throws SQLException Si ocurre un error al ejecutar la consulta
     */
    public ArrayList<JugadorVO> consultarJugadores(String factorBusqueda, String datoBuscado) throws SQLException {
        // Validar que el factor de búsqueda sea uno de los campos permitidos
        if (!esFactorBusquedaValido(factorBusqueda)) {
            throw new SQLException("Factor de búsqueda no válido: " + factorBusqueda);
        }

        String consulta = "SELECT * FROM jugadores WHERE " + factorBusqueda + " = ?";
        connection = ConexionBD.getConnection();
        preparedStatement = connection.prepareStatement(consulta);
        preparedStatement.setString(1, datoBuscado);
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
     * Valida que el factor de búsqueda sea uno de los campos permitidos
     *
     * @param factor el factor a validar
     * @return true si es válido, false en caso contrario
     */
    private boolean esFactorBusquedaValido(String factor) {
        return factor.equals("nombreJugador") || factor.equals("cedula")
                || factor.equals("usuario") || factor.equals("contrasena");
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
     *
     * @param jugador Objeto JugadorVO con la información del jugador a
     * registrar
     * @throws SQLException Si ocurre un error al insertar
     */
    public void insertarJugador(JugadorVO jugador) throws SQLException {
        String insercion = "INSERT INTO jugadores (nombreJugador, cedula, usuario, contrasena) VALUES (?, ?, ?, ?)";
        connection = ConexionBD.getConnection();
        preparedStatement = connection.prepareStatement(insercion);

        preparedStatement.setString(1, jugador.getNombreJugador());
        preparedStatement.setString(2, jugador.getCedula());
        preparedStatement.setString(3, jugador.getUsuario());
        preparedStatement.setString(4, jugador.getContrasena());

        preparedStatement.executeUpdate();

        // Cerrar recursos
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        ConexionBD.desconectar();
    }
}
