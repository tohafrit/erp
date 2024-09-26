package eco.repository;

import eco.entity.EcoAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoAccountRepository extends JpaRepository<EcoAccount, Long> {
}
