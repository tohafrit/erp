package ru.korundm.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.constant.ObjAttr;
import ru.korundm.dao.ProductionAreaService;
import ru.korundm.entity.ProductionArea;
import ru.korundm.repository.ProductionAreaRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProductionAreaServiceImpl implements ProductionAreaService {

    @PersistenceContext
    private EntityManager em;

    private final ProductionAreaRepository repository;

    public ProductionAreaServiceImpl(ProductionAreaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ProductionArea> getAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "code"));
    }

    @Override
    public List<ProductionArea> getAllById(List<Long> idList) { return repository.findAllById(idList); }

    @Override
    public ProductionArea save(ProductionArea object) {
        return repository.save(object);
    }

    @Override
    public List<ProductionArea> saveAll(List<ProductionArea> objectList) { return repository.saveAll(objectList); }

    @Override
    public ProductionArea read(long id) {
        return repository.getOne(id);
    }

    @Override
    public void delete(ProductionArea object) {
        repository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        repository.deleteById(id);
    }

    @Override
    public List<ProductionArea> getAllByTechnological(boolean technological) {
        return repository.findAllByTechnologicalOrderByCodeAsc(technological);
    }

    @Override
    public boolean existsByCodeAndIdNot(String code, Long id) {
        return id == null ? repository.existsByCode(code) : repository.existsByCodeAndIdNot(code, id);
    }

    @Override
    public List<ProductionArea> findTableData(List<Long> productionAreaIdList) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductionArea> c = cb.createQuery(ProductionArea.class);
        Root<ProductionArea> root = c.from(ProductionArea.class);
        List<Predicate> predicates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(productionAreaIdList)) {
            predicates.add(cb.not(root.get(ObjAttr.ID).in(productionAreaIdList)));
        }
        CriteriaQuery<ProductionArea> select = c.select(root).where(predicates.toArray(new Predicate[0]));
        return em.createQuery(select).getResultList();
    }
}