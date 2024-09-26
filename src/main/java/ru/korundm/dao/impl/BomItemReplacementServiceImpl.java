package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.BomItemReplacementService;
import ru.korundm.entity.BomItemReplacement;
import ru.korundm.repository.BomItemReplacementRepository;

import java.util.List;

@Service
@Transactional
public class BomItemReplacementServiceImpl implements BomItemReplacementService {

    private final BomItemReplacementRepository bomItemReplacementRepository;

    public BomItemReplacementServiceImpl(BomItemReplacementRepository bomItemReplacementRepository) {
        this.bomItemReplacementRepository = bomItemReplacementRepository;
    }

    @Override
    public List<BomItemReplacement> getAll() {
        return bomItemReplacementRepository.findAll();
    }

    @Override
    public List<BomItemReplacement> getAllById(List<Long> idList) {
        return bomItemReplacementRepository.findAllById(idList);
    }

    @Override
    public BomItemReplacement save(BomItemReplacement object) {
        return bomItemReplacementRepository.save(object);
    }

    @Override
    public List<BomItemReplacement> saveAll(List<BomItemReplacement> objectList) {
        return bomItemReplacementRepository.saveAll(objectList);
    }

    @Override
    public BomItemReplacement read(long id) {
        return bomItemReplacementRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(BomItemReplacement object) {
        bomItemReplacementRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        bomItemReplacementRepository.deleteById(id);
    }

    @Override
    public List<BomItemReplacement> getAllByBomItemId(Long bomItemId) {
        return bomItemReplacementRepository.findAllByBomItemId(bomItemId);
    }

    @Override
    public List<BomItemReplacement> getAllByBomId(Long bomId) {
        return bomItemReplacementRepository.findAllByBomItem_BomId(bomId);
    }

    @Override
    public boolean existsByComponentIdAndBomId(Long componentId, Long bomId) {
        return bomItemReplacementRepository.existsByComponentIdAndBomItem_BomId(componentId, bomId);
    }

    @Override
    public List<BomItemReplacement> getAllByComponentId(Long componentId) {
        return bomItemReplacementRepository.findAllByComponentId(componentId);
    }
}