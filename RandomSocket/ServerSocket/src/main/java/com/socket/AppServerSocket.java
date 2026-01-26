package com.socket;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppServerSocket {
    public static void main(String[] args) {
        int puerto = 5000;
        int numMagico = (int) (Math.random() * 10) + 1; 

        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor iniciado. Esperando cliente...");

            try (Socket socket = servidor.accept();
                 BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)) {

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                System.out.println("[" + dtf.format(LocalDateTime.now()) + "] Cliente conectado desde: " + socket.getInetAddress());

                String texto;
                while ((texto = entrada.readLine()) != null) {
                    int intento = Integer.parseInt(texto);
                    
                    if (intento == numMagico) {
                        salida.println("ACIERTO"); 
                        System.out.println("¡El cliente ha acertado! Cerrando conexión...");
                        break;
                    } else if (intento < numMagico) {
                        salida.println("MAYOR");
                    } else {
                        salida.println("MENOR");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}