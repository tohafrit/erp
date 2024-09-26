package asu.repository;

import asu.entity.AsuModuleMove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuModuleMoveRepository extends JpaRepository<AsuModuleMove, Long> {


}
