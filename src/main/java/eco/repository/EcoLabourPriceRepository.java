package eco.repository;

import eco.entity.EcoLabourPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoLabourPriceRepository extends JpaRepository<EcoLabourPrice, Long> {

    List<EcoLabourPrice> findAllByProtocolId(Long protocolId);
}