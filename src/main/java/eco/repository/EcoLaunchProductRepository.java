package eco.repository;

import eco.entity.EcoLaunchProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EcoLaunchProductRepository extends JpaRepository<EcoLaunchProduct, Long> {}