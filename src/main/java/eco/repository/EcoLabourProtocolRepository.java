package eco.repository;

import eco.entity.EcoLabourProtocol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoLabourProtocolRepository extends JpaRepository<EcoLabourProtocol, Long> {

    List<EcoLabourProtocol> findAllByCompanyId(Long companyId);
}