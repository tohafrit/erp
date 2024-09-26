package asu.dao;

import asu.entity.AsuComponentName;
import asu.entity.AsuComponentName_;
import asu.entity.AsuComponent_;
import asu.entity.AsuGrpComp_;
import asu.repository.AsuComponentNameRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class AsuComponentNameService {

    @PersistenceContext(unitName = "asuEntityManagerFactory")
    private EntityManager em;

    private final AsuComponentNameRepository asuComponentNameRepository;

    public AsuComponentNameService(AsuComponentNameRepository asuComponentNameRepository) {
        this.asuComponentNameRepository = asuComponentNameRepository;
    }

    public AsuComponentName read(long id) {
        return asuComponentNameRepository.getOne(id);
    }

    public AsuComponentName getByCellAndAnalog(String cell, Long analog) {
        if (StringUtils.isNumeric(cell) && cell.length() == 6 && analog != null) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<AsuComponentName> c = cb.createQuery(AsuComponentName.class);
            Root<AsuComponentName> root = c.from(AsuComponentName.class);
            CriteriaQuery<AsuComponentName> select = c.select(root).where(
                cb.equal(root.get(AsuComponentName_.analog), analog),
                cb.equal(root.get(AsuComponentName_.component).get(AsuComponent_.pos), Long.valueOf(StringUtils.substring(cell, 2))),
                cb.equal(root.get(AsuComponentName_.component).get(AsuComponent_.group).get(AsuGrpComp_.nomGrp), Long.valueOf(StringUtils.substring(cell, 0, 2)))
            );
            List<AsuComponentName> resultList = em.createQuery(select).setFirstResult(0).setMaxResults(1).getResultList();
            return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
        }
        return null;
    }
}