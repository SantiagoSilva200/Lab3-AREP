package co.edu.eci.arep;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

public class MicroSpringBoot{
    public static void main(String[] args){
        try {
            HttpServer.runServer(args);

        } catch (IOException | URISyntaxException ex){

            Logger.getLogger(MicroSpringBoot.class.getName()).log(Level.SEVERE,null, ex);

        }
    }
}
