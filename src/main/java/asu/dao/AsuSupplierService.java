package asu.dao;

import asu.entity.AsuSupplier;
import asu.repository.AsuSupplierRepository;
import org.springframework.stereotype.Service;

@Service
public class AsuSupplierService {

    private final AsuSupplierRepository asuSupplierRepository;

    public AsuSupplierService(AsuSupplierRepository asuSupplierRepository) {
        this.asuSupplierRepository = asuSupplierRepository;
    }

    public AsuSupplier read(long id) {
        return asuSupplierRepository.getOne(id);
    }
}