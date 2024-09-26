package asu.repository;

import asu.entity.AsuUlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuUlistRepository extends JpaRepository<AsuUlist, Long> {
}
