package ufp.esof.project.services;

import org.springframework.stereotype.Service;
import ufp.esof.project.models.Availability;

import java.util.Optional;

@Service
public interface AvailabilityServiceI {
    Iterable<Availability> findAll();

    Optional<Availability> findById(Long id);

    boolean deleteById(Long id);

}
