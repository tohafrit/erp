package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.BomAttributeService;
import ru.korundm.entity.BomAttribute;
import ru.korundm.repository.BomAttributeRepository;

import java.util.List;

@Service
@Transactional
public class BomAttributeServiceImpl implements BomAttributeService {

    private final BomAttributeRepository bomAttributeRepository;

    public BomAttributeServiceImpl(BomAttributeRepository bomAttributeRepository) {
        this.bomAttributeRepository = bomAttributeRepository;
    }

    @Override
    public List<BomAttribute> getAll() {
        return bomAttributeRepository.findAll();
    }

    @Override
    public List<BomAttribute> getAllById(List<Long> idList) {
        return bomAttributeRepository.findAllById(idList);
    }

    @Override
    public BomAttribute save(BomAttribute object) {
        return bomAttributeRepository.save(object);
    }

    @Override
    public List<BomAttribute> saveAll(List<BomAttribute> objectList) {
        return bomAttributeRepository.saveAll(objectList);
    }

    @Override
    public BomAttribute read(long id) {
        return bomAttributeRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(BomAttribute object) {
        bomAttributeRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        bomAttributeRepository.deleteById(id);
    }

    @Override
    public List<BomAttribute> getAllByBomId(Long bomId) {
        return bomAttributeRepository.findAllByBomId(bomId);
    }

    @Override
    public BomAttribute getByLaunchIdAndBomId(Long launchId, Long bomId) {
        return launchId == null || bomId == null ? null : bomAttributeRepository.findTopByLaunchIdAndBomId(launchId, bomId);
    }
}