package es.iescamas.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Servidor HTTP Concurrente Mejorado - Práctica 4
 * @author Ana
 */
public class HiloPorClienteServidor implements Runnable {

    protected int serverPort = 9001;
    protected ServerSocket serversocket = null;
    protected boolean isStopped;
    protected Thread runningThread = null;

    // --- VARIABLES PARA MEJORA 3 (Estadísticas) ---
    private static final long startTime = System.currentTimeMillis();
    private static int peticionesTotales = 0;

    public HiloPorClienteServidor(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();

        while (!isStopped()) {
            try {
                Socket clientSocket = this.serversocket.accept();
                // Creación del hilo por cliente
                new Thread(() -> {
                    try {
                        processClientRequest(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, "client-" + clientSocket.getPort()).start();

            } catch (IOException e) {
                if (isStopped()) return;
                throw new RuntimeException("Error aceptando conexión", e);
            }
        }
    }

    private void processClientRequest(Socket clientSocket) throws IOException {
        try (clientSocket;
             InputStream in = clientSocket.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII));
             OutputStream out = clientSocket.getOutputStream()) {

            String requestLine = br.readLine();
            if (requestLine == null || requestLine.isBlank()) return;

            // Extraer el path de la petición
            String path = "/";
            if (requestLine.startsWith("GET ")) {
                int start = 4;
                int end = requestLine.indexOf(' ', start);
                if (end > start) path = requestLine.substring(start, end);
            }

            if ("/favicon.ico".equals(path)) {
                serveFavicon(out);
                return;
            }

            // --- LÓGICA DE LAS MEJORAS ---
            
            // Incremento global de peticiones (Mejora 3)
            synchronized(this) { peticionesTotales++; }
            long uptimeSegundos = (System.currentTimeMillis() - startTime) / 1000;

            String saludo = "";
            String contenidoExtra = "";
            String status = "200 OK";

            // Enrutamiento (Mejora 2 y Mejora 1)
            if (path.equals("/")) {
                saludo = "🏠 Página de Inicio";
                contenidoExtra = "<h3>Menú de Navegación:</h3>"
                               + "<ul>"
                               + "<li><a href='/nombre/Ana'>Saludar a Ana (Mi nombre)</a></li>"
                               + "<li><a href='/nombre/Invitado'>Probar otro nombre</a></li>"
                               + "<li><a href='/ruta-falsa'>Probar Error 404</a></li>"
                               + "</ul>";
            } else if (path.startsWith("/nombre/")) {
                // Mejora 1: Saludo dinámico
                String nombreExtraido = path.substring(8);
                saludo = "👋 Hola, " + nombreExtraido;
                contenidoExtra = "<p><a href='/'>⬅ Volver al inicio</a></p>";
            } else {
                // Gestión de rutas no encontradas
                saludo = "⚠️ Error 404: No encontrado";
                status = "404 Not Found";
                contenidoExtra = "<p>La ruta <b>" + path + "</b> no existe.</p>"
                               + "<p><a href='/'>⬅ Volver al inicio</a></p>";
            }

            // Datos del cliente e hilos
            String clientIp = clientSocket.getInetAddress().getHostAddress();
            String fecha = new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(new Date());

            // Construcción del HTML Final
            String body = "<html>"
                    + "<head><title>Servidor de Ana - PSP</title></head>"
                    + "<body style='background-color: coral; font-family: sans-serif; padding: 30px;'>"
                    + "<h1 style='color: white; text-shadow: 2px 2px 4px #000;'>" + saludo + "</h1>"
                    + "<hr>"
                    + contenidoExtra
                    + "<div style='background: rgba(255,255,255,0.3); padding: 15px; border-radius: 10px; margin-top: 20px;'>"
                    + "<h3>🔍 Información Técnica:</h3>"
                    + "<p><b>Hilo actual:</b> " + Thread.currentThread().getName() + "</p>"
                    + "<p><b>Tu IP:</b> " + clientIp + "</p>"
                    + "<p><b>Fecha servidor:</b> " + fecha + "</p>"
                    + "<p>📊 <b>Estadísticas:</b> Visitas: " + peticionesTotales + " | Activo: " + uptimeSegundos + "s</p>"
                    + "</div>"
                    + "</body></html>";

            byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

            // Headers con status dinámico
            String headers = "HTTP/1.1 " + status + "\r\n" +
                             "Content-Type: text/html; charset=UTF-8\r\n" +
                             "Content-Length: " + bodyBytes.length + "\r\n" +
                             "Connection: close\r\n\r\n";

            out.write(headers.getBytes(StandardCharsets.US_ASCII));
            out.write(bodyBytes);
            out.flush();
            
            System.out.println("[" + Thread.currentThread().getName() + "] Atendida petición: " + path);
        }
    }

    // --- MÉTODOS AUXILIARES ---

    private void serveFavicon(OutputStream out) throws IOException {
        try (InputStream iconStream = HiloPorClienteServidor.class.getResourceAsStream("/favicon.ico")) {
            if (iconStream == null) {
                out.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
                return;
            }
            byte[] iconBytes = iconStream.readAllBytes();
            String headers = "HTTP/1.1 200 OK\r\nContent-Type: image/x-icon\r\nContent-Length: " + iconBytes.length + "\r\n\r\n";
            out.write(headers.getBytes());
            out.write(iconBytes);
            out.flush();
        }
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }
    
    private void openServerSocket() {
        try {
            this.serversocket = new ServerSocket(this.serverPort);
        } catch (IOException ex) {
            throw new RuntimeException("No se puede abrir el puerto " + serverPort, ex);
        }
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            if (this.serversocket != null) this.serversocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
