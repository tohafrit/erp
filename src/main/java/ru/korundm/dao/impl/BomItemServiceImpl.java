package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.constant.ObjAttr;
import ru.korundm.dao.BomItemService;
import ru.korundm.entity.*;
import ru.korundm.form.search.ComponentListOccurrenceFilterForm;
import ru.korundm.repository.BomItemRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class BomItemServiceImpl implements BomItemService {

    @PersistenceContext
    private EntityManager em;

    private final BomItemRepository bomItemRepository;

    public BomItemServiceImpl(BomItemRepository bomItemRepository) {
        this.bomItemRepository = bomItemRepository;
    }

    @Override
    public List<BomItem> getAll() {
        return bomItemRepository.findAll();
    }

    @Override
    public List<BomItem> getAllById(List<Long> idList) {
        return bomItemRepository.findAllById(idList);
    }

    @Override
    public BomItem save(BomItem object) {
        return bomItemRepository.save(object);
    }

    @Override
    public List<BomItem> saveAll(List<BomItem> objectList) {
        return bomItemRepository.saveAll(objectList);
    }

    @Override
    public BomItem read(long id) {
        return bomItemRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(BomItem object) {
        bomItemRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        bomItemRepository.deleteById(id);
    }

    @Override
    public List<BomItem> getAllByBomId(Long bomId) {
        return bomItemRepository.findAllByBomId(bomId);
    }

    @Override
    public boolean existsByBomIdAndComponentId(Long bomId, Long componentId) {
        return bomItemRepository.existsByBomIdAndComponentId(bomId, componentId);
    }

    @Override
    public void deleteAllByBomId(Long bomId) {
        bomItemRepository.deleteAllByBomId(bomId);
    }

    @Override
    public boolean existsByBomId(Long bomId) {
        return bomItemRepository.existsByBomId(bomId);
    }

    @Override
    public List<BomItem> getAllByComponentId(Long componentId) {
        return bomItemRepository.findAllByComponentId(componentId);
    }

    @Override
    public void deleteAll(List<BomItem> objectList) {
        bomItemRepository.deleteAll(objectList);
    }

    @Override
    public List<BomItem> queryDataByFilterForm(ComponentListOccurrenceFilterForm form, List<Component> componentReplacementList) {
        if (!componentReplacementList.isEmpty()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<BomItem> c = cb.createQuery(BomItem.class);
            Root<BomItem> root = c.from(BomItem.class);

            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(root.get(BomItem_.component).in(componentReplacementList));
            Join<BomItem, Bom> bomJoin = root.join(BomItem_.bom);
            Join<Bom, Product> productJoin = bomJoin.join(ObjAttr.PRODUCT);
            if (!(form.isActive() && form.isArchive())) {
                if (form.isActive()) {
                    predicateList.add(cb.isNull(productJoin.get(ObjAttr.ARCHIVE_DATE)));
                }
                if (form.isArchive()) {
                    predicateList.add(cb.isNotNull(productJoin.get(ObjAttr.ARCHIVE_DATE)));
                }
            }
            CriteriaQuery<BomItem> select = c.select(root).where(predicateList.toArray(new Predicate[0]));

            return em.createQuery(select).getResultList();
        }
        return Collections.emptyList();
    }
}