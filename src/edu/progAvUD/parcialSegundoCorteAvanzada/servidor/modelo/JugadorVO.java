package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo;

/**
 * La clase **`JugadorVO`** (Value Object) representa un objeto de valor que encapsula la información
 * de un jugador en el sistema. Incluye datos de identificación personal, credenciales de acceso,
 * y estadísticas de juego (intentos y parejas resueltas).
 * Es utilizada para transferir datos de jugadores entre capas de la aplicación.
 *
 * @author Andres Felipe
 */
public class JugadorVO {

    /**
     * El nombre completo del jugador.
     */
    private String nombreJugador;

    /**
     * El número de cédula de identidad del jugador.
     */
    private String cedula;

    /**
     * El nombre de usuario que el jugador utiliza para iniciar sesión.
     */
    private String usuario;

    /**
     * La contraseña del jugador. (En una aplicación real, las contraseñas deberían estar hasheadas y no almacenadas en texto plano).
     */
    private String contrasena;

    /**
     * Identificador único del jugador en el sistema.
     */
    private int id;

    /**
     * La cantidad de intentos realizados por el jugador en el juego.
     */
    private int cantidadIntentos;

    /**
     * La cantidad de parejas de cartas que el jugador ha resuelto correctamente en el juego.
     */
    private int cantidadParejasResueltas;

    /**
     * Constructor completo para inicializar todas las propiedades de un objeto `JugadorVO`.
     *
     * @param nombreJugador El nombre completo del jugador.
     * @param cedula El número de cédula del jugador.
     * @param usuario El nombre de usuario del jugador.
     * @param contrasena La contraseña del jugador.
     * @param id El identificador único del jugador.
     * @param cantidadIntentos La cantidad de intentos del jugador en el juego.
     * @param cantidadParejasResueltas La cantidad de parejas resueltas por el jugador en el juego.
     */
    public JugadorVO(String nombreJugador, String cedula, String usuario, String contrasena, int id, int cantidadIntentos, int cantidadParejasResueltas) {
        this.nombreJugador = nombreJugador;
        this.cedula = cedula;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.id = id;
        this.cantidadIntentos = cantidadIntentos;
        this.cantidadParejasResueltas = cantidadParejasResueltas;
    }   

    /**
     * Constructor vacío para crear un objeto `JugadorVO` sin inicializar sus propiedades.
     * Útil cuando se van a establecer las propiedades mediante los métodos `set`.
     */
    public JugadorVO() {
    }
    
    /**
     * Obtiene el nombre completo del jugador.
     *
     * @return El nombre del jugador.
     */
    public String getNombreJugador() {
        return nombreJugador;
    }

    /**
     * Establece el nombre completo del jugador.
     *
     * @param nombreJugador El nuevo nombre del jugador.
     */
    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    /**
     * Obtiene el identificador único del jugador.
     *
     * @return El ID del jugador.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el identificador único del jugador.
     *
     * @param id El nuevo ID del jugador.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el número de cédula del jugador.
     *
     * @return La cédula del jugador.
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * Establece el número de cédula del jugador.
     *
     * @param cedula La nueva cédula del jugador.
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * Obtiene el nombre de usuario del jugador.
     *
     * @return El nombre de usuario.
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Establece el nombre de usuario del jugador.
     *
     * @param usuario El nuevo nombre de usuario.
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * Obtiene la contraseña del jugador.
     *
     * @return La contraseña.
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * Establece la contraseña del jugador.
     *
     * @param contrasena La nueva contraseña.
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Obtiene la cantidad de intentos que el jugador ha realizado.
     *
     * @return La cantidad de intentos.
     */
    public int getCantidadIntentos() {
        return cantidadIntentos;
    }

    /**
     * Establece la cantidad de intentos que el jugador ha realizado.
     *
     * @param cantidadIntentos La nueva cantidad de intentos.
     */
    public void setCantidadIntentos(int cantidadIntentos) {
        this.cantidadIntentos = cantidadIntentos;
    }

    /**
     * Obtiene la cantidad de parejas de cartas que el jugador ha resuelto.
     *
     * @return La cantidad de parejas resueltas.
     */
    public int getCantidadParejasResueltas() {
        return cantidadParejasResueltas;
    }

    /**
     * Establece la cantidad de parejas de cartas que el jugador ha resuelto.
     *
     * @param cantidadParejasResueltas La nueva cantidad de parejas resueltas.
     */
    public void setCantidadParejasResueltas(int cantidadParejasResueltas) {
        this.cantidadParejasResueltas = cantidadParejasResueltas;
    }
}