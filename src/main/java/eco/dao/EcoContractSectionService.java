package eco.dao;

import eco.entity.EcoContractSection;
import eco.repository.EcoContractSectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoContractSectionService {

    private final EcoContractSectionRepository ecoContractSectionRepository;

    public EcoContractSectionService(EcoContractSectionRepository ecoContractSectionRepository) {
        this.ecoContractSectionRepository = ecoContractSectionRepository;
    }

    public List<EcoContractSection> getAllById(List<Long> idList) {
        return ecoContractSectionRepository.findAllById(idList);
    }

    public EcoContractSection read(Long id) {
        return ecoContractSectionRepository.getOne(id);
    }

    public List<EcoContractSection> getAll() {
        return ecoContractSectionRepository.findAll();
    }
}