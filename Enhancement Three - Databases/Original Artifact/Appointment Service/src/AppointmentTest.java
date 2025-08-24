import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Calendar;

public class AppointmentTest {

    @Test
    public void testValidAppointmentCreation() {
        Calendar futureDate = Calendar.getInstance();
        futureDate.add(Calendar.DATE, 1);
        Appointment appointment = new Appointment("123", futureDate.getTime(), "This is a valid description.");
        assertEquals("123", appointment.getAppointmentId());
        assertEquals(futureDate.getTime(), appointment.getAppointmentDate());
        assertEquals("This is a valid description.", appointment.getDescription());
    }

    @Test
    public void testInvalidAppointmentDate() {
        Calendar pastDate = Calendar.getInstance();
        pastDate.add(Calendar.DATE, -1);
        assertThrows(IllegalArgumentException.class, () -> {
            new Appointment("123", pastDate.getTime(), "Valid description");
        });
    }

    @Test
    public void testInvalidDescription() {
        Calendar futureDate = Calendar.getInstance();
        futureDate.add(Calendar.DATE, 1);
        assertThrows(IllegalArgumentException.class, () -> {
            new Appointment("123", futureDate.getTime(), null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Appointment("123", futureDate.getTime(), "This description is way too long to be accepted by the validation process and should throw an exception.");
        });
    }
}
