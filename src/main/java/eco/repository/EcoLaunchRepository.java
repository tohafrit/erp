package eco.repository;

import eco.entity.EcoLaunch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EcoLaunchRepository extends JpaRepository<EcoLaunch, Long> {

    List<EcoLaunch> findAllByYearLessThanEqualAndIdNotOrderByYearDescNumberInYearDesc(LocalDate year, Long launchId);

    List<EcoLaunch> findAllByBomAttributeList_Bom_Product_IdOrderByYearDescNumberInYearDesc(Long productId);
}