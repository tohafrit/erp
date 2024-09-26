package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.ComponentAppointment;

public interface ComponentAppointmentRepository extends JpaRepository<ComponentAppointment, Long> {}