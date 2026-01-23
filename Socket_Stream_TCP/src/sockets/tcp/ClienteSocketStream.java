package sockets.tcp;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClienteSocketStream {

    public static void main(String[] args) {
        String ipVM = "192.168.1.35"; 
        int puerto = 7777;

        try (Socket clientSocket = new Socket();
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Conectando al servidor...");
            clientSocket.connect(new InetSocketAddress(ipVM, puerto));
            
            // Importante: autoFlush en true para que el servidor reciba el mensaje al instante
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Log para identificar esta instancia específica (útil si abres varios clientes)
            System.out.println("--- CONECTADO AL SERVIDOR CONCURRENTE ---");
            System.out.println("Puerto local asignado: " + clientSocket.getLocalPort());
            System.out.println("Escribe HELP para ver opciones o EXIT para salir");

            boolean activo = true;
            while (activo) {
                System.out.print("> ");
                String comando = sc.nextLine(); 
                
                out.println(comando); // Envía comando + \n

                String respuesta = in.readLine(); // Bloqueo: espera respuesta del hilo del servidor
                System.out.println("SERVIDOR responde: " + respuesta);

                if (comando.equalsIgnoreCase("EXIT") || (respuesta != null && respuesta.equals("BYE"))) {
                    activo = false;
                }
            }
            System.out.println("Conexión finalizada por el usuario.");

        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}