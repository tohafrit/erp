package ru.korundm.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.SupplierService;
import ru.korundm.entity.Supplier;
import ru.korundm.entity.Supplier_;
import ru.korundm.form.search.SupplierFilterForm;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrResultQuery;
import ru.korundm.helper.TabrSorter;
import ru.korundm.repository.SupplierRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.domain.Sort.*;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    @PersistenceContext
    private EntityManager entityManager;

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public List<Supplier> getAll() {
        return supplierRepository.findAll();
    }

    @Override
    public List<Supplier> getAllById(List<Long> idList) { return supplierRepository.findAllById(idList); }

    @Override
    public Supplier save(Supplier object) {
        return supplierRepository.save(object);
    }

    @Override
    public List<Supplier> saveAll(List<Supplier> objectList) { return supplierRepository.saveAll(objectList); }

    @Override
    public Supplier read(long id) {
        return supplierRepository.getOne(id);
    }

    @Override
    public void delete(Supplier object) {
        supplierRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        supplierRepository.deleteById(id);
    }

    @Override
    public TabrResultQuery<Supplier> queryDataByFilterForm(
        TabrIn tableDataIn,
        SupplierFilterForm form
    ) {
        CriteriaBuilder cbData = entityManager.getCriteriaBuilder();
        CriteriaQuery<Supplier> cqData = cbData.createQuery(Supplier.class);
        Root<Supplier> rootData = cqData.from(Supplier.class);
        List<Predicate> cbDataPredicateList = predicateListByFilterForm(form, rootData, cbData);
        CriteriaQuery<Supplier> selectData = cqData.select(rootData);
        selectData.where(cbDataPredicateList.toArray(new Predicate[0]));
        List<TabrSorter> sorterList = tableDataIn.getSorters();
        if (CollectionUtils.isNotEmpty(sorterList)) {
            TabrSorter sorter = sorterList.get(0);
            Expression<?> expression;
            switch(sorter.getField()) {
                case "name":
                    expression = rootData.get(Supplier_.name);
                    break;
                case "inn":
                    expression = rootData.get(Supplier_.inn);
                    break;
                case "kpp":
                    expression = rootData.get(Supplier_.kpp);
                    break;
                default:
                    expression = rootData.get(Supplier_.id);
            }
            cqData.orderBy(ASC.equals(sorter.getDir()) ? cbData.asc(expression) : cbData.desc(expression));
        }
        TypedQuery<Supplier> tqData = entityManager.createQuery(selectData);
        tqData.setFirstResult(tableDataIn.getStart());
        tqData.setMaxResults(tableDataIn.getSize());
        CriteriaBuilder cbCount = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cCount = cbCount.createQuery(Long.class);
        Root<Supplier> rootCount = cCount.from(Supplier.class);
        List<Predicate> cbCountPredicateList = predicateListByFilterForm(form, rootCount, cbCount);
        cCount.select(cbCount.countDistinct(rootCount)).where(cbCountPredicateList.toArray(new Predicate[0]));
        return new TabrResultQuery<>(tqData.getResultList(), entityManager.createQuery(cCount).getSingleResult());
    }

    private List<Predicate> predicateListByFilterForm(SupplierFilterForm form, Root<Supplier> root, CriteriaBuilder cb) {
        List<Predicate> predicateList = new ArrayList<>();
        if (StringUtils.isNotBlank(form.getName())) {
            predicateList.add(cb.like(root.get(Supplier_.name), "%" + form.getName() + "%"));
        }
        if (StringUtils.isNotBlank(form.getInn())) {
            predicateList.add(cb.like(root.get(Supplier_.inn).as(String.class), "%" + form.getInn() + "%"));
        }
        if (StringUtils.isNotBlank(form.getKpp())) {
            predicateList.add(cb.like(root.get(Supplier_.kpp).as(String.class), "%" + form.getKpp() + "%"));
        }
        return predicateList;
    }

    @Override
    public long getCount() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Supplier> root = criteriaQuery.from(Supplier.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }
}