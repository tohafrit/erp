package eco.dao;

import eco.entity.EcoBomComponent;
import eco.repository.EcoBomComponentRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class EcoBomComponentService {

    @PersistenceContext(unitName = "ecoEntityManagerFactory")
    private EntityManager em;

    private final EcoBomComponentRepository ecoBomComponentRepository;

    public EcoBomComponentService(EcoBomComponentRepository ecoBomComponentRepository) {
        this.ecoBomComponentRepository = ecoBomComponentRepository;
    }

    public List<EcoBomComponent> getAll() {
        return ecoBomComponentRepository.findAll();
    }

    public EcoBomComponent read(long id) {
        return ecoBomComponentRepository.findFirstById(id);
    }
}