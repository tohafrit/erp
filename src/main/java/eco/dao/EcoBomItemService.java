package eco.dao;

import eco.entity.EcoBomItem;
import eco.repository.EcoBomItemRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class EcoBomItemService {

    private final EcoBomItemRepository ecoBomItemRepository;

    public EcoBomItemService(EcoBomItemRepository ecoBomItemRepository) {
        this.ecoBomItemRepository = ecoBomItemRepository;
    }

    public EcoBomItem read(long id) {
        return ecoBomItemRepository.findFirstById(id);
    }

    public List<EcoBomItem> getAllDistinctByBomIdAndKd(Long bomId, boolean kd) {
        return bomId != null ? ecoBomItemRepository.findDistinctByBomIdAndEcoBomItemComponentList_Kd(bomId, kd) : Collections.emptyList();
    }

    public List<EcoBomItem> getAll() {
        return ecoBomItemRepository.findAll();
    }
}