package eco.dao;

import eco.entity.EcoBomSpecItem;
import eco.repository.EcoBomSpecItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoBomSpecItemService {

    private final EcoBomSpecItemRepository ecoBomSpecItemRepository;

    public EcoBomSpecItemService(EcoBomSpecItemRepository ecoBomSpecItemRepository) {
        this.ecoBomSpecItemRepository = ecoBomSpecItemRepository;
    }

    public EcoBomSpecItem read(long id) {
        return ecoBomSpecItemRepository.getOne(id);
    }

    public boolean existsByIdAndProductIdAndVersionId(Long id, Long productId, Long versionId) {
        return ecoBomSpecItemRepository.existsByIdAndProductIdAndBomId(id, productId, versionId);
    }

    public EcoBomSpecItem findFirstByIdAndProductIdAndVersionId(Long id, Long productId, Long versionId) {
        return id != null && productId != null && versionId != null ? ecoBomSpecItemRepository.getFirstByIdAndProductIdAndBomId(id, productId, versionId) : null;
    }

    public boolean existsById(Long id) {
        return ecoBomSpecItemRepository.existsById(id);
    }

    public List<EcoBomSpecItem> getAll() {
        return ecoBomSpecItemRepository.findAll();
    }
}