package eco.dao;

import eco.entity.EcoLotGroup;
import eco.repository.EcoLotGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EcoLotGroupService {

    private final EcoLotGroupRepository ecoLotGroupRepository;

    public EcoLotGroupService(EcoLotGroupRepository ecoLotGroupRepository) {
        this.ecoLotGroupRepository = ecoLotGroupRepository;
    }

    public EcoLotGroup read(Long id) { return ecoLotGroupRepository.getOne(id); }

    public List<EcoLotGroup> getAll() {
        return ecoLotGroupRepository.findAll();
    }

    public List<EcoLotGroup> getAllByProductionShipmentLetter(Long letterId) {
        return ecoLotGroupRepository.findDistinctByLotList_AllotmentList_ProductionShipmentLetterId(letterId)
            .stream().distinct().collect(Collectors.toList());
    }
}