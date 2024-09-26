package ru.korundm.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.constant.ObjAttr;
import ru.korundm.dao.EquipmentService;
import ru.korundm.entity.*;
import ru.korundm.form.search.EquipmentListFilterForm;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrSorter;
import ru.korundm.repository.EquipmentRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

    @PersistenceContext
    private EntityManager em;

    private final EquipmentRepository equipmentRepository;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public List<Equipment> getAll() { return equipmentRepository.findAll(); }

    @Override
    public List<Equipment> getAllById(List<Long> idList) { return equipmentRepository.findAllById(idList); }

    @Override
    public Equipment save(Equipment object) { return equipmentRepository.save(object); }

    @Override
    public List<Equipment> saveAll(List<Equipment> objectList) { return equipmentRepository.saveAll(objectList); }

    @Override
    public Equipment read(long id) { return equipmentRepository.getOne(id); }

    @Override
    public void delete(Equipment object) { equipmentRepository.delete(object); }

    @Override
    public void deleteById(long id) { equipmentRepository.deleteById(id); }

    @Override
    public List<Equipment> getByTableDataIn(TabrIn tableDataIn, EquipmentListFilterForm form) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Equipment> criteria = cb.createQuery(Equipment.class);
        Root<Equipment> root = criteria.from(Equipment.class);
        List<Predicate> predicateList = getFormPredicateList(form, root, cb);
        CriteriaQuery<Equipment> select = criteria.select(root)
                .where(predicateList.toArray(new Predicate[0]));
        // Сортировка
        if (CollectionUtils.isNotEmpty(tableDataIn.getSorters())) {
            TabrSorter sorter = tableDataIn.getSorters().get(0);
            List<Path<?>> orderExpressionList = getFormOrder(root, sorter);
            List<Order> orderList = ASC.equals(sorter.getDir()) ?
                orderExpressionList.stream().map(cb::asc).collect(Collectors.toList()) : orderExpressionList.stream().map(cb::desc).collect(Collectors.toList());
            criteria.orderBy(orderList);
        }
        TypedQuery<Equipment> typedQuery = em.createQuery(select);
        typedQuery.setFirstResult(tableDataIn.getStart());
        typedQuery.setMaxResults(tableDataIn.getSize());
        return typedQuery.getResultList();
    }

    @Override
    public long getCountByForm(EquipmentListFilterForm form) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
        Root<Equipment> root = criteria.from(Equipment.class);
        List<Predicate> predicateList = getFormPredicateList(form, root, cb);
        criteria.select(cb.count(root.get(Equipment_.id))).distinct(Boolean.TRUE).where(predicateList.toArray(new Predicate[0]));
        return em.createQuery(criteria).getSingleResult();
    }

    private List<Predicate> getFormPredicateList(
            EquipmentListFilterForm form,
            Root<Equipment> root,
            CriteriaBuilder cb
    ) {
        List<Predicate> predicateList = new ArrayList<>();
        // Тип оборудования
        predicateList.add(cb.equal(root.get(Equipment_.equipmentType), form.getEquipmentTypeId()));
        // Наименование
        if (StringUtils.isNotBlank(form.getName())) {
            predicateList.add(cb.like(root.get(Equipment_.name), "%" + form.getName() + "%"));
        }
        // Участок
        if (!form.getProductionAreaIdList().isEmpty()) {
            Join<Equipment, EquipmentUnit> equipmentUnitJoin = root.join(Equipment_.equipmentUnitList, JoinType.INNER);
            Join<EquipmentUnit, EquipmentUnitProductionArea> unitProductionAreaJoin = equipmentUnitJoin.join(EquipmentUnit_.lastEquipmentUnitProductionArea);
            predicateList.add(unitProductionAreaJoin.get(EquipmentUnitProductionArea_.productionArea).in(form.getProductionAreaIdList()));
        }
        // Исключить оборудование из поиска
        if (!form.getExcludeEquipmentIdList().isEmpty()) {
            predicateList.add(cb.not(root.get(Equipment_.id).in(form.getExcludeEquipmentIdList())));
        }
        // Архивность
        if (form.getArchive() != null) {
            predicateList.add(cb.equal(root.get(Equipment_.archive), form.getArchive()));
        }
        return predicateList;
    }

    private List<Path<?>> getFormOrder(Root<Equipment> root, TabrSorter sorter) {
        List<Path<?>> orderExpressionList = new ArrayList<>();
        switch(sorter.getField()) {
            case "name":
                orderExpressionList.add(root.get(Equipment_.name));
                break;
            case "producerName":
                Join<Equipment, Producer> productJoin = root.join(Equipment_.producer, JoinType.LEFT);
                orderExpressionList.add(productJoin.get(Producer_.name));
                break;
            case "model":
                orderExpressionList.add(root.get(Equipment_.model));
                break;
            case "weight":
                orderExpressionList.add(root.get(Equipment_.weight));
                break;
            case "voltage":
                orderExpressionList.add(root.get(Equipment_.voltage));
                break;
            case "power":
                orderExpressionList.add(root.get(Equipment_.power));
                break;
            case "dimensions":
                orderExpressionList.add(root.get(Equipment_.dimensions));
                break;
            case "compressedAirPressure":
                orderExpressionList.add(root.get(Equipment_.compressedAirPressure));
                break;
            case "compressedAirConsumption":
                orderExpressionList.add(root.get(Equipment_.compressedAirConsumption));
                break;
            case "nitrogenPressure":
                orderExpressionList.add(root.get(Equipment_.nitrogenPressure));
                break;
            case "water":
                orderExpressionList.add(root.get(Equipment_.water));
                break;
            case "sewerage":
                orderExpressionList.add(root.get(Equipment_.sewerage));
                break;
            case "extractorVolume":
                orderExpressionList.add(root.get(Equipment_.extractorVolume));
                break;
            case "extractorDiameter":
                orderExpressionList.add(root.get(Equipment_.extractorDiameter));
                break;
            case "link":
                orderExpressionList.add(root.get(Equipment_.link));
                break;
            case "shift":
                orderExpressionList.add(root.get(Equipment_.shift));
                break;
            case "user":
                Join<Equipment, User> userJoin = root.join(Equipment_.user, JoinType.LEFT);
                orderExpressionList.add(userJoin.get(User_.lastName));
                break;
            case "code":
                Join<Equipment, EquipmentType> equipmentTypeJoin = root.join(Equipment_.equipmentType, JoinType.INNER);
                Join<Equipment, EquipmentUnit> equipmentUnitJoin = root.join(Equipment_.equipmentUnitList, JoinType.INNER);
                Join<EquipmentUnit, EquipmentUnitProductionArea> upaJoin = equipmentUnitJoin.join(EquipmentUnit_.lastEquipmentUnitProductionArea, JoinType.INNER);
                Join<EquipmentUnitProductionArea, ProductionArea> productionAreaJoin = upaJoin.join(EquipmentUnitProductionArea_.productionArea, JoinType.INNER);
                orderExpressionList.add(productionAreaJoin.get(ProductionArea_.code));
                orderExpressionList.add(equipmentTypeJoin.get(EquipmentType_.code));
                orderExpressionList.add(equipmentUnitJoin.get(EquipmentUnit_.code));
                break;
            case "areaName":
                Join<Equipment, EquipmentUnit> euJoin = root.join(Equipment_.equipmentUnitList, JoinType.INNER);
                Join<EquipmentUnit, EquipmentUnitProductionArea> unitProductionAreaJoin = euJoin.join(EquipmentUnit_.lastEquipmentUnitProductionArea);
                orderExpressionList.add(unitProductionAreaJoin.get(EquipmentUnitProductionArea_.productionArea));
                break;
            default:
                orderExpressionList.add(root.get(Equipment_.id));
        }
        return orderExpressionList;
    }

    @Override
    public List<Equipment> findTableData(List<Long> equipmentIdList) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Equipment> c = cb.createQuery(Equipment.class);
        Root<Equipment> root = c.from(Equipment.class);
        List<Predicate> predicates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(equipmentIdList)) {
            predicates.add(cb.not(root.get(ObjAttr.ID).in(equipmentIdList)));
        }
        CriteriaQuery<Equipment> select = c.select(root).where(predicates.toArray(new Predicate[0]));
        return em.createQuery(select).getResultList();
    }
}