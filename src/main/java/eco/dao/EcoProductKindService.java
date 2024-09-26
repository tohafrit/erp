package eco.dao;

import eco.entity.EcoProductKind;
import eco.repository.EcoProductKindRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoProductKindService {

    private final EcoProductKindRepository ecoProductKindRepository;

    public EcoProductKindService(EcoProductKindRepository ecoProductKindRepository) {
        this.ecoProductKindRepository = ecoProductKindRepository;
    }

    public List<EcoProductKind> getAll() {
        return ecoProductKindRepository.findAll();
    }
}