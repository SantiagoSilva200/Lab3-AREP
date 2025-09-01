import co.edu.eci.arep.HttpRequest;
import co.edu.eci.arep.HttpResponse;
import co.edu.eci.arep.HttpServer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HttpServerTest {

    @Test
    public void testHelloEndpoint() {
        HttpRequest request = new HttpRequest("/hello", null);
        HttpResponse response = new HttpResponse();

        HttpServer.get("/hello", (req, resp) -> "Greetings from Spring Boot!");

        assertTrue(HttpServer.routes.containsKey("/hello"));

        String result = HttpServer.routes.get("/hello").apply(request, response);
        assertEquals("Greetings from Spring Boot!", result);
    }

    @Test
    public void testGreetingEndpointWithoutParams() {
        HttpRequest request = new HttpRequest("/greeting", null);
        HttpResponse response = new HttpResponse();

        HttpServer.get("/greeting", (req, resp) -> {
            String name = req.getValues("name");
            if (name == null || name.isEmpty()) {
                name = "World";
            }
            return "Hola " + name;
        });

        assertTrue(HttpServer.routes.containsKey("/greeting"));

        String result = HttpServer.routes.get("/greeting").apply(request, response);
        assertEquals("Hola World", result);
    }

    @Test
    public void testGreetingEndpointWithParams() {
        HttpRequest request = new HttpRequest("/greeting", "name=Santiago");
        HttpResponse response = new HttpResponse();
        HttpServer.get("/greeting", (req, resp) -> {
            String name = req.getValues("name");
            if (name == null || name.isEmpty()) {
                name = "World";
            }
            return "Hola " + name;
        });
        String result = HttpServer.routes.get("/greeting").apply(request, response);
        assertEquals("Hola Santiago", result);
    }

    @Test
    public void testGreetingEndpointEmptyParam() {
        HttpRequest request = new HttpRequest("/greeting", "name=");
        HttpResponse response = new HttpResponse();
        HttpServer.get("/greeting", (req, resp) -> {
            String name = req.getValues("name");
            if (name == null || name.isEmpty()) {
                name = "World";
            }
            return "Hola " + name;
        });

        String result = HttpServer.routes.get("/greeting").apply(request, response);
        assertEquals("Hola World", result);
    }

    @Test
    public void testMultipleEndpoints() {
        HttpServer.get("/hello", (req, resp) -> "Hello");
        HttpServer.get("/greeting", (req, resp) -> "Greeting");
        HttpServer.get("/test", (req, resp) -> "Test");

        assertTrue(HttpServer.routes.containsKey("/hello"));
        assertTrue(HttpServer.routes.containsKey("/greeting"));
        assertTrue(HttpServer.routes.containsKey("/test"));
        assertEquals(3, HttpServer.routes.size());
    }
}