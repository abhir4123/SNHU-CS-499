import java.util.Date;

public class Appointment {
    private final String appointmentId;
    private final Date appointmentDate;
    private String description;

    public Appointment(String appointmentId, Date appointmentDate, String description) {
        if (appointmentId == null || appointmentId.length() > 10) throw new IllegalArgumentException("Invalid appointment ID");
        if (appointmentDate == null || appointmentDate.before(new Date())) throw new IllegalArgumentException("Invalid appointment date");
        if (description == null || description.length() > 50) throw new IllegalArgumentException("Invalid description");

        this.appointmentId = appointmentId;
        this.appointmentDate = appointmentDate;
        this.description = description;
    }

    public String getAppointmentId() { return appointmentId; }
    public Date getAppointmentDate() { return appointmentDate; }
    public String getDescription() { return description; }

    public void setDescription(String description) {
        if (description == null || description.length() > 50) throw new IllegalArgumentException("Invalid description");
        this.description = description;
    }
}
