package eco.dao;

import eco.entity.*;
import eco.repository.EcoBomRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.List;

@Service
public class EcoBomService {

    @PersistenceContext(unitName = "ecoEntityManagerFactory")
    private EntityManager em;

    private final EcoBomRepository bomRepository;

    public EcoBomService(EcoBomRepository bomRepository) {
        this.bomRepository = bomRepository;
    }

    public EcoBom read(Long id) {
        return id == null ? null : bomRepository.findFirstById(id);
    }

    public List<EcoBom> getAll() {
        return bomRepository.findAll();
    }

    public EcoBom getLastApprovedVersion(long productId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EcoBom> c = cb.createQuery(EcoBom.class);
        Root<EcoBom> bom = c.from(EcoBom.class);
        Join<EcoBom, EcoBomAttribute> attributeJoin = bom.join(EcoBom_.bomAttributeList, JoinType.INNER);
        Join<EcoBomAttribute, EcoLaunch> launchJoin = attributeJoin.join(EcoBomAttribute_.launch, JoinType.INNER);
        c.orderBy(cb.desc(launchJoin.get(EcoLaunch_.year)), cb.desc(launchJoin.get(EcoLaunch_.numberInYear)));
        CriteriaQuery<EcoBom> select = c.select(bom).where(
            cb.equal(bom.get(EcoBom_.product).get(EcoProduct_.id), productId),
            cb.isNotNull(attributeJoin.get(EcoBomAttribute_.approveDate))
        );
        List<EcoBom> resultList = em.createQuery(select).setFirstResult(0).setMaxResults(1).getResultList();
        return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
    }

    public boolean existsById(Long id) {
        return id != null && bomRepository.existsById(id);
    }

    public boolean existsByIdAndProductId(Long id, Long productId) {
        return id != null && productId != null && bomRepository.existsByIdAndProductId(id, productId);
    }

    public List<EcoBom> getAllByProductId(Long productId) {
        return bomRepository.findAllByProductId(productId);
    }

    /**
     * Согласно логике в eco, служебные версии имеют major поле равное 0.
     * Они нам не нужны. Сортировка в пользовательском интерфейсе major asc, minor asc, modification asc
     */
    public List<EcoBom> getActualList(Long productId) {
        return bomRepository.findAllByProductIdAndMajorNotOrderByMajorAscMinorAscModificationAsc(productId, 0);
    }

    public EcoBom lastLaunchVersion(Long launchId, Long productId) {
        return bomRepository.lastLaunchVersion(launchId, productId);
    }

    public List<EcoBom> getAllSortedVersionDesc() {
        return bomRepository.findAllByOrderByMajorDescMinorDescModificationDesc();
    }
}