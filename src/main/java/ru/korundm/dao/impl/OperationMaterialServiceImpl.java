package ru.korundm.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.constant.ObjAttr;
import ru.korundm.dao.OperationMaterialService;
import ru.korundm.entity.OperationMaterial;
import ru.korundm.entity.WorkType;
import ru.korundm.repository.OperationMaterialRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class OperationMaterialServiceImpl implements OperationMaterialService {

    @PersistenceContext
    private EntityManager em;

    private final OperationMaterialRepository operationMaterialRepository;

    public OperationMaterialServiceImpl(OperationMaterialRepository operationMaterialRepository) {
        this.operationMaterialRepository = operationMaterialRepository;
    }

    @Override
    public List<OperationMaterial> getAll() {
        return operationMaterialRepository.findAll();
    }

    @Override
    public List<OperationMaterial> getAllById(List<Long> idList) {
        return operationMaterialRepository.findAllById(idList);
    }

    @Override
    public OperationMaterial save(OperationMaterial object) {
        return operationMaterialRepository.save(object);
    }

    @Override
    public List<OperationMaterial> saveAll(List<OperationMaterial> objectList) {
        return operationMaterialRepository.saveAll(objectList);
    }

    @Override
    public OperationMaterial read(long id) {
        return operationMaterialRepository.getOne(id);
    }

    @Override
    public void delete(OperationMaterial object) {
        operationMaterialRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        operationMaterialRepository.deleteById(id);
    }

    @Override
    public List<OperationMaterial> findTableData(List<Long> materialIdList, Long workTypeId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OperationMaterial> c = cb.createQuery(OperationMaterial.class);
        Root<OperationMaterial> root = c.from(OperationMaterial.class);
        List<Predicate> predicates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(materialIdList)) {
            predicates.add(cb.not(root.get(ObjAttr.ID).in(materialIdList)));
        }
        if (workTypeId != null) {
            Join<OperationMaterial, WorkType> workTypeJoin = root.join("workTypeList", JoinType.LEFT);
            predicates.add(cb.and(cb.equal(workTypeJoin.get("id"), workTypeId)));
        }
        CriteriaQuery<OperationMaterial> select = c.select(root).where(predicates.toArray(new Predicate[0]));
        return em.createQuery(select).getResultList();
    }
}