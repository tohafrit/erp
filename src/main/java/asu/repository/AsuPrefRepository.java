package asu.repository;

import asu.entity.AsuPref;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuPrefRepository extends JpaRepository<AsuPref, Long> {
}