package co.edu.eci.arep;

public class Student {
    String nombre;
    String apellido;
    String carrera;
    boolean matriculado;

    Student(String n, String a, String c, boolean m) {
        nombre = n;
        apellido = a;
        carrera = c;
        matriculado = m;
    }

    @Override
    public String toString() {
        return nombre + " " + apellido + " | " + carrera + " | " + (matriculado ? "Sí" : "No");
    }
}

