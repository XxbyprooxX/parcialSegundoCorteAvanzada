/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progAvUD.parcialSegundoCorteAvanzada.cliente.modelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * Clase que permite cargar propiedades desde un archivo .properties.
 * Utiliza un flujo de entrada para leer los datos de configuración.
 * 
 * @author Andres Felipe
 */
public class ConexionPropiedades {

    // Flujo de entrada para leer el archivo de propiedades
    private FileInputStream fileInPropiedades;

    /**
     * Constructor que recibe el archivo de propiedades y lo abre para lectura.
     * 
     * @param archivo Archivo .properties que contiene las configuraciones.
     * @throws FileNotFoundException Si el archivo no se encuentra.
     */
    public ConexionPropiedades(File archivo) throws FileNotFoundException {
        this.fileInPropiedades = new FileInputStream(archivo);
    }

    /**
     * Método que carga las propiedades desde el archivo abierto.
     * 
     * @return Objeto Properties con las claves y valores cargados desde el archivo.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public Properties cargarPropiedades() throws IOException {
        Properties propiedades = new Properties();
        propiedades.load(fileInPropiedades); // Carga el contenido del archivo al objeto Properties
        fileInPropiedades.close(); // Cierra el flujo después de la lectura
        return propiedades;
    }
}