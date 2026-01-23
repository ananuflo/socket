package sockets.tcp;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

public class ServidorSocketStream {

    public static void main(String[] args) {
        int puerto = 7777; 

        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress("0.0.0.0", puerto));
            System.out.println("Servidor de Comandos iniciado en puerto " + puerto);

            while (true) {
                System.out.println("Esperando nuevo cliente...");
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
                    
                    boolean continuar = true;
                    while (continuar) {
                        String linea = in.readLine();
                        if (linea == null) break; // Si el cliente corta la conexión

                        String comando = linea.trim().toUpperCase();
                        String respuesta;
                        
                        // LOG: Depuración de comando recibido
                        System.out.println("Comando recibido: " + comando);

                        switch (comando) {
                            case "DATE":
                                respuesta = LocalDateTime.now().toString();
                                break;
                            case "CAL":
                                respuesta = "ENERO 2026: L M X J V S D (Simulado)";
                                break;
                            case "FORTUNE":
                                String[] frases = {"La suerte favorece a los audaces", "Hoy aprenderas algo nuevo", "Java es tu amigo"};
                                respuesta = frases[(int) (Math.random() * frases.length)];
                                break;
                            case "HELP":
                                respuesta = "Comandos: DATE, CAL, FORTUNE, HELP, EXIT";
                                break;
                            case "EXIT":
                                respuesta = "BYE";
                                continuar = false;
                                break;
                            default:
                                respuesta = "ERROR: Comando no reconocido. Escribe HELP.";
                        }

                        // Enviar respuesta y LOG de bytes
                        out.println(respuesta);
                        System.out.println("Respuesta enviada (" + respuesta.getBytes().length + " bytes)");
                    }
                    System.out.println("Sesión finalizada con el cliente.");
                } catch (IOException e) { System.out.println("Cliente desconectado."); }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
