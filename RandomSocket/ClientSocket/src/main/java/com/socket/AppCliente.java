package com.socket;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class AppCliente {
    public static void main(String[] args) {
        String host = "127.0.0.1"; 
        int puerto = 5000;

        try (Socket socket = new Socket(host, puerto);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
             Scanner teclado = new Scanner(System.in)) {

            System.out.println("Conectado al servidor. Adivina el número (1-10):");

            while (true) {
                System.out.print("Introduce tu número: ");
                int num = teclado.nextInt();

                if (num < 1 || num > 10) {
                    System.out.println("Error: El número debe estar entre 1 y 10. Inténtalo de nuevo.");
                    continue;
                }

                salida.println(num);
                String respuesta = entrada.readLine();

                if (respuesta.equals("ACIERTO")) {
                    System.out.println("¡FELICIDADES! Has ganado.");
                    break;
                } else {
                    System.out.println("El número secreto es " + respuesta + " que el tuyo.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}
