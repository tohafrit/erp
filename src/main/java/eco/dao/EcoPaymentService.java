package eco.dao;

import eco.entity.EcoPayment;
import eco.repository.EcoPaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoPaymentService {

    private final EcoPaymentRepository ecoPaymentRepository;

    public EcoPaymentService(EcoPaymentRepository ecoPaymentRepository) {
        this.ecoPaymentRepository = ecoPaymentRepository;
    }

    public EcoPayment read(Long id) {
        return ecoPaymentRepository.getOne(id);
    }

    public List<EcoPayment> getAll() {
        return ecoPaymentRepository.findAll();
    }
}