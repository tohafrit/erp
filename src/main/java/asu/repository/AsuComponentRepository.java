package asu.repository;

import asu.entity.AsuComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuComponentRepository extends JpaRepository<AsuComponent, Long> {
}