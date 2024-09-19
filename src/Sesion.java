import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Sesion {
    int id;
    String nombre;
    String tipoSesion;
    Set<Asistente> listaAsistentes;

    public Sesion(int id, String nombre, String tipoSesion) {
        this.id = id;
        this.nombre = nombre;
        this.tipoSesion = tipoSesion;
        this.listaAsistentes = Collections.synchronizedSet(new HashSet<>());
    }
}