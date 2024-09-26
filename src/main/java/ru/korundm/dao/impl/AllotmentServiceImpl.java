package ru.korundm.dao.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.constant.ObjAttr;
import ru.korundm.dao.AllotmentService;
import ru.korundm.entity.Allotment;
import ru.korundm.entity.Lot;
import ru.korundm.entity.LotGroup;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrResultQuery;
import ru.korundm.repository.AllotmentRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class AllotmentServiceImpl implements AllotmentService {

    @PersistenceContext
    private EntityManager em;

    private final AllotmentRepository allotmentRepository;

    public AllotmentServiceImpl(AllotmentRepository allotmentRepository) {
        this.allotmentRepository = allotmentRepository;
    }

    @Override
    public List<Allotment> getAll() {
        return allotmentRepository.findAll();
    }

    @Override
    public List<Allotment> getAllById(List<Long> idList) {
        return allotmentRepository.findAllById(idList);
    }

    @Override
    public Allotment save(Allotment object) {
        return allotmentRepository.save(object);
    }

    @Override
    public List<Allotment> saveAll(List<Allotment> objectList) {
        return allotmentRepository.saveAll(objectList);
    }

    @Override
    public Allotment read(long id) {
        return allotmentRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Allotment object) {
        allotmentRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        allotmentRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<Allotment> allotmentList) {
        allotmentRepository.deleteAll(allotmentList);
    }

    @Override
    public int getCountAllByLot(Lot lot) {
        return allotmentRepository.countAllByLot(lot);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Allotment> getAllForUnapprovedLaunch(Long productId) {
        String nativeQuery =
            "SELECT\n" +
            "  a.*\n" +
            "FROM\n" +
            "  allotments a\n" +
            "  JOIN\n" +
            "  lots l\n" +
            "  ON\n" +
            "  a.lot_id = l.id\n" +
            "  AND a.shipment_date IS NULL\n" +
            "  JOIN\n" +
            "  lot_groups lg\n" +
            "  ON\n" +
            "  l.lot_group_id = lg.id\n" +
            "  AND lg.kind = 1\n" +
            "  JOIN\n" +
            "  contract_sections cs\n" +
            "  ON\n" +
            "  lg.contract_section_id = cs.id\n" +
            "  AND cs.archive_date IS NULL\n" +
            "  JOIN\n" +
            "  contracts c\n" +
            "  ON\n" +
            "  cs.contract_id = c.id\n" +
            "  AND c.contract_type not in (256, 512)\n" +
            "WHERE\n" +
            "  lg.product_id = :productId\n" +
            "  AND (\n" +
            "    a.launch_product_id IS NULL\n" +
            "    OR\n" +
            "    a.launch_product_id IN (\n" +
            "      SELECT lp.id\n" +
            "      FROM launch_product lp, launch l\n" +
            "      WHERE\n" +
            "        lp.product_id = :productId\n" +
            "        AND lp.launch_id = l.id\n" +
            "        AND l.confirm_date IS NULL\n" +
            "    )\n" +
            "  )";
        Query query = em.createNativeQuery(nativeQuery, Allotment.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Allotment> getAllForApprovedLaunch(Long launchProductId) {
        String nativeQuery =
            "SELECT \n" +
            "  a.*\n" +
            "FROM\n" +
            "  allotments a,\n" +
            "  lots l,\n" +
            "  lot_groups g,\n" +
            "  contract_sections s,\n" +
            "  contract_sections s0,\n" +
            "  contracts c\n" +
            "WHERE \n" +
            "  a.launch_product_id = :launchProductId\n" +
            "  AND l.id = a.lot_id\n" +
            "  AND g.id = l.lot_group_id\n" +
            "  AND s.id = g.contract_section_id\n" +
            "  AND c.id = s.contract_id\n" +
            "  AND s.contract_id = s0.contract_id\n" +
            "  AND s0.section_number = 0\n" +
            "ORDER BY\n" +
            "  DATE_FORMAT(s0.section_date,'%y'),\n" +
            "  c.contract_number,\n" +
            "  s.section_number,\n" +
            "  l.delivery_date";
        Query query = em.createNativeQuery(nativeQuery, Allotment.class);
        query.setParameter("launchProductId", launchProductId);
        return query.getResultList();
    }

    @Override
    public List<Allotment> getAllByMatValueListLetterId(Long letterId) {
        return letterId == null ? Collections.emptyList() : allotmentRepository.findDistinctByMatValueListLetterId(letterId);
    }

    @NotNull
    @Override
    public TabrResultQuery<Allotment> findTableDataByContractSection(TabrIn tableInput, @Nullable Long sectionId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Allotment> c = cb.createQuery(Allotment.class);
        Root<LotGroup> root = c.from(LotGroup.class);
        Join<LotGroup, Lot> lot = root.join(ObjAttr.LOT_LIST);
        Join<Lot, Allotment> allotment = lot.join(ObjAttr.ALLOTMENT_LIST);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(ObjAttr.CONTRACT_SECTION), sectionId));
        predicates.add(cb.isNull(allotment.get(ObjAttr.SHIPMENT_PERMIT_DATE)));
        CriteriaQuery<Allotment> select = c.select(allotment).where(predicates.toArray(new Predicate[0]));
        var typedQuery = em.createQuery(select);
        typedQuery.setFirstResult(tableInput.getStart());
        typedQuery.setMaxResults(tableInput.getSize());
        CriteriaBuilder cbCount = em.getCriteriaBuilder();
        CriteriaQuery<Long> cCount = cbCount.createQuery(Long.class);
        Root<Allotment> rootCount = cCount.from(Allotment.class);
        cCount.select(cbCount.countDistinct(rootCount));
        return new TabrResultQuery<>(em.createQuery(select).getResultList(), em.createQuery(cCount).getSingleResult());
    }

    @NotNull
    @Override
    public List<Allotment> getAllByShipmentWaybillId(@Nullable Long id) {
        return id == null ? Collections.emptyList() : allotmentRepository.findDistinctAllByMatValueListShipmentWaybillId(id);
    }
}