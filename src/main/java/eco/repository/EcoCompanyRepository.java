package eco.repository;

import eco.entity.EcoCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoCompanyRepository extends JpaRepository<EcoCompany, Long> {
}
