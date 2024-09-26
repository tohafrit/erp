package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.Plant;

public interface PlantRepository extends JpaRepository<Plant, Long> {}