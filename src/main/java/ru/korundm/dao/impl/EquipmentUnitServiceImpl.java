package ru.korundm.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.EquipmentUnitService;
import ru.korundm.entity.*;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrResultQuery;
import ru.korundm.helper.TabrSorter;
import ru.korundm.repository.EquipmentUnitRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@Transactional
public class EquipmentUnitServiceImpl implements EquipmentUnitService {

    @PersistenceContext
    private EntityManager em;

    private final EquipmentUnitRepository equipmentUnitRepository;

    public EquipmentUnitServiceImpl(EquipmentUnitRepository equipmentUnitRepository) {
        this.equipmentUnitRepository = equipmentUnitRepository;
    }

    @Override
    public List<EquipmentUnit> getAll() { return equipmentUnitRepository.findAll(); }

    @Override
    public List<EquipmentUnit> getAllById(List<Long> idList) { return equipmentUnitRepository.findAllById(idList); }

    @Override
    public EquipmentUnit save(EquipmentUnit object) { return equipmentUnitRepository.save(object); }

    @Override
    public List<EquipmentUnit> saveAll(List<EquipmentUnit> objectList) { return equipmentUnitRepository.saveAll(objectList); }

    @Override
    public EquipmentUnit read(long id) { return equipmentUnitRepository.getOne(id); }

    @Override
    public void delete(EquipmentUnit object) { equipmentUnitRepository.delete(object); }

    @Override
    public void deleteById(long id) { equipmentUnitRepository.deleteById(id); }

    @Override
    public List<EquipmentUnit> getAllByEquipment(Equipment equipment) { return equipmentUnitRepository.findAllByEquipment(equipment); }

    @Override
    public List<EquipmentUnit> getAllByEquipmentId(Long equipmentId) { return equipmentUnitRepository.findAllByEquipmentId(equipmentId); }

    @Override
    public List<EquipmentUnit> getAllByIdIsNotInAndEquipment(List<Long> idList, Equipment equipment) {
        return equipmentUnitRepository.findAllByIdIsNotInAndEquipment(idList, equipment);
    }

    @Override
    public void deleteAllByEquipment(Equipment equipment) { equipmentUnitRepository.deleteAllByEquipment(equipment); }

    @Override
    public void deleteAll(List<EquipmentUnit> equipmentUnitList) { equipmentUnitRepository.deleteAll(equipmentUnitList); }

    @Override
    public EquipmentUnit getByAreaIdAndEquipmentTypeId(Long areaId, Long equipmentTypeId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipmentUnit> criteria = cb.createQuery(EquipmentUnit.class);
        Root<Equipment> root = criteria.from(Equipment.class);
        Join<Equipment, EquipmentUnit> equipmentUnitJoin = root.join(Equipment_.equipmentUnitList, JoinType.INNER);
        Join<EquipmentUnit, EquipmentUnitProductionArea> unitProductionAreaJoin = equipmentUnitJoin.join(EquipmentUnit_.equipmentUnitProductionAreaList, JoinType.INNER);
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(cb.equal(root.get(Equipment_.equipmentType), equipmentTypeId));
        predicateList.add(cb.equal(unitProductionAreaJoin.get(EquipmentUnitProductionArea_.productionArea), areaId));
        CriteriaQuery<EquipmentUnit> select = criteria.select(equipmentUnitJoin)
                .orderBy(cb.desc(unitProductionAreaJoin.get(EquipmentUnitProductionArea_.movedOn))).where(predicateList.toArray(new Predicate[0]));
        List<EquipmentUnit> equipmentUnitList = em.createQuery(select).getResultList();
        return !equipmentUnitList.isEmpty() ? equipmentUnitList.get(0) : null;
    }

    @Override
    public TabrResultQuery<EquipmentUnit> getByTableDataIn(TabrIn tableDataIn) {
        CriteriaBuilder cbData = em.getCriteriaBuilder();
        CriteriaQuery<EquipmentUnit> cqData = cbData.createQuery(EquipmentUnit.class);
        Root<EquipmentUnit> rootData = cqData.from(EquipmentUnit.class);
        CriteriaQuery<EquipmentUnit> selectData = cqData.select(rootData);
        List<TabrSorter> sorterList = tableDataIn.getSorters();
        if (CollectionUtils.isNotEmpty(sorterList)) {
            TabrSorter sorter = sorterList.get(0);
            Expression<?> expression;
            switch (sorter.getField()) {
                case "name":
                    expression = rootData.join(EquipmentUnit_.equipment, JoinType.LEFT).get(Equipment_.name);
                    break;
                case "producer":
                    expression = rootData.join(EquipmentUnit_.equipment, JoinType.LEFT).join(Equipment_.producer, JoinType.LEFT).get(Producer_.name);
                    break;
                case "model":
                    expression = rootData.join(EquipmentUnit_.equipment, JoinType.LEFT).get(Equipment_.model);
                    break;
                case "serialNumber":
                    expression = rootData.get(EquipmentUnit_.serialNumber);
                    break;
                case "inventoryNumber":
                    expression = rootData.get(EquipmentUnit_.inventoryNumber);
                    break;
                default:
                    expression = rootData.get(EquipmentUnit_.id);
            }
            cqData.orderBy(ASC.equals(sorter.getDir()) ? cbData.asc(expression) : cbData.desc(expression));
        }
        TypedQuery<EquipmentUnit> tqData = em.createQuery(selectData);
        tqData.setFirstResult(tableDataIn.getStart());
        tqData.setMaxResults(tableDataIn.getSize());
        CriteriaBuilder cbCount = em.getCriteriaBuilder();
        CriteriaQuery<Long> cCount = cbCount.createQuery(Long.class);
        Root<EquipmentUnit> rootCount = cCount.from(EquipmentUnit.class);
        cCount.select(cbCount.countDistinct(rootCount));
        return new TabrResultQuery<>(tqData.getResultList(), em.createQuery(cCount).getSingleResult());
    }
}