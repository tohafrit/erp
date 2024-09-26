package eco.dao;

import eco.entity.EcoCompanyConstProtocol;
import eco.repository.EcoCompanyConstProtocolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoCompanyConstProtocolService {

    private final EcoCompanyConstProtocolRepository ecoCompanyConstProtocolRepository;

    public EcoCompanyConstProtocolService(EcoCompanyConstProtocolRepository ecoCompanyConstProtocolRepository) {
        this.ecoCompanyConstProtocolRepository = ecoCompanyConstProtocolRepository;
    }

    public EcoCompanyConstProtocol read(long id) {
        return ecoCompanyConstProtocolRepository.getOne(id);
    }

    public List<EcoCompanyConstProtocol> getAllByCompanyId(long companyId) {
        return ecoCompanyConstProtocolRepository.findAllByCompanyIdOrderByProtocolDateDesc(companyId);
    }

    public List<EcoCompanyConstProtocol> getAll() {
        return ecoCompanyConstProtocolRepository.findAll();
    }
}