package asu.repository;

import asu.entity.AsuProdModuleStateOnProdSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuProdModuleStateOnProdSiteRepository extends JpaRepository<AsuProdModuleStateOnProdSite, Long> {

    AsuProdModuleStateOnProdSite findTopByCodeOrderByCode(String code);

    boolean existsByCode(String code);
}