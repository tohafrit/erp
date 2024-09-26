package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.BomItemPositionService;
import ru.korundm.entity.BomItemPosition;
import ru.korundm.repository.BomItemPositionRepository;

import java.util.List;

@Service
@Transactional
public class BomItemPositionServiceImpl implements BomItemPositionService {

    private final BomItemPositionRepository bomItemPositionRepository;

    public BomItemPositionServiceImpl(BomItemPositionRepository bomItemPositionRepository) {
        this.bomItemPositionRepository = bomItemPositionRepository;
    }

    @Override
    public List<BomItemPosition> getAll() {
        return bomItemPositionRepository.findAll();
    }

    @Override
    public List<BomItemPosition> getAllById(List<Long> idList) {
        return bomItemPositionRepository.findAllById(idList);
    }

    @Override
    public BomItemPosition save(BomItemPosition object) {
        return bomItemPositionRepository.save(object);
    }

    @Override
    public List<BomItemPosition> saveAll(List<BomItemPosition> objectList) {
        return bomItemPositionRepository.saveAll(objectList);
    }

    @Override
    public BomItemPosition read(long id) {
        return bomItemPositionRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(BomItemPosition object) {
        bomItemPositionRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        bomItemPositionRepository.deleteById(id);
    }

    @Override
    public List<BomItemPosition> getAllByBomItemId(Long bomItemId) {
        return bomItemPositionRepository.findAllByBomItemId(bomItemId);
    }

    @Override
    public long getCountAllByBomItemId(Long bomItemId) {
        return bomItemPositionRepository.countAllByBomItemId(bomItemId);
    }
}