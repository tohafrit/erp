package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.BomSpecItemService;
import ru.korundm.entity.BomSpecItem;
import ru.korundm.repository.BomSpecItemRepository;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class BomSpecItemServiceImpl implements BomSpecItemService {

    private final BomSpecItemRepository bomSpecItemRepository;

    public BomSpecItemServiceImpl(BomSpecItemRepository bomSpecItemRepository) {
        this.bomSpecItemRepository = bomSpecItemRepository;
    }

    @Override
    public List<BomSpecItem> getAll() {
        return bomSpecItemRepository.findAll();
    }

    @Override
    public List<BomSpecItem> getAllById(List<Long> idList) {
        return bomSpecItemRepository.findAllById(idList);
    }

    @Override
    public BomSpecItem save(BomSpecItem object) {
        return bomSpecItemRepository.save(object);
    }

    @Override
    public List<BomSpecItem> saveAll(List<BomSpecItem> objectList) {
        return bomSpecItemRepository.saveAll(objectList);
    }

    @Override
    public BomSpecItem read(long id) {
        return bomSpecItemRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(BomSpecItem object) {
        bomSpecItemRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        bomSpecItemRepository.deleteById(id);
    }

    @Override
    public List<BomSpecItem> getAllByBomId(Long bomId) {
        return bomId == null ? Collections.emptyList() : bomSpecItemRepository.findAllByBomId(bomId);
    }

    @Override
    public boolean existsByBomIdAndProductId(Long bomId, Long productId) {
        return bomSpecItemRepository.existsByBomIdAndProductId(bomId, productId);
    }
}