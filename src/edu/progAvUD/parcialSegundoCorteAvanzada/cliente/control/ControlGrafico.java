/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.cliente.vista.VentanaPrincipal;

/**
 *
 * @author Andres Felipe
 */
public class ControlGrafico {
    
    private ControlPrincipal controlPrincipal;
    private VentanaPrincipal ventanaPrincipal;
    

    public ControlGrafico(ControlPrincipal controlPrincipal) {
        this.controlPrincipal = controlPrincipal;
        this.ventanaPrincipal = new VentanaPrincipal();
    }
            
            
    
}
