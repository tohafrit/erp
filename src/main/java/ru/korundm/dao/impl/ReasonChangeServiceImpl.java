package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ReasonChangeService;
import ru.korundm.entity.ReasonChange;
import ru.korundm.repository.ReasonChangeRepository;

import java.util.List;

@Service
@Transactional
public class ReasonChangeServiceImpl implements ReasonChangeService {

    private final ReasonChangeRepository reasonChangeRepository;

    public ReasonChangeServiceImpl(ReasonChangeRepository reasonChangeRepository) {
        this.reasonChangeRepository = reasonChangeRepository;
    }

    @Override
    public List<ReasonChange> getAll() { return reasonChangeRepository.findAll(); }

    @Override
    public List<ReasonChange> getAllById(List<Long> idList) { return reasonChangeRepository.findAllById(idList); }

    @Override
    public ReasonChange save(ReasonChange object) { return reasonChangeRepository.save(object); }

    @Override
    public List<ReasonChange> saveAll(List<ReasonChange> objectList) { return reasonChangeRepository.saveAll(objectList); }

    @Override
    public ReasonChange read(long id) { return reasonChangeRepository.getOne(id); }

    @Override
    public void delete(ReasonChange object) { reasonChangeRepository.delete(object); }

    @Override
    public void deleteById(long id) { reasonChangeRepository.deleteById(id); }
}
