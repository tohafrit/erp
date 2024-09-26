package ru.korundm.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.EquipmentUnitEventService;
import ru.korundm.entity.*;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrResultQuery;
import ru.korundm.helper.TabrSorter;
import ru.korundm.repository.EquipmentUnitEventRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@Transactional
public class EquipmentUnitEventServiceImpl implements EquipmentUnitEventService {

    @PersistenceContext
    private EntityManager entityManager;

    private final EquipmentUnitEventRepository equipmentUnitEventRepository;

    public EquipmentUnitEventServiceImpl(EquipmentUnitEventRepository equipmentUnitEventRepository) {
        this.equipmentUnitEventRepository = equipmentUnitEventRepository;
    }

    @Override
    public List<EquipmentUnitEvent> getAll() {
        return equipmentUnitEventRepository.findAll();
    }

    @Override
    public List<EquipmentUnitEvent> getAllById(List<Long> idList) {
        return equipmentUnitEventRepository.findAllById(idList);
    }

    @Override
    public EquipmentUnitEvent save(EquipmentUnitEvent object) {
        return equipmentUnitEventRepository.save(object);
    }

    @Override
    public List<EquipmentUnitEvent> saveAll(List<EquipmentUnitEvent> objectList) {
        return equipmentUnitEventRepository.saveAll(objectList);
    }

    @Override
    public EquipmentUnitEvent read(long id) {
        return equipmentUnitEventRepository.getOne(id);
    }

    @Override
    public void delete(EquipmentUnitEvent object) {
        equipmentUnitEventRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        equipmentUnitEventRepository.deleteById(id);
    }

    @Override
    public void deleteAllByEquipmentUnit(EquipmentUnit equipmentUnit) {
        equipmentUnitEventRepository.deleteAllByEquipmentUnit(equipmentUnit);
    }

    @Override
    public void deleteAllByEquipmentUnitIn(List<EquipmentUnit> equipmentUnitList) {
        equipmentUnitEventRepository.deleteAllByEquipmentUnitIn(equipmentUnitList);
    }

    @Override
    public void deleteAllByEquipment(Equipment equipment) {
        equipmentUnitEventRepository.deleteAllByEquipmentUnit_Equipment(equipment);
    }

    @Override
    public TabrResultQuery<EquipmentUnitEvent> getByTableDataIn(TabrIn tableDataIn) {
        CriteriaBuilder cbData = entityManager.getCriteriaBuilder();
        CriteriaQuery<EquipmentUnitEvent> cqData = cbData.createQuery(EquipmentUnitEvent.class);
        Root<EquipmentUnitEvent> rootData = cqData.from(EquipmentUnitEvent.class);
        CriteriaQuery<EquipmentUnitEvent> selectData = cqData.select(rootData);
        List<TabrSorter> sorterList = tableDataIn.getSorters();
        if (CollectionUtils.isNotEmpty(sorterList)) {
            TabrSorter sorter = sorterList.get(0);
            Expression<?> expression;
            switch (sorter.getField()) {
                case "name":
                    expression = rootData.get(EquipmentUnitEvent_.name);
                    break;
                case "eventType":
                    expression = rootData.join(EquipmentUnitEvent_.equipmentUnitEventType, JoinType.LEFT).get(EquipmentUnitEventType_.name);
                    break;
                case "equipmentName":
                    expression = rootData.join(EquipmentUnitEvent_.equipmentUnit, JoinType.LEFT).join(EquipmentUnit_.equipment, JoinType.LEFT).get(Equipment_.name);
                    break;
                case "serialNumber":
                    expression = rootData.join(EquipmentUnitEvent_.equipmentUnit, JoinType.LEFT).get(EquipmentUnit_.serialNumber);
                    break;
                case "inventoryNumber":
                    expression = rootData.join(EquipmentUnitEvent_.equipmentUnit, JoinType.LEFT).get(EquipmentUnit_.inventoryNumber);
                    break;
                case "eventOn":
                    expression = rootData.get(EquipmentUnitEvent_.eventOn);
                    break;
                case "commentary":
                    expression = rootData.get(EquipmentUnitEvent_.commentary);
                    break;
                default:
                    expression = rootData.get(EquipmentUnitEvent_.id);
            }
            cqData.orderBy(ASC.equals(sorter.getDir()) ? cbData.asc(expression) : cbData.desc(expression));
        }
        TypedQuery<EquipmentUnitEvent> tqData = entityManager.createQuery(selectData);
        tqData.setFirstResult(tableDataIn.getStart());
        tqData.setMaxResults(tableDataIn.getSize());
        CriteriaBuilder cbCount = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cCount = cbCount.createQuery(Long.class);
        Root<EquipmentUnitEvent> rootCount = cCount.from(EquipmentUnitEvent.class);
        cCount.select(cbCount.countDistinct(rootCount));
        return new TabrResultQuery<>(tqData.getResultList(), entityManager.createQuery(cCount).getSingleResult());
    }

    @Override
    public long getCount() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<EquipmentUnitEvent> root = criteriaQuery.from(EquipmentUnitEvent.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public int getPageById(long id) {
        String nativeQuery =
            "SELECT\n" +
            "     IFNULL (\n" +
            "         (SELECT r.page FROM\n" +
            "             (SELECT\n" +
            "                e.id,\n" +
            "                ROW_NUMBER() OVER(ORDER BY e.event_on DESC),\n" +
            "                CASE\n" +
            "                    WHEN ROW_NUMBER() OVER(ORDER BY e.event_on DESC)%50 = 0\n" +
            "                    THEN FLOOR(ROW_NUMBER() OVER(ORDER BY e.event_on DESC)/50)\n" +
            "                    ELSE FLOOR(ROW_NUMBER() OVER(ORDER BY e.event_on DESC)/50) + 1\n" +
            "                END page\n" +
            "            FROM\n" +
            "                equipment_unit_events e\n" +
            "            ) r\n" +
            "        WHERE r.id = :id), 1\n" +
            "    )\n" +
            "FROM\n" +
            "    dual";
        Query query = entityManager.createNativeQuery(nativeQuery);
        query.setParameter("id", id);
        return ((BigDecimal) query.getSingleResult()).intValue();
    }
}