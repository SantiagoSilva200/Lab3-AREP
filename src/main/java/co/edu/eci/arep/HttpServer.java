package co.edu.eci.arep;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpServer {

    private static final List<Student> estudiantes = new ArrayList<>();

    static {
        estudiantes.add(new Student("Juan", "Pérez", "Ingeniería de Sistemas", true));
        estudiantes.add(new Student("Ana", "Gómez", "Medicina", false));
        estudiantes.add(new Student("Santiago", "Silva", "Ingeniería de Sistemas", true));
        estudiantes.add(new Student("María", "López", "Arquitectura", true));
        estudiantes.add(new Student("Carlos", "Martínez", "Derecho", false));
    }

    public static void main(String[] args) throws IOException {
        ServerSocket servidor = new ServerSocket(8080);
        System.out.println("Servidor iniciado en http://localhost:8080");

        while (true) {
            Socket cliente = servidor.accept();
            BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            OutputStream salida = cliente.getOutputStream();

            String linea = entrada.readLine();
            if (linea == null) continue;
            System.out.println("Petición: " + linea);

            String[] partes = linea.split(" ");
            String metodo = partes[0];
            String recurso = partes[1];

            if (recurso.equals("/")) recurso = "/index.html";
            if (recurso.startsWith("/registrar") && metodo.equals("POST")) {
                Map<String, String> params = leerQuery(recurso);
                String nombre = params.getOrDefault("nombre", "");
                String apellido = params.getOrDefault("apellido", "");
                String carrera = params.getOrDefault("carrera", "");
                boolean matriculado = params.getOrDefault("matriculado","true").equalsIgnoreCase("true");

                estudiantes.add(new Student(nombre, apellido, carrera, matriculado));

                String respuesta = "Estudiante registrado: " + nombre + " " + apellido;
                writeResponse(salida, 200, "text/plain", respuesta.getBytes(StandardCharsets.UTF_8));
                closeStreams(entrada, salida, cliente);
                continue;
            }

            if (recurso.startsWith("/buscar")) {
                Map<String, String> params = leerQuery(recurso);
                String query = params.getOrDefault("q", "").toLowerCase();
                boolean soloMatriculados = params.getOrDefault("matriculado", "false").equals("true");

                StringBuilder sb = new StringBuilder();
                for (Student s : estudiantes) {
                    if ((s.nombre.toLowerCase().contains(query) || s.apellido.toLowerCase().contains(query)) &&
                            (!soloMatriculados || s.matriculado)) {
                        sb.append(s.toString()).append("<br>");
                    }
                }

                writeResponse(salida, 200, "text/html", sb.toString().getBytes(StandardCharsets.UTF_8));
                closeStreams(entrada, salida, cliente);
                continue;
            }

            InputStream inputStream = HttpServer.class.getResourceAsStream(recurso);
            if (inputStream != null) {
                String mime = guessMime(recurso);
                String header = "HTTP/1.1 200 OK\r\nContent-Type: " + mime + "\r\n\r\n";
                salida.write(header.getBytes());
                inputStream.transferTo(salida);
                inputStream.close();
            } else {
                String respuesta = "HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n<h1>404 - Archivo no encontrado</h1>";
                salida.write(respuesta.getBytes());
            }

            closeStreams(entrada, salida, cliente);
        }
    }

    private static void closeStreams(BufferedReader entrada, OutputStream salida, Socket cliente) throws IOException {
        salida.close();
        entrada.close();
        cliente.close();
    }

    private static Map<String, String> leerQuery(String recurso) {
        Map<String, String> map = new HashMap<>();
        int q = recurso.indexOf('?');
        if (q < 0) return map;
        String query = recurso.substring(q + 1);
        for (String param : query.split("&")) {
            String[] kv = param.split("=");
            if (kv.length == 2) {
                map.put(kv[0], URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return map;
    }

    private static String guessMime(String path) {
        path = path.toLowerCase();
        if (path.endsWith(".html")) return "text/html; charset=utf-8";
        if (path.endsWith(".css")) return "text/css; charset=utf-8";
        if (path.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (path.endsWith(".png")) return "image/png";
        return "application/octet-stream";
    }

    private static void writeResponse(OutputStream out, int status, String contentType, byte[] body) throws IOException {
        String header = "HTTP/1.1 " + status + " OK\r\nContent-Type: " + contentType + "\r\n\r\n";
        out.write(header.getBytes());
        out.write(body);
    }
}
