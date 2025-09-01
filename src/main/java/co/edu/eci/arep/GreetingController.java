package co.edu.eci.arep;

import co.edu.eci.arep.Annotations.RestController;
import co.edu.eci.arep.Annotations.GetMapping;
import co.edu.eci.arep.Annotations.RequestParam;

@RestController
public class GreetingController {

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }
}
