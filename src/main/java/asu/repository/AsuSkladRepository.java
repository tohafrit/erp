package asu.repository;

import asu.entity.AsuSklad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuSkladRepository extends JpaRepository<AsuSklad, Long> {
}
