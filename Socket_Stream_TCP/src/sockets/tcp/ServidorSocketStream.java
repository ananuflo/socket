package sockets.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class ServidorSocketStream {

    public static void main(String[] args) {
    	int puerto = 5555; // Asegúrate de que coincida con el cliente

        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress("0.0.0.0", puerto));
            System.out.println("Servidor esperando en puerto " + puerto);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     // Usamos BufferedReader para leer líneas completas (hasta el \n)
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     // Usamos PrintWriter para enviar texto con auto-flush
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String mensaje = in.readLine(); // Bloqueo: espera hasta que el cliente mande \n
                    if (mensaje != null) {
                        System.out.println("Cliente dice: " + mensaje);
                        
                        // ENVIAR RESPUESTA (Aquí se cumple el protocolo)
                        out.println("OK: " + mensaje); 
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
