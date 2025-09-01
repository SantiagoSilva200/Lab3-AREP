package co.edu.eci.arep;

import co.edu.eci.arep.Annotations.GetMapping;
import co.edu.eci.arep.Annotations.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String index(){
        return "Grettings from Spring Boot!";
    }
}

