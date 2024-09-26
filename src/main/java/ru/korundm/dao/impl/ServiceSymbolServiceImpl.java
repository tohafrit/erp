package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ServiceSymbolService;
import ru.korundm.entity.ServiceSymbol;
import ru.korundm.repository.ServiceSymbolRepository;

import java.util.List;

@Service
@Transactional
public class ServiceSymbolServiceImpl implements ServiceSymbolService {

    private final ServiceSymbolRepository serviceSymbolRepository;

    public ServiceSymbolServiceImpl(ServiceSymbolRepository serviceSymbolRepository) {
        this.serviceSymbolRepository = serviceSymbolRepository;
    }

    @Override
    public List<ServiceSymbol> getAll() {
        return serviceSymbolRepository.findAll();
    }

    @Override
    public List<ServiceSymbol> getAllById(List<Long> idList) {
        return serviceSymbolRepository.findAllById(idList);
    }

    @Override
    public ServiceSymbol save(ServiceSymbol object) {
        return serviceSymbolRepository.save(object);
    }

    @Override
    public List<ServiceSymbol> saveAll(List<ServiceSymbol> objectList) {
        return serviceSymbolRepository.saveAll(objectList);
    }

    @Override
    public ServiceSymbol read(long id) {
        return serviceSymbolRepository.getOne(id);
    }

    @Override
    public void delete(ServiceSymbol object) {
        serviceSymbolRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        serviceSymbolRepository.deleteById(id);
    }

    @Override
    public ServiceSymbol getByCode(String code) {
        return serviceSymbolRepository.findFirstByCode(code);
    }

    @Override
    public List<ServiceSymbol> getAllByTechnologicalProcess(boolean technicalProcess) {
        return serviceSymbolRepository.findAllByTechnologicalProcess(technicalProcess);
    }

    @Override
    public List<ServiceSymbol> getAllByOperationCard(boolean operationCard) {
        return serviceSymbolRepository.findAllByOperationCard(operationCard);
    }

    @Override
    public List<ServiceSymbol> getAllByRouteMap(boolean routeMap) {
        return serviceSymbolRepository.findAllByRouteMap(routeMap);
    }
}