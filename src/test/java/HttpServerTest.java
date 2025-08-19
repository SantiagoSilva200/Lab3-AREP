
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.*;
import java.net.*;

public class HttpServerTest {

    @Test
    public void testRegistrarEstudiante() throws IOException {
        String registrarUrl = "http://localhost:8080/registrar?nombre=Laura&apellido=Gomez&carrera=Matematicas&matriculado=true";
        String response = sendPostRequest(registrarUrl);
        assertTrue(response.contains("Estudiante registrado: Laura Gomez"));
    }

    @Test
    public void testBuscarEstudiantePorNombre() throws IOException {
        String buscarUrl = "http://localhost:8080/buscar?q=Laura&matriculado=false";
        String response = sendGetRequest(buscarUrl);
        assertTrue(response.contains("Laura Gomez"));
    }

    @Test
    public void testBuscarSoloMatriculados() throws IOException {
        String buscarMatUrl = "http://localhost:8080/buscar?q=&matriculado=true";
        String response = sendGetRequest(buscarMatUrl);
        assertTrue(response.contains("Sí"));
    }

    private static String sendGetRequest(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    private static String sendPostRequest(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}