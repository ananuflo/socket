package sockets.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.ServerSocket;

public class ServidorSocketStream {

    public static void main(String[] args) {
    	int puerto = 7777;

        // "try-with-resources": Cierra automáticamente el ServerSocket al terminar
        try (ServerSocket serverSocket = new ServerSocket()) {
            
            InetSocketAddress addr = new InetSocketAddress("0.0.0.0", puerto);
            serverSocket.bind(addr);
            System.out.println("Servidor iniciado en puerto " + puerto);

            while (true) { // Bucle para que no se cierre tras el primer cliente
                System.out.println("Esperando cliente...");
                
                try (Socket newSocket = serverSocket.accept();
                     InputStream is = newSocket.getInputStream()) {
                    
                    System.out.println("Conexión desde: " + newSocket.getInetAddress());

                    byte[] buffer = new byte[25];
                    
                    // SOLUCIÓN CORRECTA: Guardamos cuántos bytes se han leído realmente
                    int bytesLeidos = is.read(buffer); 

                    if (bytesLeidos != -1) {
                        // Usamos el constructor String(byte[], offset, length) 
                        // para evitar la "basura" del resto del buffer
                        String mensajeLimpio = new String(buffer, 0, bytesLeidos);
                        System.out.println("Mensaje recibido (sin basura): [" + mensajeLimpio + "]");
                        System.out.println("Bytes reales leídos: " + bytesLeidos);
                    }
                } 
                // Aquí el newSocket se cierra automáticamente gracias al try-with-resources
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}