package eco.dao;

import eco.entity.EcoUnit;
import eco.repository.EcoUnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoUnitService {

    private final EcoUnitRepository unitRepository;

    public EcoUnitService(EcoUnitRepository userInfoRepository) {
        this.unitRepository = userInfoRepository;
    }

    public EcoUnit read(long id) {
        return unitRepository.getOne(id);
    }

    public List<EcoUnit> getAll() {
        return unitRepository.findAll();
    }
}