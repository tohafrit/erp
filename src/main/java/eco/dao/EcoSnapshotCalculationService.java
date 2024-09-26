package eco.dao;

import eco.entity.EcoSnapshotCalculation;
import eco.repository.EcoSnapshotCalculationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoSnapshotCalculationService {

    private final EcoSnapshotCalculationRepository ecoSnapshotCalculationRepository;

    public EcoSnapshotCalculationService(EcoSnapshotCalculationRepository ecoSnapshotCalculationRepository) {
        this.ecoSnapshotCalculationRepository = ecoSnapshotCalculationRepository;
    }

    public List<EcoSnapshotCalculation> getAll() {
        return ecoSnapshotCalculationRepository.findAll();
    }

    public List<EcoSnapshotCalculation> getAllById(List<Long> idList) {
        return ecoSnapshotCalculationRepository.findAllById(idList);
    }

    public EcoSnapshotCalculation save(EcoSnapshotCalculation object) {
        return ecoSnapshotCalculationRepository.save(object);
    }

    public List<EcoSnapshotCalculation> saveAll(List<EcoSnapshotCalculation> objectList) {
        return ecoSnapshotCalculationRepository.saveAll(objectList);
    }

    public EcoSnapshotCalculation read(long id) {
        return ecoSnapshotCalculationRepository.getOne(id);
    }

    public void delete(EcoSnapshotCalculation object) {
        ecoSnapshotCalculationRepository.delete(object);
    }

    public void deleteById(long id) {
        ecoSnapshotCalculationRepository.deleteById(id);
    }
}