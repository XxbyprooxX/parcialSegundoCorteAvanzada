CREATE DATABASE IF NOT EXISTS datosJugadores;

USE datosJugadores;

CREATE TABLE IF NOT EXISTS jugadores (
    id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    nombreJugador VARCHAR(35) NOT NULL,
    cedula VARCHAR(35) NOT NULL,
    usuario VARCHAR(20) NOT NULL,
    contrasena VARCHAR(20) NOT NULL
);