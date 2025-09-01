package co.edu.eci.arep;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;

public class HttpServer {

    public static final Map<String, BiFunction<HttpRequest, HttpResponse, String>> routes = new HashMap<>();
    private static String staticFilesLocation;

    public static void staticfiles(String location) {
        staticFilesLocation = location;
    }

    public static void get(String path, BiFunction<HttpRequest, HttpResponse, String> handler) {
        routes.put(path, handler);
    }

    public static void runServer(String[] args) throws IOException {
        String projectRoot = new File("").getAbsolutePath();
        staticfiles(projectRoot + "/src/main/resources/webroot");

        ServerSocket servidor = new ServerSocket(35000);
        System.out.println("Servidor iniciado en http://localhost:35000");

        while (true) {
            Socket cliente = servidor.accept();
            BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            OutputStream salida = cliente.getOutputStream();

            try {
                String linea = entrada.readLine();
                if (linea == null) {
                    closeStreams(entrada, salida, cliente);
                    continue;
                }

                String[] partes = linea.split(" ");
                String metodo = partes[0];
                String recursoCompleto = partes[1];

                String path = recursoCompleto.split("\\?")[0];
                String query = null;
                int queryIndex = recursoCompleto.indexOf('?');
                if (queryIndex != -1) {
                    query = recursoCompleto.substring(queryIndex + 1);
                }

                HttpRequest request = new HttpRequest(path, query);
                HttpResponse response = new HttpResponse();

                if (metodo.equals("GET") && routes.containsKey(path)) {
                    try {
                        String result = routes.get(path).apply(request, response);
                        writeResponse(salida, 200, "text/plain", result.getBytes(StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        writeResponse(salida, 500, "text/plain", "Error interno".getBytes(StandardCharsets.UTF_8));
                    }
                    continue;
                }

                if (metodo.equals("GET")) {
                    String filePath = getStaticFilePath(path);
                    File file = new File(staticFilesLocation + filePath);
                    if (file.exists() && file.isFile()) {
                        try (InputStream inputStream = new FileInputStream(file)) {
                            String mime = guessMime(file.getName());
                            writeResponse(salida, 200, mime, inputStream.readAllBytes());
                        }
                        continue;
                    }
                }

                writeResponse(salida, 404, "text/html",
                        "<h1>404 - Recurso no encontrado</h1>".getBytes(StandardCharsets.UTF_8));

            } finally {
                closeStreams(entrada, salida, cliente);
            }
        }
    }

    private static String getStaticFilePath(String requestPath) {
        return requestPath.equals("/") ? "/index.html" : requestPath;
    }

    private static void closeStreams(BufferedReader entrada, OutputStream salida, Socket cliente) throws IOException {
        salida.close();
        entrada.close();
        cliente.close();
    }

    private static String guessMime(String filename) {
        if (filename.endsWith(".html")) return "text/html; charset=utf-8";
        if (filename.endsWith(".css")) return "text/css; charset=utf-8";
        if (filename.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        return "text/plain; charset=utf-8";
    }

    private static void writeResponse(OutputStream out, int status, String contentType, byte[] body) throws IOException {
        String statusText = status == 200 ? "OK" : "Not Found";
        String header = "HTTP/1.1 " + status + " " + statusText + "\r\nContent-Type: " + contentType + "\r\n\r\n";
        out.write(header.getBytes());
        out.write(body);
    }
}