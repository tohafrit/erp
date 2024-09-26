package eco.repository;

import eco.entity.EcoSapsanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoSapsanProductRepository extends JpaRepository<EcoSapsanProduct, Long> {

    EcoSapsanProduct findByPrefix(String prefix);
}
