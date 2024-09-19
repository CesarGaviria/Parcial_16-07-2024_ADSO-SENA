import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Asistente {
    String nombre;
    String cedula;
    Set<Sesion> sesionesAsistidas;

    public Asistente(String nombre, String cedula) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.sesionesAsistidas = Collections.synchronizedSet(new HashSet<>());
    }

}