package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.modelo;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.ConexionBD;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Cristianlol789
 */
public class JugadorDAO {

    private Connection connection;  // Conexión a la base de datos
    private Statement statement;    // Objeto para ejecutar sentencias SQL
    private ResultSet resultSet;    // Resultado de las consultas SQL

    /**
     * Constructor por defecto, inicializa las variables como null.
     */
    public JugadorDAO() {
        this.connection = null;
        this.statement = null;
        this.resultSet = null;
    }

    /**
     * Consulta un jugador por ID (entero) y carga sus datos en el objeto GatoVO
     * dado.
     *
     * @param cedula Identificador del jugador.
     * @param jugador Objeto JugadorVO que será llenado con los datos
     * encontrados.
     * @return JugadorVO con los datos del jugador si se encuentra.
     * @throws SQLException Si ocurre un error al ejecutar la consulta.
     */
    public JugadorVO consultarGatoPorId(int cedula, JugadorVO jugador) throws SQLException {
        String consulta = "SELECT * FROM jugadores where cedula='" + cedula + "'";
        connection = ConexionBD.getConnection();
        statement = connection.createStatement();
        resultSet = statement.executeQuery(consulta);
        if (resultSet.next()) {
            jugador.setNombreJugador(resultSet.getString("nombreJugador"));
            jugador.setCedula(resultSet.getString("cedula"));
            jugador.setUsuario(resultSet.getString("usuario"));
            jugador.setContrasena(resultSet.getString("contrasena"));
        }
        statement.close();
        ConexionBD.desconectar();
        return jugador;
    }

    /**
     * Consulta un jugador por ID representado como String.(Útil si el ID
     * proviene de una entrada textual).
     *
     * @param factorBusqueda el parametro de busqueda
     * @param datoBuscado cedula del jugador en formato String.
     * @return JugadorVO con los datos consultados.
     * @throws SQLException Si ocurre un error al ejecutar la consulta.
     */
    public ArrayList<JugadorVO> consultarGatos(String factorBusqueda, String datoBuscado) throws SQLException {
        String consulta = "SELECT * FROM jugadores where " + factorBusqueda + "='" + datoBuscado + "'";
        connection = ConexionBD.getConnection();
        statement = connection.createStatement();
        resultSet = statement.executeQuery(consulta);
        ArrayList<JugadorVO> jugadores = new ArrayList<>();
        while (resultSet.next()) {
            JugadorVO jugador = new JugadorVO();
            jugador.setNombreJugador(resultSet.getString("nombreJugador"));
            jugador.setCedula(resultSet.getString("cedula"));
            jugador.setUsuario(resultSet.getString("usuario"));
            jugador.setContrasena(resultSet.getString("contrasena"));
            jugadores.add(jugador);
        }
        statement.close();
        ConexionBD.desconectar();
        return jugadores;
    }

    /**
     * Consulta la cantidad total de jugadores en la base de datos.
     *
     * @return Número de jugadores registrados.
     * @throws SQLException Si ocurre un error al ejecutar la consulta.
     */
    public int consultarCantidadGatos() throws SQLException {
        String consulta = "SELECT COUNT(*) FROM jugadores";
        connection = ConexionBD.getConnection();
        statement = connection.createStatement();
        resultSet = statement.executeQuery(consulta);
        int numero = 0;
        if (resultSet.next()) {
            numero = resultSet.getInt(1);
        }
        resultSet.close();
        statement.close();
        ConexionBD.desconectar();
        return numero;
    }

    /**
     * Obtiene la lista completa de jugadores registrados en la base de datos.
     *
     * @return Lista de objetos JugadorVO.
     * @throws SQLException Si ocurre un error al ejecutar la consulta.
     */
    public ArrayList<JugadorVO> darListaGatos() throws SQLException {
        String consulta = "SELECT * FROM jugadores";
        connection = ConexionBD.getConnection();
        statement = connection.createStatement();
        resultSet = statement.executeQuery(consulta);
        ArrayList<JugadorVO> jugadores = new ArrayList<>();
        while (resultSet.next()) {
            JugadorVO jugador = new JugadorVO();
            jugador.setNombreJugador(resultSet.getString("nombreJugador"));
            jugador.setCedula(resultSet.getString("cedula"));
            jugador.setUsuario(resultSet.getString("usuario"));
            jugador.setContrasena(resultSet.getString("contrasena"));
            jugadores.add(jugador);
        }
        statement.close();
        ConexionBD.desconectar();
        return jugadores;
    }

    /**
     * Inserta un nuevo jugador en la base de datos.
     *
     * @param jugador Objeto JugadorVO con la información del jugador a registrar.
     * @throws SQLException Si ocurre un error al insertar.
     */
    public void insertarGato(JugadorVO jugador) throws SQLException {
        String insercion = "INSERT INTO `jugadores`(`nombreJugador`, `cedula`, `usuario`, `contrasena`) "
                + "VALUES ('" + jugador.getNombreJugador() + "'," + jugador.getCedula() + "," + jugador.getUsuario() + ",'"
                + jugador.getContrasena() + "')";
        connection = ConexionBD.getConnection();
        statement = connection.createStatement();
        statement.executeUpdate(insercion);
        statement.close();
        ConexionBD.desconectar();
    }

    /**
     * Elimina un jugador de la base de datos según su ID.
     *
     * @param cedula Identificador del jugador.
     * @throws SQLException Si ocurre un error al ejecutar la eliminación.
     */
    public void eliminarGato(int cedula) throws SQLException {
        String consulta = "DELETE FROM jugadores where cedula='" + cedula + "'";
        connection = ConexionBD.getConnection();
        statement = connection.createStatement();
        statement.executeUpdate(consulta);
        statement.close();
        ConexionBD.desconectar();

    }

    /**
     * Modifica un atributo específico de un jugador en la base de datos.
     *
     * @param cedula parametro identificador
     * @param factorACambiar es el dato que se busca cambiar
     * @param valorModificado Nuevo valor que se quiere asignar.
     * @throws SQLException Si ocurre un error al ejecutar la modificación.
     */
    public void modificarGato(int cedula, String factorACambiar, String valorModificado) throws SQLException {
        String consulta = "UPDATE jugadores SET " + factorACambiar + " = '" + valorModificado + "' WHERE cedula = '" + cedula + "'";
        connection = ConexionBD.getConnection();
        statement = connection.createStatement();
        statement.executeUpdate(consulta);
        statement.close();
        ConexionBD.desconectar();
    }
}
