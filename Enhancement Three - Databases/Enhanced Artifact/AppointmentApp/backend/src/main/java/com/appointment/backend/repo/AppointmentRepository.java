package com.appointment.backend.repo;

import com.appointment.backend.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data repository for Appointment documents.
 * Method names derive queries automatically via naming conventions.
 */
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    /** All appointments ordered by date ascending. */
    List<Appointment> findAllByOrderByAppointmentDateAsc();

    /** Upcoming: date ≥ given date, ordered ascending. */
    List<Appointment> findAllByAppointmentDateGreaterThanEqualOrderByAppointmentDateAsc(LocalDate date);

    /** Previous: date < given date, ordered descending (newest first). */
    List<Appointment> findAllByAppointmentDateLessThanOrderByAppointmentDateDesc(LocalDate date);

    /**
     * Exclusive range: ordered ascending.
     */
    List<Appointment> findAllByAppointmentDateBetweenOrderByAppointmentDateAsc(LocalDate start, LocalDate end);
}
