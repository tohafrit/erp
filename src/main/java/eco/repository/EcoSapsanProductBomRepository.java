package eco.repository;

import eco.entity.EcoSapsanProductBom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoSapsanProductBomRepository extends JpaRepository<EcoSapsanProductBom, Long> {
}