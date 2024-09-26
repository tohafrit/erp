package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.TechnologicalEntityStarService;
import ru.korundm.entity.*;
import ru.korundm.repository.TechnologicalEntityStarRepository;

import java.util.List;

@Service
@Transactional
public class TechnologicalEntityStarServiceImpl implements TechnologicalEntityStarService {

    private final TechnologicalEntityStarRepository repository;

    public TechnologicalEntityStarServiceImpl(TechnologicalEntityStarRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TechnologicalEntityStar> getAll() {
        return repository.findAll();
    }

    @Override
    public List<TechnologicalEntityStar> getAllById(List<Long> idList) {
        return repository.findAllById(idList);
    }

    @Override
    public TechnologicalEntityStar save(TechnologicalEntityStar object) {
        return repository.save(object);
    }

    @Override
    public List<TechnologicalEntityStar> saveAll(List<TechnologicalEntityStar> objectList) {
        return repository.saveAll(objectList);
    }

    @Override
    public TechnologicalEntityStar read(long id) {
        return repository.getOne(id);
    }

    @Override
    public void delete(TechnologicalEntityStar object) {
        repository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        repository.deleteById(id);
    }
}