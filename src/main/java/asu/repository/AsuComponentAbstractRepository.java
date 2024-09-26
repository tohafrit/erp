package asu.repository;

import asu.entity.AsuComponentAbstract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuComponentAbstractRepository extends JpaRepository<AsuComponentAbstract, Long> {
}