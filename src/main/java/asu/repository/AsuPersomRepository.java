package asu.repository;

import asu.entity.AsuPersona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuPersomRepository extends JpaRepository<AsuPersona, Long>  {

}
