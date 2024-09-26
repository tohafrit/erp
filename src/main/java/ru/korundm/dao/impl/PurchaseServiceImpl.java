package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.PurchaseService;
import ru.korundm.entity.Purchase;
import ru.korundm.repository.PurchaseRepository;

import java.util.List;

@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public void deleteAll() {
        purchaseRepository.deleteAll();
    }

    @Override
    public List<Purchase> getAll() {
        return purchaseRepository.findAll();
    }

    @Override
    public List<Purchase> getAllById(List<Long> idList) {
        return purchaseRepository.findAllById(idList);
    }

    @Override
    public Purchase save(Purchase object) {
        return purchaseRepository.save(object);
    }

    @Override
    public List<Purchase> saveAll(List<Purchase> objectList) {
        return purchaseRepository.saveAll(objectList);
    }

    @Override
    public Purchase read(long id) {
        return purchaseRepository.getOne(id);
    }

    @Override
    public void delete(Purchase object) {
        purchaseRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        purchaseRepository.deleteById(id);
    }
}