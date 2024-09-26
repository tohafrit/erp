package eco.dao;

import eco.entity.EcoSapsanProductBom;
import eco.repository.EcoSapsanProductBomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoSapsanProductBomService {

    private final EcoSapsanProductBomRepository sapsanProductBomRepository;

    public EcoSapsanProductBomService(EcoSapsanProductBomRepository sapsanProductBomRepository) {
        this.sapsanProductBomRepository = sapsanProductBomRepository;
    }

    public List<EcoSapsanProductBom> getAll() {
        return sapsanProductBomRepository.findAll();
    }
}