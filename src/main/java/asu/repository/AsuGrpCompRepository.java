package asu.repository;

import asu.entity.AsuGrpComp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuGrpCompRepository extends JpaRepository<AsuGrpComp, Long> {

    AsuGrpComp findFirstByNomGrp(Long nomGrp);
}