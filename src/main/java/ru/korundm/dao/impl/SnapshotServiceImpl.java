package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.SnapshotService;
import ru.korundm.entity.Snapshot;
import ru.korundm.repository.SnapshotRepository;

import java.util.List;

@Service
@Transactional
public class SnapshotServiceImpl implements SnapshotService {

    private final SnapshotRepository snapshotRepository;

    public SnapshotServiceImpl(SnapshotRepository snapshotRepository) {
        this.snapshotRepository = snapshotRepository;
    }

    @Override
    public void deleteAll() {
        snapshotRepository.deleteAll();
    }

    @Override
    public List<Snapshot> getAll() {
        return snapshotRepository.findAll();
    }

    @Override
    public List<Snapshot> getAllById(List<Long> idList) {
        return snapshotRepository.findAllById(idList);
    }

    @Override
    public Snapshot save(Snapshot object) {
        return snapshotRepository.save(object);
    }

    @Override
    public List<Snapshot> saveAll(List<Snapshot> objectList) {
        return snapshotRepository.saveAll(objectList);
    }

    @Override
    public Snapshot read(long id) {
        return snapshotRepository.getOne(id);
    }

    @Override
    public void delete(Snapshot object) {
        snapshotRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        snapshotRepository.deleteById(id);
    }
}