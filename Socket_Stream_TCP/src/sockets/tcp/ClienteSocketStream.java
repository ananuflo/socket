package sockets.tcp;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClienteSocketStream {

    public static void main(String[] args) {
        String ipVM = "192.168.1.35"; // Tu IP real de la VM
        int puerto = 7777;

        try (Socket clientSocket = new Socket();
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Conectando al servidor...");
            clientSocket.connect(new InetSocketAddress(ipVM, puerto));
            
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            System.out.println("--- CONECTADO AL SERVIDOR DE COMANDOS ---");
            System.out.println("Escribe HELP para ver opciones o EXIT para salir");

            boolean activo = true;
            while (activo) {
                System.out.print("> ");
                String comando = sc.nextLine(); // Leemos de tu teclado
                
                out.println(comando); // Enviamos al servidor

                String respuesta = in.readLine(); // Leemos la respuesta del servidor
                System.out.println("SERVIDOR: " + respuesta);

                if (comando.equalsIgnoreCase("EXIT") || respuesta.equals("BYE")) {
                    activo = false;
                }
            }
            System.out.println("Conexión cerrada.");

        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}