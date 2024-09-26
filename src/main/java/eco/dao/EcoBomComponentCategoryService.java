package eco.dao;

import eco.entity.EcoBomComponentCategory;
import eco.repository.EcoBomComponentCategoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EcoBomComponentCategoryService {

    private final EcoBomComponentCategoryRepository ecoBomComponentCategoryRepository;

    public EcoBomComponentCategoryService(EcoBomComponentCategoryRepository ecoBomComponentCategoryRepository) {
        this.ecoBomComponentCategoryRepository = ecoBomComponentCategoryRepository;
    }

    public EcoBomComponentCategory read(long id) {
        return ecoBomComponentCategoryRepository.findFirstById(id);
    }

    public List<EcoBomComponentCategory> getAll() {
        return ecoBomComponentCategoryRepository.findAll(Sort.by(Sort.Order.asc("orderId")));
    }
}