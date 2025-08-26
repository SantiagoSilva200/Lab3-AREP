import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import co.edu.eci.arep.HttpServer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpServerTest {

    private static Thread serverThread;
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void startServer() throws Exception {
        serverThread = new Thread(() -> {
            try {
                String[] args = {};
                HttpServer.main(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
        Thread.sleep(3000);
    }

    @AfterAll
    public static void stopServer() {
    }

    @Test
    public void testHello() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/App/hello"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("hello world!", response.body().trim());
        System.out.println("/App/hello: " + response.body().trim());
    }

    @Test
    public void testHelloWParameters() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/App/hello?name=Pedro"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Hello Pedro", response.body().trim());
        System.out.println("/App/hello?name=Pedro: " + response.body().trim());
    }

    @Test
    public void testPi() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/App/pi"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(String.valueOf(Math.PI), response.body().trim());
        System.out.println("/App/pi: " + response.body().trim());
    }
}