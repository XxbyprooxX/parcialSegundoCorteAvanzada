/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.control;

/**
 *
 * @author Andres Felipe
 */
public class ClienteHilo extends Thread {
    
    public ControlCliente controlCliente;

    public ClienteHilo(ControlCliente controlCliente) {
        this.controlCliente = controlCliente;
    }
    
}
