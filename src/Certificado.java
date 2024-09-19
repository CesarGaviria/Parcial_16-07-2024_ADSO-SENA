import java.util.HashSet;
import java.util.Set;

public class Certificado {
    int id;
    Asistente asistente;
    Set<Sesion> sesionesCompletadas;

    public Certificado(int id, Asistente asistente, Set<Sesion> sesionesCompletadas) {
        this.id = id;
        this.asistente = asistente;
        this.sesionesCompletadas = new HashSet<>(sesionesCompletadas);
    }
}