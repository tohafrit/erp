package eco.dao;

import eco.entity.EcoIdentifier;
import eco.repository.EcoIdentifierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoIdentifierService {

    private final EcoIdentifierRepository ecoIdentifierRepository;

    public EcoIdentifierService(EcoIdentifierRepository ecoIdentifierRepository) {
        this.ecoIdentifierRepository = ecoIdentifierRepository;
    }

    public EcoIdentifier read(long id) {
        return ecoIdentifierRepository.getOne(id);
    }

    public List<EcoIdentifier> getAll() {
        return ecoIdentifierRepository.findAll();
    }
}
