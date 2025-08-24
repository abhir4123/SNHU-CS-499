package com.appointment.backend.auth;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository interface for User collection.
 * Provides lookup by email and existence check.
 */
public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
