package asu.repository;

import asu.entity.AsuModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuModuleRepository extends JpaRepository<AsuModule, Long> {
}