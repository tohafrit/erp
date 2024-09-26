package eco.dao;

import eco.entity.EcoProductType;
import eco.repository.EcoProductTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoProductTypeService {

    private final EcoProductTypeRepository ecoProductTypeRepository;

    public EcoProductTypeService(EcoProductTypeRepository ecoProductTypeRepository) {
        this.ecoProductTypeRepository = ecoProductTypeRepository;
    }

    public List<EcoProductType> findAllByIdIn(List<Long> idList) {
        return ecoProductTypeRepository.findAllByIdIn(idList);
    }
}