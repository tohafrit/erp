package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.Justification;

public interface JustificationRepository extends JpaRepository<Justification, Long> {}