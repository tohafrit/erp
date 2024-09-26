package eco.dao;

import eco.entity.EcoAllotment;
import eco.repository.EcoAllotmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoAllotmentService {

    private final EcoAllotmentRepository ecoAllotmentRepository;

    public EcoAllotmentService(EcoAllotmentRepository ecoAllotmentRepository) {
        this.ecoAllotmentRepository = ecoAllotmentRepository;
    }

    public EcoAllotment read(Long id) {
        return ecoAllotmentRepository.getOne(id);
    }

    public List<EcoAllotment> getAll() {
        return ecoAllotmentRepository.findAll();
    }
}