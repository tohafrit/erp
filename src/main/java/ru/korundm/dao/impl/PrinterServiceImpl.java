package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.PrinterService;
import ru.korundm.entity.Printer;
import ru.korundm.repository.PrinterRepository;

import java.util.List;

@Service
@Transactional
public class PrinterServiceImpl implements PrinterService {

    private final PrinterRepository  printerRepository;

    public PrinterServiceImpl(PrinterRepository printerRepository) {
        this.printerRepository = printerRepository;
    }

    @Override
    public List<Printer> getAll() {
        return printerRepository.findAll();
    }

    @Override
    public List<Printer> getAllById(List<Long> idList) {
        return printerRepository.findAllById(idList);
    }

    @Override
    public Printer save(Printer object) {
        return printerRepository.save(object);
    }

    @Override
    public List<Printer> saveAll(List<Printer> objectList) {
        return printerRepository.saveAll(objectList);
    }

    @Override
    public Printer read(long id) {
        return printerRepository.getOne(id);
    }

    @Override
    public void delete(Printer object) {
        printerRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        printerRepository.deleteById(id);
    }
}
