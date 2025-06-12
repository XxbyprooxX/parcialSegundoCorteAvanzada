package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.ConexionBD;
import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.ConexionPropiedades;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Andres Felipe
 */
public class ControlPrincipal {

    private ControlGrafico controlGrafico;

    public ControlPrincipal() {
        this.controlGrafico = new ControlGrafico(this);
    }

    /**
     * Este metodo se encarga de crear la conexion con las propiedades
     *
     * @return la conexion
     */
    public ConexionPropiedades crearConexionPropiedades() {
        ConexionPropiedades conexionPropiedades = null;
        boolean flag = true;
        do {
            try {
                conexionPropiedades = new ConexionPropiedades(controlGrafico.pedirArchivoPropiedades());
                if (conexionPropiedades != null) {
                    flag = false;
                }
            } catch (Exception ex) {
                controlGrafico.mostrarMensajeError("No se pudo crear la conexion correctamente");
            }
        } while (flag);

        return conexionPropiedades;
    }
    /**
     * Este metodo se encarga de cargar las propiedades de la base de datos
     */
    public void cargarDatosBD() {
        ConexionPropiedades conexionPropiedades = crearConexionPropiedades();
        try {
            Properties propiedadesBD = conexionPropiedades.cargarPropiedades();
            String URLBD = propiedadesBD.getProperty("URLBD");
            String usuario = propiedadesBD.getProperty("usuario");
            String contrasena = propiedadesBD.getProperty("contrasena");
            ConexionBD.setURLBD(URLBD);
            ConexionBD.setUsuario(usuario);
            ConexionBD.setContrasena(contrasena);

        } catch (IOException ex) {
            controlGrafico.mostrarMensajeError("No se pudo cargar el archivo propiedades de la Base de Datos");
        }
    }

    /**
     * Carga los datos de los jugadores desde las propiedades
     */
    public void cargarDatosJugadoresPropiedades() {
        ConexionPropiedades conexionPropiedades = crearConexionPropiedades();
        boolean flag = true;
        do {
            try {
                Properties propiedadesJugadores = conexionPropiedades.cargarPropiedades();
                int cantidadDeJugadoresRegistrar = Integer.parseInt(propiedadesJugadores.getProperty("cantidadJugadoresARegistrar"));
                for (int i = 1; i <= cantidadDeJugadoresRegistrar; i++) {
                    String nombreJugador = propiedadesJugadores.getProperty("jugador" + i + ".nombre");
                    
                }
                flag = false;
                controlGrafico.mostrarMensajeExito("Se han creado correctamente los jugadores");
            } catch (IOException ex) {
                controlGrafico.mostrarMensajeError("No se pudo cargar el archivo propiedades de los jugadores");
                System.exit(0);
            } catch (NumberFormatException ex) {
                controlGrafico.mostrarMensajeError("El texto no es un valor valido");
                System.exit(0);
            } catch (Exception ex) {
                controlGrafico.mostrarMensajeError("Algun dato del jugador no corresponde");
                System.exit(0);
            }
        } while (flag);
    }
}
