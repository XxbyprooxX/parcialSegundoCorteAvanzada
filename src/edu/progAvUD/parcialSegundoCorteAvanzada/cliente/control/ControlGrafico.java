/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.control;

import edu.progAvUD.parcialSegundoCorteAvanzada.cliente.vista.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Andres Felipe
 */
public class ControlGrafico implements ActionListener{
    
    private ControlPrincipal controlPrincipal;
    private VentanaPrincipal ventanaPrincipal;
    

    public ControlGrafico(ControlPrincipal controlPrincipal) {
        this.controlPrincipal = controlPrincipal;
        this.ventanaPrincipal = new VentanaPrincipal();
        
        for (int i = 0; i <= 39; i++) {
            ventanaPrincipal.panelJuego.anadirBoton(i);
        }
        
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
            
        
    
}
