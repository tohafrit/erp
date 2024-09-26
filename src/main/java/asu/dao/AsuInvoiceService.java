package asu.dao;

import asu.entity.AsuInvoice;
import asu.repository.AsuInvoiceRepository;
import org.springframework.stereotype.Service;

@Service
public class AsuInvoiceService {

    private final AsuInvoiceRepository asuPlantRepository;

    public AsuInvoiceService(AsuInvoiceRepository asuPlantRepository) {
        this.asuPlantRepository = asuPlantRepository;
    }

    public AsuInvoice read(long id) {
        return asuPlantRepository.getOne(id);
    }
}