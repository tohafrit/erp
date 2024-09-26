package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.Labour;

public interface LabourRepository extends JpaRepository<Labour, Long> {}