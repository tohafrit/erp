package asu.repository;

import asu.entity.AsuComponentName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuComponentNameRepository extends JpaRepository<AsuComponentName, Long> {
}