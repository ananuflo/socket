package sockets.tcp;

import java.io.*;
import java.net.*;

public class ClienteCarga {
    public static void main(String[] args) {
        String ipVM = "192.168.1.35";
        int puerto = 7777;

        for (int i = 1; i <= 10; i++) {
            final int idCliente = i;
            new Thread(() -> {
                try (Socket s = new Socket(ipVM, puerto);
                     PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                    
                    System.out.println("Cliente de carga #" + idCliente + " iniciando...");
                    
                    // Cada cliente envía 3 comandos y sale
                    out.println("DATE"); 
                    System.out.println("C#" + idCliente + " R: " + in.readLine());
                    
                    out.println("FORTUNE");
                    System.out.println("C#" + idCliente + " R: " + in.readLine());
                    
                    out.println("EXIT");
                    System.out.println("C#" + idCliente + " finalizado.");
                    
                } catch (IOException e) {
                    System.err.println("Error en cliente #" + idCliente + ": " + e.getMessage());
                }
            }).start();
        }
    }
}
