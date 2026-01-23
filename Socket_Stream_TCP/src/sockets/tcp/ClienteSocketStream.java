package sockets.tcp;
import java.io.IOException;
import java.io.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClienteSocketStream {

    public static void main(String[] args) {
    	try (Socket clientSocket = new Socket()) {
            InetSocketAddress addr = new InetSocketAddress("localhost", 5555);
            clientSocket.connect(addr);

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // 1. ENVIAR (Petición)
            String mensaje = "Hola servidor";
            out.println(mensaje); // println añade el \n automático
            System.out.println("Mensaje enviado");

            // 2. RECIBIR (Respuesta)
            // El cliente se bloquea aquí hasta que el servidor responda con un \n
            String respuesta = in.readLine(); 
            System.out.println("Respuesta del servidor: " + respuesta);

        } catch (IOException e) { e.printStackTrace(); }
    }
}