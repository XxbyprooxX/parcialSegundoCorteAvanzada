/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.control;

import java.io.DataInputStream;

/**
 *
 * @author Andres Felipe
 */
public class ThreadCliente extends Thread {
    
    public DataInputStream entrada;
    
    public ControlCliente controlCliente;

    public ThreadCliente(DataInputStream entrada, ControlCliente controlCliente) {
        this.entrada = entrada;
        this.controlCliente = controlCliente;
    }

   
    
}
