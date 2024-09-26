package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.OkeiService;
import ru.korundm.entity.Okei;
import ru.korundm.repository.OkeiRepository;

import java.util.List;

@Service
@Transactional
public class OkeiServiceImpl implements OkeiService {

    private final OkeiRepository okeiRepository;

    public OkeiServiceImpl(OkeiRepository okeiRepository) {
        this.okeiRepository = okeiRepository;
    }

    @Override
    public List<Okei> getAll() {
        return okeiRepository.findAll();
    }

    @Override
    public List<Okei> getAllById(List<Long> idList) { return okeiRepository.findAllById(idList); }

    @Override
    public Okei save(Okei object) {
        return okeiRepository.save(object);
    }

    @Override
    public List<Okei> saveAll(List<Okei> objectList) { return okeiRepository.saveAll(objectList); }

    @Override
    public Okei read(long id) {
        return okeiRepository.getOne(id);
    }

    @Override
    public void delete(Okei object) {
        okeiRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        okeiRepository.deleteById(id);
    }

    @Override
    public Okei getByCode(String code) {
        return okeiRepository.findFirstByCode(code);
    }
}