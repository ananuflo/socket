package sockets.tcp;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorSocketStream {
    public static void main(String[] args) {
        int puerto = 7777;
        // Creamos un pool de 10 hilos fijos
        ExecutorService pool = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress("0.0.0.0", puerto));
            System.out.println("Servidor CONCURRENTE iniciado en puerto " + puerto);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Acepta y sigue
                // Delegamos la atención al pool de hilos
                pool.submit(() -> atenderCliente(clientSocket));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static void atenderCliente(Socket clientSocket) {
        String nombreHilo = Thread.currentThread().getName(); // Log obligatorio
        try (clientSocket;
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            System.out.println("[" + nombreHilo + "] Cliente conectado: " + clientSocket.getInetAddress());
            
            String linea;
            while ((linea = in.readLine()) != null) {
                String comando = linea.trim().toUpperCase();
                // LOG: Nombre hilo, remoto y comando
                System.out.println("[" + nombreHilo + "] Comando: " + comando);

                if (comando.equals("EXIT")) {
                    out.println("BYE");
                    break;
                }
                out.println(procesarComando(comando));
            }
        } catch (IOException e) {
            System.out.println("[" + nombreHilo + "] Error con cliente.");
        }
    }

    private static String procesarComando(String cmd) {
        switch (cmd) {
            case "DATE": return LocalDateTime.now().toString();
            case "FORTUNE": return "La persistencia vence al talento.";
            case "HELP": return "DATE, FORTUNE, HELP, EXIT";
            default: return "ERROR: Comando desconocido";
        }
    }
}
