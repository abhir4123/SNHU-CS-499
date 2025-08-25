import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Calendar;

public class AppointmentServiceTest {
    private AppointmentService service;
    private Appointment appointment;

    @BeforeEach
    public void setup() {
        service = new AppointmentService();
        Calendar futureDate = Calendar.getInstance();
        futureDate.add(Calendar.DATE, 1);
        appointment = new Appointment("123", futureDate.getTime(), "Valid description");
        service.addAppointment(appointment);
    }

    @Test
    public void testAddAppointmentSuccess() {
        Calendar futureDate = Calendar.getInstance();
        futureDate.add(Calendar.DATE, 2);
        Appointment newAppointment = new Appointment("456", futureDate.getTime(), "Another valid description.");
        service.addAppointment(newAppointment);
        assertEquals("Another valid description.", service.getAppointment("456").getDescription());
    }

    @Test
    public void testDeleteAppointment() {
        service.deleteAppointment("123");
        assertNull(service.getAppointment("123"));
    }
}
