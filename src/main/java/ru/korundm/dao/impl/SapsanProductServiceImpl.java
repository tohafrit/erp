package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.SapsanProductService;
import ru.korundm.entity.SapsanProduct;
import ru.korundm.repository.SapsanProductRepository;

import java.util.List;

@Service
@Transactional
public class SapsanProductServiceImpl implements SapsanProductService {

    private final SapsanProductRepository sapsanProductRepository;

    public SapsanProductServiceImpl(SapsanProductRepository sapsanProductRepository) {
        this.sapsanProductRepository = sapsanProductRepository;
    }

    @Override
    public List<SapsanProduct> getAll() {
        return sapsanProductRepository.findAll();
    }

    @Override
    public List<SapsanProduct> getAllById(List<Long> idList) {
        return sapsanProductRepository.findAllById(idList);
    }

    @Override
    public SapsanProduct save(SapsanProduct object) {
        return sapsanProductRepository.save(object);
    }

    @Override
    public List<SapsanProduct> saveAll(List<SapsanProduct> objectList) {
        return sapsanProductRepository.saveAll(objectList);
    }

    @Override
    public SapsanProduct read(long id) {
        return sapsanProductRepository.getOne(id);
    }

    @Override
    public void delete(SapsanProduct object) {
        sapsanProductRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        sapsanProductRepository.deleteById(id);
    }

    public SapsanProduct getByPrefix(String prefix) {
        return sapsanProductRepository.findByPrefix(prefix);
    }
}