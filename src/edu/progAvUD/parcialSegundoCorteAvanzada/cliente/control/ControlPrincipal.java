/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.servidor.modelo.ConexionPropiedades;

/**
 *
 * @author Andres Felipe
 */
public class ControlPrincipal {
    
    private ControlGrafico controlGrafico;

    public ControlPrincipal() {
        this.controlGrafico = new ControlGrafico(this);
    }
    
    public ConexionPropiedades crearConexionPropiedades(){
        ConexionPropiedades conexionPropiedades = null;
        boolean flag = true;
        do{
            try{
                conexionPropiedades = new ConexionPropiedades(controlGrafico.pedirArchivoPropiedades());
                if (conexionPropiedades != null) {
                    flag = false;
                }
            }catch(Exception e){
                controlGrafico.mostrarMensajeError("Ocurrio un error en el archivo de propiedades");
            }
        }while(flag);
        return conexionPropiedades;
    }
    
    
}
