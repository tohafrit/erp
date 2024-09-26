package asu.repository;

import asu.entity.AsuContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuContractRepository extends JpaRepository<AsuContract, Long>  {
}