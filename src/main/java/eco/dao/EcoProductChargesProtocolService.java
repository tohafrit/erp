package eco.dao;

import eco.entity.EcoProductChargesProtocol;
import eco.repository.EcoProductChargesProtocolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoProductChargesProtocolService {

    private final EcoProductChargesProtocolRepository productChargesProtocolRepository;

    public EcoProductChargesProtocolService(EcoProductChargesProtocolRepository productChargesProtocolRepository) {
        this.productChargesProtocolRepository = productChargesProtocolRepository;
    }

    public EcoProductChargesProtocol read(Long id) {
        return productChargesProtocolRepository.getOne(id);
    }

    public List<EcoProductChargesProtocol> getAll() {
        return productChargesProtocolRepository.findAll();
    }
}