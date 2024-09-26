package eco.dao;

import eco.entity.EcoLabourProtocol;
import eco.repository.EcoLabourProtocolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoLabourProtocolService {

    private final EcoLabourProtocolRepository ecoLabourProtocolRepository;

    public EcoLabourProtocolService(EcoLabourProtocolRepository ecoLabourProtocolRepository) {
        this.ecoLabourProtocolRepository = ecoLabourProtocolRepository;
    }

    public EcoLabourProtocol read(long id) {
        return ecoLabourProtocolRepository.getOne(id);
    }

    public List<EcoLabourProtocol> getAllByCompanyId(Long companyId) {
        return ecoLabourProtocolRepository.findAllByCompanyId(companyId);
    }

    public List<EcoLabourProtocol> getAll() {
        return ecoLabourProtocolRepository.findAll();
    }
}