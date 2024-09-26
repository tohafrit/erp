package eco.dao;

import eco.entity.EcoLabourPrice;
import eco.repository.EcoLabourPriceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoLabourPriceService {

    private final EcoLabourPriceRepository ecoLabourPriceRepository;

    public EcoLabourPriceService(EcoLabourPriceRepository ecoLabourPriceRepository) {
        this.ecoLabourPriceRepository = ecoLabourPriceRepository;
    }

    public List<EcoLabourPrice> getAllByProtocolId(Long protocolId) {
        return ecoLabourPriceRepository.findAllByProtocolId(protocolId);
    }

    public List<EcoLabourPrice> getAll() {
        return ecoLabourPriceRepository.findAll();
    }
}