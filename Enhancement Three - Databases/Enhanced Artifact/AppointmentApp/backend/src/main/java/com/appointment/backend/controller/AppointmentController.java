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
import java.util.Map;

/**
 * REST API for appointment resources.
 * - All GET endpoints are public.
 * - POST/DELETE are protected by JWT (enforced in SecurityConfig).
 */
@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /** Returns all appointments (unsorted at the service level's discretion). */
    @GetMapping
    public Collection<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    /** Returns a single appointment by its ID or throws if not found. */
    @GetMapping("/{id}")
    public Appointment getAppointment(@PathVariable String id) {
        return appointmentService.getAppointment(id);
    }

    /**
     * Creates a new appointment.
     * Returns a simple JSON status payload on success.
     * Validation and duplicate checks are handled in the domain/service layer.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> addAppointment(@RequestBody Appointment appointment) {
        appointmentService.addAppointment(appointment);
        return ResponseEntity.ok(Map.of(
                "status", "created",
                "appointmentId", appointment.getAppointmentId()));
    }

    /**
     * Deletes an appointment by ID.
     * Returns a simple JSON status payload on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAppointment(@PathVariable String id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok(Map.of(
                "status", "deleted",
                "appointmentId", id));
    }

    /** Upcoming appointments: date >= today (ordering handled by the service). */
    @GetMapping("/upcoming")
    public List<Appointment> getUpcomingAppointments() {
        return appointmentService.getUpcomingAppointments();
    }

    /** Previous appointments: date < today (ordering handled by the service). */
    @GetMapping("/previous")
    public List<Appointment> getPreviousAppointments() {
        return appointmentService.getPreviousAppointments();
    }

    /**
     * Range query (exclusive): [start, end] but excluding the bounds.
     * Input is validated here; parsing errors are handled by the global exception
     * handler.
     */
    @GetMapping("/range")
    public List<Appointment> getRange(@RequestParam String start, @RequestParam String end) {
        if (start == null || start.isBlank()) {
            throw new IllegalArgumentException("Start date is required (yyyy-MM-dd).");
        }
        if (end == null || end.isBlank()) {
            throw new IllegalArgumentException("End date is required (yyyy-MM-dd).");
        }

        // Let DateTimeParseException be handled by the global handler -> 400 with a
        // helpful message
        LocalDate s = LocalDate.parse(start);
        LocalDate e = LocalDate.parse(end);

        // Exclusive range; service will also validate ordering
        return appointmentService.getAppointmentsInRange(s, e);
    }

    /**
     * Download appointments as CSV or JSON.
     * - scope: all | upcoming | previous | range (range requires start & end)
     * - format: csv | json
     * Responds with Content-Disposition header to trigger file download.
     */
    @GetMapping("/export")
    public ResponseEntity<?> export(
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam(defaultValue = "all") String scope,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {

        // Choose dataset by scope; the service enforces business rules
        List<Appointment> data;
        switch (scope.toLowerCase()) {
            case "all" -> data = appointmentService.getAllSortedByDate();
            case "upcoming" -> data = appointmentService.getUpcomingAppointments();
            case "previous" -> data = appointmentService.getPreviousAppointments();
            case "range" -> {
                if (start == null || end == null) {
                    throw new IllegalArgumentException("Range export requires start and end query parameters.");
                }
                data = appointmentService.getAppointmentsInRange(LocalDate.parse(start), LocalDate.parse(end));
            }
            default -> throw new IllegalArgumentException("Unsupported scope. Use all|upcoming|previous|range.");
        }

        // Return requested format with a friendly filename
        String fileTag = scope.toLowerCase();
        if ("csv".equalsIgnoreCase(format)) {
            String csv = CsvUtil.toCsv(data);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=appointments-" + fileTag + ".csv")
                    .contentType(MediaType.valueOf("text/csv"))
                    .body(csv);
        } else if ("json".equalsIgnoreCase(format)) {
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=appointments-" + fileTag + ".json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(data);
        } else {
            throw new IllegalArgumentException("Unsupported export format. Use csv or json.");
        }
    }
}
