package com.appointment.backend.controller;

import com.appointment.backend.model.Appointment;
import com.appointment.backend.service.AppointmentService;
import com.appointment.backend.util.CsvUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * REST controller exposing the appointments API.
 * Responsibilities:
 * - Map HTTP requests to service calls
 * - Parse/validate simple query parameters
 * - Shape HTTP responses (status, headers, content type)
 * Validation and error-to-status mapping are handled by the model/service and
 * the global exception handler.
 */
@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    // For this project we instantiate the service directly.
    // In a larger app, prefer @Service + constructor injection.
    private final AppointmentService appointmentService = new AppointmentService();

    /** Return all appointments (unsorted; kept for backward compatibility). */
    @GetMapping
    public Collection<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    /** Return one appointment by ID or null if not found. */
    @GetMapping("/{id}")
    public Appointment getAppointment(@PathVariable String id) {
        return appointmentService.getAppointment(id);
    }

    /**
     * Create a new appointment.
     * The Appointment constructor and service enforce all validation and will throw
     * IllegalArgumentException on failure.
     * The GlobalExceptionHandler converts those to a 4xx response with a friendly
     * JSON error.
     */
    @PostMapping
    public void addAppointment(@RequestBody Appointment appointment) {
        appointmentService.addAppointment(appointment);
    }

    /** Delete by ID. Throws if the ID doesn't exist (handled globally to 4xx). */
    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable String id) {
        appointmentService.deleteAppointment(id);
    }

    /** Today and future, sorted soonest → farthest. */
    @GetMapping("/upcoming")
    public List<Appointment> getUpcomingAppointments() {
        return appointmentService.getUpcomingAppointments();
    }

    /** Strictly before today, sorted newest → oldest. */
    @GetMapping("/previous")
    public List<Appointment> getPreviousAppointments() {
        return appointmentService.getPreviousAppointments();
    }

    /**
     * Inclusive date range [start, end] in yyyy-MM-dd.
     * Parsing failures bubble to the global handler (400 with a helpful message).
     */
    @GetMapping("/range")
    public List<Appointment> getRange(@RequestParam String start, @RequestParam String end) {
        LocalDate s = LocalDate.parse(start);
        LocalDate e = LocalDate.parse(end);
        return appointmentService.getAppointmentsInRange(s, e);
    }

    /**
     * Export data as CSV or JSON.
     * scope:
     * - all: every appointment, date-sorted ascending
     * - upcoming: today and future, ascending
     * - previous: before today, newest first
     * - range: requires start and end (inclusive)
     * format: csv | json
     */
    @GetMapping("/export")
    public ResponseEntity<?> export(
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam(defaultValue = "all") String scope,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {

        // Choose dataset based on scope
        List<Appointment> data;
        switch (scope.toLowerCase()) {
            case "all":
                data = appointmentService.getAllSortedByDate();
                break;
            case "upcoming":
                data = appointmentService.getUpcomingAppointments();
                break;
            case "previous":
                data = appointmentService.getPreviousAppointments();
                break;
            case "range":
                if (start == null || end == null) {
                    throw new IllegalArgumentException("Range export requires start and end query parameters.");
                }
                data = appointmentService.getAppointmentsInRange(LocalDate.parse(start), LocalDate.parse(end));
                break;
            default:
                throw new IllegalArgumentException("Unsupported scope. Use all|upcoming|previous|range.");
        }

        String fileTag = scope.toLowerCase();

        // Shape response in the requested format
        if ("csv".equalsIgnoreCase(format)) {
            String csv = CsvUtil.toCsv(data);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=appointments-" + fileTag + ".csv")
                    .contentType(MediaType.valueOf("text/csv"))
                    .body(csv);
        } else if ("json".equalsIgnoreCase(format)) {
            // Let Spring serialize the list; we still provide a download filename.
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=appointments-" + fileTag + ".json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(data);
        } else {
            throw new IllegalArgumentException("Unsupported export format. Use csv or json.");
        }
    }
}
