package eco.repository;

import eco.entity.EcoCompanyConstProtocol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoCompanyConstProtocolRepository extends JpaRepository<EcoCompanyConstProtocol, Long> {

    List<EcoCompanyConstProtocol> findAllByCompanyIdOrderByProtocolDateDesc(Long companyId);
}