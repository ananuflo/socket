package es.iescamas.socket;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletableFuture;

class HiloPorClienteServidorTest {

    private HiloPorClienteServidor server;
    private Thread serverThread;
    private int port;

    @BeforeEach
    void startServer() throws Exception {
        // Pedimos un puerto libre automático para evitar bloqueos
        try (ServerSocket tmp = new ServerSocket(0)) {
            port = tmp.getLocalPort();
        }
        server = new HiloPorClienteServidor(port);
        serverThread = new Thread(server, "test-server");
        serverThread.start();
        waitUntilListening("127.0.0.1", port, 1000);
    }

    @AfterEach
    void stopServer() throws Exception {
        server.stop();
        serverThread.join(500);
    }

    @Test
    @DisplayName("C1: GET /nombre/ana devuelve 200 OK y saludo")
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    @Tag("http")
    void shouldSayHello() throws Exception {
        String response = httpGet("/nombre/ana"); 
        assertTrue(response.contains("200 OK"), "Debe devolver 200 OK");
        assertTrue(response.contains("Hola, ana"), "Debe contener el saludo: Hola, ana");
    }

    @Test
    @DisplayName("C2.1: Ruta desconocida devuelve 404 Not Found")
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    @Tag("error")
    void shouldReturn404() throws Exception {
        String response = httpGet("/ruta/inexistente");
        assertTrue(response.contains("404 Not Found"), "Debe devolver error 404");
    }

    @Test
    @DisplayName("C2.2: Concurrencia con 2 clientes en paralelo")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    @Tag("concurrency")
    void shouldHandleConcurrentRequests() throws Exception {
        CompletableFuture<String> c1 = CompletableFuture.supplyAsync(() -> {
            try { return httpGet("/nombre/Juan"); } catch (Exception e) { return ""; }
        });
        CompletableFuture<String> c2 = CompletableFuture.supplyAsync(() -> {
            try { return httpGet("/nombre/Maria"); } catch (Exception e) { return ""; }
        });
        assertTrue(c1.get().contains("Hola, Juan"), "Debe saludar a Juan");
        assertTrue(c2.get().contains("Hola, Maria"), "Debe saludar a Maria");
    }

    private String httpGet(String path) throws Exception {
        try (Socket s = new Socket("127.0.0.1", port);
             OutputStream out = s.getOutputStream();
             InputStream in = s.getInputStream()) {
            String req = "GET " + path + " HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n";
            out.write(req.getBytes(StandardCharsets.US_ASCII));
            out.flush();
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void waitUntilListening(String host, int port, long maxMs) throws Exception {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < maxMs) {
            try (Socket ignored = new Socket(host, port)) { return; }
            catch (IOException e) { Thread.sleep(50); }
        }
    }
}
