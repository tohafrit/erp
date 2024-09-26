package asu.repository;

import asu.entity.AsuCompOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuCompOutRepository extends JpaRepository<AsuCompOut, Long> {
}