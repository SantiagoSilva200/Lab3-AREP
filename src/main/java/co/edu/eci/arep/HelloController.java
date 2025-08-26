package co.edu.eci.arep;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String index(){
        return "Grettings from Spring Boot!";
    }
}

