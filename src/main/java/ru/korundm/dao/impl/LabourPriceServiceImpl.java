package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.LabourPriceService;
import ru.korundm.entity.LabourPrice;
import ru.korundm.repository.LabourPriceRepository;

import java.util.List;

@Service
@Transactional
public class LabourPriceServiceImpl implements LabourPriceService {

    private final LabourPriceRepository labourPriceRepository;

    public LabourPriceServiceImpl(LabourPriceRepository labourPriceRepository) {
        this.labourPriceRepository = labourPriceRepository;
    }

    @Override
    public List<LabourPrice> getAll() {
        return labourPriceRepository.findAll();
    }

    @Override
    public List<LabourPrice> getAllById(List<Long> idList) {
        return labourPriceRepository.findAllById(idList);
    }

    @Override
    public LabourPrice save(LabourPrice object) {
        return labourPriceRepository.save(object);
    }

    @Override
    public List<LabourPrice> saveAll(List<LabourPrice> objectList) {
        return labourPriceRepository.saveAll(objectList);
    }

    @Override
    public LabourPrice read(long id) {
        return labourPriceRepository.getOne(id);
    }

    @Override
    public void delete(LabourPrice object) {
        labourPriceRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        labourPriceRepository.deleteById(id);
    }
}