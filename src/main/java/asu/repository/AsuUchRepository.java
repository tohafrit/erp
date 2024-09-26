package asu.repository;

import asu.entity.AsuUch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuUchRepository extends JpaRepository<AsuUch, Long> {

    AsuUch findTopByCodeOrderByCode(String code);

    boolean existsByCode(String code);
}