package eco.dao;

import eco.entity.EcoSapsanProduct;
import eco.repository.EcoSapsanProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoSapsanProductService {

    @Autowired
    private EcoSapsanProductRepository sapsanProductRepository;

    public EcoSapsanProduct getByPrefix(String prefix) {
        return sapsanProductRepository.findByPrefix(prefix);
    }

    public List<EcoSapsanProduct> getAll() {
        return sapsanProductRepository.findAll();
    }
}