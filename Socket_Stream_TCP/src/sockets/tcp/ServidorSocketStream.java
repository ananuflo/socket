package sockets.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.ServerSocket;

public class ServidorSocketStream {

    public static void main(String[] args) {
    	
    	int puerto = 7777; //Definimos puerto y usamos uno distinto para demostrar el cambio. 
    	
        try {
            System.out.println("Creando socket servidor");

            ServerSocket serverSocket = new ServerSocket();

            System.out.println("Realizando el bind");

            InetSocketAddress addr = new InetSocketAddress("0.0.0.0", puerto);
            serverSocket.bind(addr);
            
            System.out.println("Servidor esperando conexiones en el puerto: " + puerto);

            //Aceptar las conexiones
            System.out.println("Aceptando conexiones");

            Socket newSocket = serverSocket.accept();
            System.out.println("Conexión recibida");
            
            System.out.println("IP del CLiente remoto: " + newSocket.getInetAddress().getHostAddress());
            System.out.println("Puerto remoto del cliente: " + newSocket.getPort());

            InputStream is = newSocket.getInputStream();
            byte[] mensaje = new byte[25];
            is.read(mensaje); //Para leer el mensaje del flujo de bytes
            
            OutputStream os = newSocket.getOutputStream();
            String respuesta = "Mensaje recibido correctamente";
            os.write(respuesta.getBytes()); //Para enviar la respuesta al cliente de la información que ha recibido.

            

            System.out.println("Cerrando el nuevo socket");
            newSocket.close();

            System.out.println("Cerrando el socket servidor");
            serverSocket.close();

            System.out.println("Servidor finalizado");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}