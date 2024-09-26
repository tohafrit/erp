package asu.repository;

import asu.entity.AsuPlant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuPlantRepository extends JpaRepository<AsuPlant, Long> {
}