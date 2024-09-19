import javax.print.attribute.standard.JobPriority;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.*;

public class Conferencia {
    Map<String, Asistente> listaAsistentes;
    Map<Integer, Sesion> listaSesiones;
    List<Certificado> listaCertificados;
    ExecutorService executor;

    public Conferencia() {
        this.listaAsistentes = new ConcurrentHashMap<>();
        this.listaSesiones = new ConcurrentHashMap<>();
        this.listaCertificados = new CopyOnWriteArrayList<>();
        this.executor = Executors.newFixedThreadPool(10);
    }

    public void inscribirAsistente(String nombre, String cedula) {
        executor.submit(() -> {
            try {
                if (listaAsistentes.containsKey(cedula)) {
                    throw new IllegalArgumentException("Asistente ya registrado");
                }
                Asistente asistente = new Asistente(nombre, cedula);
                listaAsistentes.put(cedula, asistente);
                System.out.println("Asistente inscrito o cargado: " + nombre);
                guardarInscripcion(asistente);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, "Error al inscribir: " + e.getMessage());
            }
        });
    }

    public void cargarAsistencias() {
        try (BufferedReader reader = new BufferedReader(new FileReader("asistencias.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String cedula = parts[0];
                    int idSesion = Integer.parseInt(parts[1]);
                    boolean asistencia = Boolean.parseBoolean(parts[2]);
                    Asistente asistente = listaAsistentes.get(cedula);
                    Sesion sesion = listaSesiones.get(idSesion);
                    if (asistente != null && sesion != null) {
                        if (asistencia) {
                            asistente.sesionesAsistidas.add(sesion);
                            sesion.listaAsistentes.add(asistente);
                        }
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar las asistencias: " + e.getMessage());
        }
    }

    public void registrarAsistencia(String cedula, int idSesion) {
       //Usar hilos en este metodo me daba error, no se registraba :c
        try {
            Asistente asistente = listaAsistentes.get(cedula);
            Sesion sesion = listaSesiones.get(idSesion);
            if (asistente == null) {
                throw new IllegalArgumentException("Asistente no registrado");
            }
            if (sesion == null) {
                throw new IllegalArgumentException("Sesión no existe");
            }
            boolean asistenciaRegistrada = asistente.sesionesAsistidas.add(sesion);
            if (asistenciaRegistrada) {
                sesion.listaAsistentes.add(asistente);
                JOptionPane.showMessageDialog(null, "Asistencia registrada: " + asistente.nombre + " - " + sesion.nombre);
                guardarAsistencia(cedula, idSesion, asistenciaRegistrada);
            } else {
                JOptionPane.showMessageDialog(null, "Algo a ido mal, " + asistente.nombre
                        + "\nYa tiene asistencia en " + sesion.nombre + "\nSesión: " + sesion.id);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null,"Error al registrar asistencia: " + e.getMessage());
        }
    }

    public void generarCertificado(String cedula, String tipoVerificiacion) {
        executor.submit(() -> {
            Asistente asistente = listaAsistentes.get(cedula);
            if (asistente == null) {
                JOptionPane.showMessageDialog(null, "Asistente no registrado");
                return;
            }
            boolean asistioASesion1 = asistente.sesionesAsistidas.stream().anyMatch(sesion -> sesion.id == 1);
            boolean asistioASesion2 = asistente.sesionesAsistidas.stream().anyMatch(sesion -> sesion.id == 2);
            boolean asistioASesion3 = asistente.sesionesAsistidas.stream().anyMatch(sesion -> sesion.id == 3);
            if (asistioASesion1 && asistioASesion2 && asistioASesion3) {
                Certificado certificado = new Certificado(listaCertificados.size() + 1, asistente, new HashSet<>(asistente.sesionesAsistidas));
                listaCertificados.add(certificado);
                JOptionPane.showMessageDialog(null, "Certificado generado para: " + asistente.nombre);
            } else {
                if (tipoVerificiacion.equals("manual")) {
                    JOptionPane.showMessageDialog(null, "Certificado no generado para: " + cedula
                            + " " + asistente.nombre + "\nEl asistente no ha asistido a todas las sesiones requeridas (1, 2 y 3)");
                }
            }
        });
    }

    public void guardarInscripcion(Asistente asistente) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("inscripciones.txt", true))) {
            writer.println(asistente.cedula + "," + asistente.nombre);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar inscripción " + e.getMessage());
        }
    }

    public void guardarAsistencia(String cedula, int idSesion, boolean asistenciaRegistrada) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("asistencias.txt", true))) {
            writer.println(cedula + "," + idSesion + "," + asistenciaRegistrada);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar asistencia " + e.getMessage());
        }
    }

    public void cargarInscripciones() {
        try (BufferedReader reader = new BufferedReader(new FileReader("inscripciones.txt"))) {
            String line;
            int c = 0;
            while ((line = reader.readLine()) != null) {
                if (c > 0) {
                    String[] parts = line.split(",");
                    inscribirAsistente(parts[1], parts[0]);
                }
                c++;
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter("inscripciones.txt", false))) {
                writer.println("1,Admin");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al cargar inscripción " + e.getMessage());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar inscripción " + e.getMessage());
        }
    }

    public void listarInscripciones() {
        try (BufferedReader reader = new BufferedReader(new FileReader("inscripciones.txt"))) {
            String line;
            StringBuilder lista = new StringBuilder();
            int c = 0;
            while ((line = reader.readLine()) != null) {
                if (c > 0) {
                    lista.append(line).append("\n");
                }
                c++;
            }
            JOptionPane.showMessageDialog(null, lista.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al listar inscripción " + e.getMessage());
        }
    }
    public void cerrar() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}