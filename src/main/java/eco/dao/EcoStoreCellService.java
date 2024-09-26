package eco.dao;

import eco.entity.EcoStoreCell;
import eco.repository.EcoStoreCellRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoStoreCellService {

    private final EcoStoreCellRepository ecoStoreCellRepository;

    public EcoStoreCellService(EcoStoreCellRepository ecoStoreCellRepository) {
        this.ecoStoreCellRepository = ecoStoreCellRepository;
    }

    public EcoStoreCell read(long id) {
        return ecoStoreCellRepository.getOne(id);
    }

    public List<EcoStoreCell> getAll() {
        return ecoStoreCellRepository.findAll();
    }
}
