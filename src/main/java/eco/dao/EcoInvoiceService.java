package eco.dao;

import eco.entity.EcoInvoice;
import eco.repository.EcoInvoiceRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "ecoInvoiceService")
public class EcoInvoiceService {

    private final EcoInvoiceRepository invoiceRepository;

    public EcoInvoiceService(@Qualifier("ecoInvoiceRepository") EcoInvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<EcoInvoice> getAll() {
        return invoiceRepository.findAll();
    }

    public EcoInvoice read(Long id) {
        return invoiceRepository.getOne(id);
    }
}