package packetworld.pojo;

/**
 * Holder en memoria para la asignación conductor↔vehículo.
 * Guarda la última DriverAssignment conocida y expone el driverId convenientemente.
 * Colocado en packetworld.pojo porque indicaste que ahí es donde la clase ya existe.
 */
public final class DriverAssignmentSession {
    private static DriverAssignment currentAssignment = null;

    private DriverAssignmentSession() {}

    public static DriverAssignment getCurrentAssignment() {
        return currentAssignment;
    }

    public static void setCurrentAssignment(DriverAssignment assignment) {
        currentAssignment = assignment;
    }

    public static void clear() {
        currentAssignment = null;
    }

    /**
     * Devuelve el id del conductor (conductorId / driverId) de la asignación actual,
     * o null si no hay asignación.
     */
    public static Integer getCurrentDriverId() {
        return currentAssignment == null ? null : currentAssignment.getDriverId();
    }
}