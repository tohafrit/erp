package asu.repository;

import asu.entity.AsuProdModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuProdModuleRepository extends JpaRepository<AsuProdModule, Long> {

    AsuProdModule findTopByNumAndModule_Code1AndModule_Code2(int num, String code1, String code2);

    AsuProdModule findTopByModule_Code1AndModule_Code2(String code1, String code2);
}