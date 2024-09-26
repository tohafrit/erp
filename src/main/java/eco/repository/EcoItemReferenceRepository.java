package eco.repository;

import eco.entity.EcoItemReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoItemReferenceRepository extends JpaRepository<EcoItemReference, Long> {

    List<EcoItemReference> findByItemGroupReference_ContractSectionReference_ProductShipmentLetter_Id(Long letterId);
}
