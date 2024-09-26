package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProducerService;
import ru.korundm.entity.Producer;
import ru.korundm.repository.ProducerRepository;

import java.util.List;

@Service
@Transactional
public class ProducerServiceImpl implements ProducerService {

    private final ProducerRepository producerRepository;

    public ProducerServiceImpl(ProducerRepository producerRepository) {
        this.producerRepository = producerRepository;
    }

    @Override
    public List<Producer> getAll() {
        return producerRepository.findAll();
    }

    @Override
    public List<Producer> getAllById(List<Long> idList) { return producerRepository.findAllById(idList); }

    @Override
    public Producer save(Producer object) {
        return producerRepository.save(object);
    }

    @Override
    public List<Producer> saveAll(List<Producer> objectList) { return producerRepository.saveAll(objectList); }

    @Override
    public Producer read(long id) {
        return producerRepository.getOne(id);
    }

    @Override
    public void delete(Producer object) {
        producerRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        producerRepository.deleteById(id);
    }
}