package edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo;

/**
 *
 * @author Cristianlol789
 */
public class Carta {
    
    private String tipoObjeto;
    private int id;

    public Carta(String tipoObjeto, int id) {
        this.tipoObjeto = tipoObjeto;
        this.id = id;
    }

    public String getTipoObjeto() {
        return tipoObjeto;
    }

    public void setTipoObjeto(String tipoObjeto) {
        this.tipoObjeto = tipoObjeto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
}