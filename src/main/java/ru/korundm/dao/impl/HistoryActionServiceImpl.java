package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.HistoryActionService;
import ru.korundm.entity.HistoryAction;
import ru.korundm.repository.HistoryActionRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class HistoryActionServiceImpl implements HistoryActionService {

    private final HistoryActionRepository historyActionRepository;

    public HistoryActionServiceImpl(HistoryActionRepository historyActionRepository) {
        this.historyActionRepository = historyActionRepository;
    }

    @Override
    public List<HistoryAction> getAll() {
        return historyActionRepository.findAll();
    }

    @Override
    public List<HistoryAction> getAllById(List<Long> idList) {
        return historyActionRepository.findAllById(idList);
    }

    @Override
    public HistoryAction save(HistoryAction object) {
        throw new UnsupportedOperationException("For historical action types, only read operations are available");
    }

    @Override
    public List<HistoryAction> saveAll(List<HistoryAction> objectList) {
        throw new UnsupportedOperationException("For historical action types, only read operations are available");
    }

    @Override
    public HistoryAction read(long id) {
        return historyActionRepository.getOne(id);
    }

    @Override
    public void delete(HistoryAction object) {
        throw new UnsupportedOperationException("For historical action types, only read operations are available");
    }

    @Override
    public void deleteById(long id) {
        throw new UnsupportedOperationException("For historical action types, only read operations are available");
    }
}