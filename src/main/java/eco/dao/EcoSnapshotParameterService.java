package eco.dao;

import eco.entity.EcoSnapshotParameter;
import eco.repository.EcoSnapshotParameterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoSnapshotParameterService {

    private final EcoSnapshotParameterRepository ecoSnapshotParameterRepository;

    public EcoSnapshotParameterService(EcoSnapshotParameterRepository ecoSnapshotParameterRepository) {
        this.ecoSnapshotParameterRepository = ecoSnapshotParameterRepository;
    }

    public List<EcoSnapshotParameter> getAll() {
        return ecoSnapshotParameterRepository.findAll();
    }

    public List<EcoSnapshotParameter> getAllById(List<Long> idList) {
        return ecoSnapshotParameterRepository.findAllById(idList);
    }

    public EcoSnapshotParameter save(EcoSnapshotParameter object) {
        return ecoSnapshotParameterRepository.save(object);
    }

    public List<EcoSnapshotParameter> saveAll(List<EcoSnapshotParameter> objectList) {
        return ecoSnapshotParameterRepository.saveAll(objectList);
    }

    public EcoSnapshotParameter read(long id) {
        return ecoSnapshotParameterRepository.getOne(id);
    }

    public void delete(EcoSnapshotParameter object) {
        ecoSnapshotParameterRepository.delete(object);
    }

    public void deleteById(long id) {
        ecoSnapshotParameterRepository.deleteById(id);
    }

    public List<EcoSnapshotParameter> getAllByPurchaseAndType(long purchaseId, long type) {
        return ecoSnapshotParameterRepository.findAllByPurchase_IdAndTypeOrderByIdDesc(purchaseId, type);
    }
}