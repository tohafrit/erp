package eco.dao;

import eco.entity.EcoAccount;
import eco.repository.EcoAccountRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class EcoAccountService {

    @PersistenceContext(unitName = "ecoEntityManagerFactory")
    private EntityManager entityManager;

    private final EcoAccountRepository ecoAccountRepository;

    public EcoAccountService(EcoAccountRepository ecoAccountRepository) {
        this.ecoAccountRepository = ecoAccountRepository;
    }

    public EcoAccount read(long id) {
        return ecoAccountRepository.getOne(id);
    }

    public List<EcoAccount> getAll() {
        return ecoAccountRepository.findAll();
    }
}