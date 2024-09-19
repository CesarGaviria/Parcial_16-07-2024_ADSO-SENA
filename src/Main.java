import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "Bienvenido \n" +
                "La certificacion solo se le otorga a quien tenga asistencia en las 3 sesiones");
        Conferencia conferencia = new Conferencia();
        conferencia.listaSesiones.put(1, new Sesion(1, "Introducción a Java", "charla"));
        conferencia.listaSesiones.put(2, new Sesion(2, "Programación concurrente", "taller"));
        conferencia.listaSesiones.put(3, new Sesion(3, "Debate sobre IA", "panel"));
        conferencia.cargarInscripciones();
        conferencia.cargarAsistencias();
        while (true) {
            String[] opciones = {"Inscribir Asistente", "Registrar Asistencia", "Generar Certificado", "Listar Sesiones", "Guardar Inscripciones", "Listar Inscripciones", "Salir"};
            int seleccion = JOptionPane.showOptionDialog(null, "Seleccione una opción:", "Sistema de Conferencia", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);
            String cedula;
            switch (seleccion) {
                case 0:
                    String nombre = JOptionPane.showInputDialog("Ingrese el nombre del asistente:");
                    cedula = JOptionPane.showInputDialog("Ingrese la cédula del asistente:");
                    conferencia.inscribirAsistente(nombre, cedula);
                    break;
                case 1:
                    cedula = JOptionPane.showInputDialog("Ingrese la cédula del asistente:");
                    String idSesionStr = JOptionPane.showInputDialog("Ingrese el ID de la sesión:");
                    try {
                        int idSesion = Integer.parseInt(idSesionStr);
                        conferencia.registrarAsistencia(cedula, idSesion);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "ID de sesión inválido");
                    }
                    conferencia.generarCertificado(cedula, "automatica");
                    break;
                case 2:
                    cedula = JOptionPane.showInputDialog("Ingrese la cédula del asistente:");
                    conferencia.generarCertificado(cedula, "manual");
                    break;
                case 3:
                    StringBuilder sesiones = new StringBuilder("Sesiones disponibles:\n");
                    for (Sesion sesion : conferencia.listaSesiones.values()) {
                        sesiones.append(sesion.id).append(": ").append(sesion.nombre).append(" (").append(sesion.tipoSesion).append(")\n");
                    }
                    JOptionPane.showMessageDialog(null, sesiones.toString());
                    break;
                case 4:
                    JOptionPane.showMessageDialog(null, "Las inscripciones se guardan automáticamente.");
                    break;
                case 5:
                    conferencia.listarInscripciones();
                    break;
                case 6:
                    conferencia.cerrar();
                    JOptionPane.showMessageDialog(null, "Gracias por usar el Sistema de Conferencia");
                    System.exit(0);
                default:
                    break;
            }
        }
    }
}