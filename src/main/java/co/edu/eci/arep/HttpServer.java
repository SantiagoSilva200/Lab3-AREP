package co.edu.eci.arep;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;

public class HttpServer {

    private static final List<Student> estudiantes = new ArrayList<>();
    private static final Map<String, BiFunction<HttpRequest, HttpResponse, String>> routes = new HashMap<>();
    private static String staticFilesLocation = "/webroot";
    private static String appPrefix = "/App";

    static {
        estudiantes.add(new Student("Juan", "Pérez", "Ingeniería de Sistemas", true));
        estudiantes.add(new Student("Ana", "Gómez", "Medicina", false));
        estudiantes.add(new Student("Santiago", "Silva", "Ingeniería de Sistemas", true));
        estudiantes.add(new Student("María", "López", "Arquitectura", true));
        estudiantes.add(new Student("Carlos", "Martínez", "Derecho", false));
    }

    public static void staticfiles(String location) {
        staticFilesLocation = location;
        System.out.println("Static files location set to: " + staticFilesLocation);
    }

    public static void setAppPrefix(String prefix) {
        appPrefix = prefix;
        System.out.println("App prefix set to: " + appPrefix);
    }

    public static void get(String path, BiFunction<HttpRequest, HttpResponse, String> handler) {
        String fullPath = appPrefix + path;
        routes.put(fullPath, handler);
        System.out.println("Route registered: " + fullPath);
    }

    public static void main(String[] args) throws IOException {
        staticfiles("/webroot");
        get("/hello", (req, resp) -> "Hello " + req.getValues("name"));
        get("/pi", (req, resp) -> {
            return String.valueOf(Math.PI);
        });

        ServerSocket servidor = new ServerSocket(8080);
        System.out.println("Servidor iniciado en http://localhost:8080");
        System.out.println("Static files location: " + staticFilesLocation);
        System.out.println("REST services prefix: " + appPrefix);

        while (true) {
            Socket cliente = servidor.accept();
            BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            OutputStream salida = cliente.getOutputStream();

            String linea = entrada.readLine();
            if (linea == null) continue;

            String[] partes = linea.split(" ");
            String metodo = partes[0];
            String recursoCompleto = partes[1];

            String path = recursoCompleto;
            String query = null;
            int queryIndex = recursoCompleto.indexOf('?');
            if (queryIndex != -1) {
                path = recursoCompleto.substring(0, queryIndex);
                query = recursoCompleto.substring(queryIndex + 1);
            }

            HttpRequest request = new HttpRequest(path, query);
            HttpResponse response = new HttpResponse();

            if (metodo.equals("GET") && routes.containsKey(path)) {
                try {
                    String result = routes.get(path).apply(request, response);

                    if (path.equals("/App/hello") && request.getValues("name").isEmpty()) {
                        result = "hello world!";
                    }

                    String contentType = response.getContentType();
                    writeResponse(salida, response.getStatus(), contentType, result.getBytes(StandardCharsets.UTF_8));
                    closeStreams(entrada, salida, cliente);
                    continue;
                } catch (Exception e) {
                    writeResponse(salida, 500, "text/plain",
                            "Error interno del servidor".getBytes(StandardCharsets.UTF_8));
                    closeStreams(entrada, salida, cliente);
                    continue;
                }
            }

            if (metodo.equals("GET")) {
                String staticFilePath = getStaticFilePath(path);
                InputStream inputStream = HttpServer.class.getResourceAsStream(staticFilePath);

                if (inputStream != null) {
                    String mime = guessMime(staticFilePath);
                    String header = "HTTP/1.1 200 OK\r\nContent-Type: " + mime + "\r\n\r\n";
                    salida.write(header.getBytes());
                    inputStream.transferTo(salida);
                    inputStream.close();
                    closeStreams(entrada, salida, cliente);
                    continue;
                }
            }

            String respuesta = "HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n<h1>404 - Recurso no encontrado</h1>";
            salida.write(respuesta.getBytes());
            closeStreams(entrada, salida, cliente);
        }
    }

    private static String getStaticFilePath(String requestPath) {
        if (requestPath.equals("/")) {
            return staticFilesLocation + "/index.html";
        }
        return staticFilesLocation + requestPath;
    }

    private static void closeStreams(BufferedReader entrada, OutputStream salida, Socket cliente) throws IOException {
        salida.close();
        entrada.close();
        cliente.close();
    }

    private static String guessMime(String path) {
        path = path.toLowerCase();
        if (path.endsWith(".html")) return "text/html; charset=utf-8";
        if (path.endsWith(".css")) return "text/css; charset=utf-8";
        if (path.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif")) return "image/gif";
        if (path.endsWith(".ico")) return "image/x-icon";
        if (path.endsWith(".json")) return "application/json; charset=utf-8";
        if (path.endsWith(".txt")) return "text/plain; charset=utf-8";
        return "application/octet-stream";
    }

    private static void writeResponse(OutputStream out, int status, String contentType, byte[] body) throws IOException {
        String statusText = (status == 200) ? "OK" : "Error";
        String header = "HTTP/1.1 " + status + " " + statusText + "\r\nContent-Type: " + contentType + "\r\n\r\n";
        out.write(header.getBytes());
        out.write(body);
    }
}