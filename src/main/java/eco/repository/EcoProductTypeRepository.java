package eco.repository;

import eco.entity.EcoProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoProductTypeRepository extends JpaRepository<EcoProductType, Long> {

    List<EcoProductType> findAllByIdIn(List<Long> idList);
}