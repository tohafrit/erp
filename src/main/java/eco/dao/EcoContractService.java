package eco.dao;

import eco.entity.EcoContract;
import eco.entity.EcoContractSection;
import eco.entity.EcoContractSection_;
import eco.entity.EcoContract_;
import eco.repository.EcoContractRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class EcoContractService {

    @PersistenceContext(unitName = "ecoEntityManagerFactory")
    private EntityManager entityManager;

    private final EcoContractRepository ecoContractRepository;

    public EcoContractService(EcoContractRepository ecoContractRepository) {
        this.ecoContractRepository = ecoContractRepository;
    }

    public EcoContract read(long id) {
        return ecoContractRepository.getOne(id);
    }

    public List<EcoContract> getAll() {
        return ecoContractRepository.findAll();
    }

    public List<EcoContract> getByParams(
        Long performer,
        Long contractNumber,
        Long contractType,
        int year
    ) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EcoContract> criteria = criteriaBuilder.createQuery(EcoContract.class);
        Root<EcoContract> root = criteria.from(EcoContract.class);
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(criteriaBuilder.equal(root.get(EcoContract_.contractType), contractType));
        predicateList.add(criteriaBuilder.equal(root.get(EcoContract_.performer), performer));
        predicateList.add(criteriaBuilder.equal(root.get(EcoContract_.contractNumber), contractNumber));

        // Ограничение по году
        Join<EcoContract, EcoContractSection> contractSectionJoin = root.join(EcoContract_.sectionList, JoinType.INNER);
        predicateList.add(criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, contractSectionJoin.get(EcoContractSection_.date)), year));

        CriteriaQuery<EcoContract> select = criteria.select(root).distinct(Boolean.TRUE);
        select.where(predicateList.toArray(new Predicate[0]));
        TypedQuery<EcoContract> typedQuery = entityManager.createQuery(select);
        return typedQuery.getResultList();
    }
}