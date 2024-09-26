package eco.dao;

import eco.entity.*;
import eco.repository.EcoBomAttributeRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class EcoBomAttributeService {

    @PersistenceContext(unitName = "ecoEntityManagerFactory")
    private EntityManager em;

    private final EcoBomAttributeRepository bomAttributeRepository;

    public EcoBomAttributeService(EcoBomAttributeRepository bomAttributeRepository) {
        this.bomAttributeRepository = bomAttributeRepository;
    }

    public EcoBomAttribute read(long id) {
        return bomAttributeRepository.getOne(id);
    }

    public Boolean isAccepted(EcoBom bom, EcoLaunch launch) {
        return bomAttributeRepository.isAccepted(bom, launch);
    }

    public EcoBomAttribute getApproved(EcoBom bom) {
        return bomAttributeRepository.findFirstByBomOrderByLaunch_YearDescLaunch_NumberInYearDesc(bom);
    }

    public List<EcoBomAttribute> getAll() {
        return bomAttributeRepository.findAll();
    }

    public List<EcoBomAttribute> getDistinctByPurchaseType(Long purchaseType) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EcoBomAttribute> criteria = cb.createQuery(EcoBomAttribute.class);
        Root<EcoBomAttribute> root = criteria.from(EcoBomAttribute.class);

        Join<EcoBomAttribute, EcoBom> bomJoin = root.join(EcoBomAttribute_.bom, JoinType.INNER);
        List<Order> orderList = new ArrayList<>();
        List<Predicate> predicateList = new ArrayList<>();
        switch (purchaseType.intValue()) {
            case 0:
                orderList.add(cb.desc(bomJoin.get(EcoBom_.major)));
                orderList.add(cb.desc(bomJoin.get(EcoBom_.minor)));
                orderList.add(cb.desc(bomJoin.get(EcoBom_.modification)));
                Join<EcoBomAttribute, EcoLaunch> launchJoin = root.join(EcoBomAttribute_.launch, JoinType.INNER);
                orderList.add(cb.desc(launchJoin.get(EcoLaunch_.year)));
                orderList.add(cb.desc(launchJoin.get(EcoLaunch_.numberInYear)));
                predicateList.add(cb.isNotNull(root.get(EcoBomAttribute_.approveDate)));
            case 1:
                orderList.add(cb.desc(bomJoin.get(EcoBom_.major)));
                orderList.add(cb.desc(bomJoin.get(EcoBom_.minor)));
                orderList.add(cb.desc(bomJoin.get(EcoBom_.modification)));
                orderList.add(cb.desc(root.get(EcoBomAttribute_.id)));
            case 2:
                break;
        }
        criteria.orderBy(orderList);

        CriteriaQuery<EcoBomAttribute> select = criteria.select(root).distinct(Boolean.TRUE);
        select.where(predicateList.toArray(new Predicate[0]));
        TypedQuery<EcoBomAttribute> typedQuery = em.createQuery(select);
        return typedQuery.getResultList();
    }

    public List<EcoBomAttribute> getAllByApproveDateIsNotNull() {
        return bomAttributeRepository.findAllByApproveDateIsNotNull();
    }
}