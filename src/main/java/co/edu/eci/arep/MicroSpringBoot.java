package co.edu.eci.arep;

import co.edu.eci.arep.Annotations.Component;
import co.edu.eci.arep.Annotations.RestController;
import co.edu.eci.arep.Annotations.GetMapping;
import co.edu.eci.arep.Annotations.RequestParam;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MicroSpringBoot {
    public static void main(String[] args) {
        try {
            exploreAndRegisterComponents();
            HttpServer.runServer(args);

        } catch (Exception ex) {
            Logger.getLogger(MicroSpringBoot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void exploreAndRegisterComponents() throws Exception {
        registerIfComponent("co.edu.eci.arep.HelloController");
        registerIfComponent("co.edu.eci.arep.GreetingController");
    }

    private static void registerIfComponent(String className) {
        try {
            Class<?> clazz = Class.forName(className);

            if (clazz.isAnnotationPresent(Component.class) ||
                    clazz.isAnnotationPresent(RestController.class)) {

                Object instance = clazz.getDeclaredConstructor().newInstance();

                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        GetMapping annotation = method.getAnnotation(GetMapping.class);
                        String path = annotation.value();

                        HttpServer.get(path, (req, resp) -> {
                            try {
                                Object[] params = getMethodParameters(method, req);
                                return (String) method.invoke(instance, params);
                            } catch (Exception e) {
                                return "Error: " + e.getMessage();
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error cargando clase " + className + ": " + e.getMessage());
        }
    }

    private static Object[] getMethodParameters(Method method, HttpRequest request) {
        Parameter[] parameters = method.getParameters();
        Object[] paramValues = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];

            if (param.isAnnotationPresent(RequestParam.class)) {
                RequestParam annotation = param.getAnnotation(RequestParam.class);
                String paramName = annotation.value();
                String defaultValue = annotation.defaultValue();

                String value = request.getValues(paramName);
                if (value == null || value.isEmpty()) {
                    value = defaultValue;
                }

                paramValues[i] = value;
            } else {
                paramValues[i] = "";
            }
        }

        return paramValues;
    }
}