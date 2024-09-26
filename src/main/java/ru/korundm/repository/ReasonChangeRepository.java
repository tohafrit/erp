package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.ReasonChange;

public interface ReasonChangeRepository extends JpaRepository<ReasonChange, Long> {}