package eco.dao;

import eco.entity.EcoBomItemComponent;
import eco.repository.EcoBomItemComponentRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class EcoBomItemComponentService {

    private final EcoBomItemComponentRepository ecoBomItemComponentRepository;

    public EcoBomItemComponentService(EcoBomItemComponentRepository ecoBomItemComponentRepository) {
        this.ecoBomItemComponentRepository = ecoBomItemComponentRepository;
    }

    public EcoBomItemComponent read(long id) {
        return ecoBomItemComponentRepository.findFirstById(id);
    }

    public List<EcoBomItemComponent> getByBomIdAndKd(Long bomId, boolean kd) {
        return bomId != null ? ecoBomItemComponentRepository.findByBomItem_BomIdAndKd(bomId, kd) : Collections.emptyList();
    }

    public List<EcoBomItemComponent> getAll() {
        return ecoBomItemComponentRepository.findAll();
    }
}