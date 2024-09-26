package asu.repository;

import asu.entity.AsuOkei;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuOkeiRepository extends JpaRepository<AsuOkei, Long> {
}