package sockets.tcp;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClienteSocketStream {

    public static void main(String[] args) {
    	
        try {
        	
        	//Cambiamos IP y puerto
        	//Lo he hecho apagando la VM y cambiado la red a adaptador de puente, luego en la terminal pongo hostname -I y ya me sale
        	String ipVM = "192.168.1.35";
        	int puerto = 7777; //usamos el mismo puerto de la clase ServidorSocketStream que cambiamos
        	
            System.out.println("Creando socket cliente");
            Socket clientSocket = new Socket();

            System.out.println("Estableciendo la conexión en: " + ipVM + ":" + puerto);

            InetSocketAddress addr = new InetSocketAddress(ipVM, puerto);
            clientSocket.connect(addr);

            //Los streams para comunicarnos
            InputStream is = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();

            System.out.println("Enviando mensaje");

            String mensaje = "mensaje desde el cliente";
            os.write(mensaje.getBytes());

            System.out.println("Mensaje enviado");

            System.out.println("Cerrando el socket cliente");
            clientSocket.close();

            System.out.println("Terminado");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}