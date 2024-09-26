package ru.korundm.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.constant.ObjAttr;
import ru.korundm.dao.BomItemReplacementService;
import ru.korundm.dao.BomItemService;
import ru.korundm.dao.ComponentService;
import ru.korundm.entity.*;
import ru.korundm.enumeration.BomItemReplacementStatus;
import ru.korundm.enumeration.ComponentLifecycle;
import ru.korundm.enumeration.ComponentType;
import ru.korundm.form.search.ComponentListFilterForm;
import ru.korundm.form.search.ComponentListReplaceFilterForm;
import ru.korundm.form.search.ProductSpecComponentFilterForm;
import ru.korundm.helper.DynamicObject;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrResultQuery;
import ru.korundm.helper.TabrSorter;
import ru.korundm.repository.ComponentRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@Transactional
public class ComponentServiceImpl implements ComponentService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ComponentRepository componentRepository;
    private final BomItemService bomItemService;
    private final BomItemReplacementService bomItemReplacementService;

    public ComponentServiceImpl(
        ComponentRepository componentRepository,
        BomItemService bomItemService,
        BomItemReplacementService bomItemReplacementService
    ) {
        this.componentRepository = componentRepository;
        this.bomItemService = bomItemService;
        this.bomItemReplacementService = bomItemReplacementService;
    }

    @Override
    public List<Component> getAll() {
        return componentRepository.findAll();
    }

    @Override
    public List<Component> getAllById(List<Long> idList) {
        return componentRepository.findAllById(idList);
    }

    @Override
    public Component save(Component object) {
        return componentRepository.save(object);
    }

    @Override
    public List<Component> saveAll(List<Component> objectList) {
        return componentRepository.saveAll(objectList);
    }

    @Override
    public Component read(long id) {
        return componentRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Component object) {
        componentRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        componentRepository.deleteById(id);
    }

    @Override
    public TabrResultQuery<Component> queryDataByFilterForm(TabrIn input, ProductSpecComponentFilterForm form) {
        CriteriaBuilder cbData = entityManager.getCriteriaBuilder();
        CriteriaQuery<Component> cqData = cbData.createQuery(Component.class);
        Root<Component> rootData = cqData.from(Component.class);
        CriteriaQuery<Component> selectData = cqData.select(rootData);
        selectData.where(predicateListByFilterForm(form, rootData, cbData).toArray(new Predicate[0]));
        List<TabrSorter> sorterList = input.getSorters();
        if (CollectionUtils.isNotEmpty(sorterList)) {
            TabrSorter sorter = sorterList.get(0);
            Expression<?> expression;
            switch(sorter.getField()) {
                case Component_.POSITION:
                    expression = rootData.get(Component_.position);
                    break;
                case Component_.NAME:
                    expression = rootData.get(Component_.name);
                    break;
                case Component_.PRODUCER:
                    expression = rootData.join(Component_.producer, JoinType.LEFT).get(CompanyM.NAME);
                    break;
                case Component_.CATEGORY:
                    expression = rootData.join(Component_.category, JoinType.LEFT).get(ComponentCategory_.name);
                    break;
                case Component_.DESCRIPTION:
                    expression = rootData.get(Component_.description);
                    break;
                case Component_.OKEI:
                    expression = rootData.join(Component_.okei, JoinType.LEFT).get(Okei_.symbolNational);
                    break;
                case Component_.PURPOSE:
                    expression = rootData.join(Component_.purpose, JoinType.LEFT).get(ComponentPurpose_.name);
                    break;
                case Component_.INSTALLATION:
                    expression = rootData.join(Component_.installation, JoinType.LEFT).get(ComponentInstallationType_.name);
                    break;
                case Component_.PRICE:
                    expression = rootData.get(Component_.price);
                    break;
                default:
                    expression = rootData.get(Component_.id);
            }
            cqData.orderBy(ASC.equals(sorter.getDir()) ? cbData.asc(expression) : cbData.desc(expression));
        }
        TypedQuery<Component> tqData = entityManager.createQuery(selectData);
        tqData.setFirstResult(input.getStart());
        tqData.setMaxResults(input.getSize());
        //
        CriteriaBuilder cbCount = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cCount = cbCount.createQuery(Long.class);
        Root<Component> rootCount = cCount.from(Component.class);
        cCount.select(cbCount.count(rootCount)).where(predicateListByFilterForm(form, rootCount, cbCount).toArray(new Predicate[0]));
        return new TabrResultQuery<>(tqData.getResultList(), entityManager.createQuery(cCount).getSingleResult());
    }

    private List<Predicate> predicateListByFilterForm(ProductSpecComponentFilterForm form, Root<Component> root, CriteriaBuilder cb) {
        List<Predicate> predicateList = new ArrayList<>();
        if (form.isNewComponent()) {
            predicateList.add(cb.and(
                cb.equal(root.get(Component_.approved), Boolean.FALSE),
                cb.isNull(root.get(Component_.position))
            ));
        }
        predicateList.add(cb.or(
            cb.equal(root.get(Component_.processed), Boolean.FALSE),
            cb.and(
                cb.equal(root.get(Component_.processed), Boolean.TRUE),
                root.get(Component_.substituteComponent).isNull()
            ),
            root.get(Component_.substituteComponent).isNotNull()
        ));
        if (!form.isShowReplaceable()) {
            predicateList.add(root.get(Component_.substituteComponent).isNull());
        }
        String name = form.getName();
        if (StringUtils.isNotBlank(name)) {
            predicateList.add(cb.like(root.get(Component_.name), "%" + name + "%"));
        }
        Integer position = form.getPosition();
        if (position != null) {
            predicateList.add(cb.like(root.get(Component_.position).as(String.class), "%" + position + "%"));
        }
        List<Long> categoryIdList = form.getCategoryIdList();
        if (CollectionUtils.isNotEmpty(categoryIdList)) {
            predicateList.add(root.get(Component_.category).get(ComponentCategory_.id).in(categoryIdList));
        }
        List<Long> producerIdList = form.getProducerIdList();
        if (CollectionUtils.isNotEmpty(producerIdList)) {
            predicateList.add(root.get(Component_.producer).get(CompanyM.ID).in(producerIdList));
        }
        String description = form.getDescription();
        if (StringUtils.isNotBlank(description)) {
            predicateList.add(cb.like(root.get(Component_.description), "%" + description + "%"));
        }
        return predicateList;
    }

    @Override
    public <T> TabrResultQuery<T> queryDataByFilterForm(TabrIn input, ComponentListReplaceFilterForm form, Class<T> cl) {
        CriteriaBuilder cbData = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cqData = cbData.createQuery(cl);
        Root<Component> rootData = cqData.from(Component.class);
        List<TabrSorter> sorterList = input.getSorters();
        if (CollectionUtils.isNotEmpty(sorterList)) {
            TabrSorter sorter = sorterList.get(0);
            Expression<?> expression;
            switch(sorter.getField()) {
                case Component_.POSITION:
                    expression = rootData.get(Component_.position);
                    break;
                case Component_.NAME:
                    expression = rootData.get(Component_.name);
                    break;
                case Component_.PRODUCER:
                    expression = rootData.join(Component_.producer, JoinType.LEFT).get(CompanyM.NAME);
                    break;
                case Component_.CATEGORY:
                    expression = rootData.join(Component_.category, JoinType.LEFT).get(ComponentCategory_.name);
                    break;
                case Component_.DESCRIPTION:
                    expression = rootData.get(Component_.description);
                    break;
                case Component_.OKEI:
                    expression = rootData.join(Component_.okei, JoinType.LEFT).get(Okei_.symbolNational);
                    break;
                case Component_.PURPOSE:
                    expression = rootData.join(Component_.purpose, JoinType.LEFT).get(ComponentPurpose_.name);
                    break;
                case Component_.INSTALLATION:
                    expression = rootData.join(Component_.installation, JoinType.LEFT).get(ComponentInstallationType_.name);
                    break;
                case Component_.PRICE:
                    expression = rootData.get(Component_.price);
                    break;
                case ObjAttr.SUBSTITUTE_COMPONENT:
                    expression = rootData.join(Component_.substituteComponent, JoinType.LEFT).get(Component_.id);
                    break;
                default:
                    expression = rootData.get(Component_.id);
            }
            cqData.orderBy(ASC.equals(sorter.getDir()) ? cbData.asc(expression) : cbData.desc(expression));
        }
        CriteriaQuery<T> selectData = cqData.multiselect(
            rootData.get(Component_.id).alias(ObjAttr.ID),
            rootData.get(Component_.name).alias(ObjAttr.NAME),
            rootData.join(Component_.producer, JoinType.LEFT).get(ObjAttr.NAME).alias(ObjAttr.PRODUCER),
            rootData.get(Component_.position).alias(ObjAttr.POSITION_NUM),
            rootData.join(Component_.category, JoinType.LEFT).get(ComponentCategory_.name).alias(ObjAttr.CATEGORY),
            rootData.get(Component_.description).alias(ObjAttr.DESCRIPTION),
            rootData.get(Component_.processed).alias(ObjAttr.PROCESSED),
            rootData.join(Component_.substituteComponent, JoinType.LEFT).get(Component_.id).alias(ObjAttr.SUB_COMPONENT_ID),
            rootData.join(Component_.substituteComponent, JoinType.LEFT).get(Component_.name).alias(ObjAttr.SUB_COMPONENT_NAME),
            rootData.join(Component_.substituteComponent, JoinType.LEFT).get(Component_.position).alias(ObjAttr.SUB_COMPONENT_POSITION)
        );
        selectData.where(predicateListByFilterForm(form, rootData, cbData).toArray(new Predicate[0]));
        TypedQuery<T> tqData = entityManager.createQuery(selectData);
        tqData.setFirstResult(input.getStart());
        tqData.setMaxResults(input.getSize());
        CriteriaBuilder cbCount = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cCount = cbCount.createQuery(Long.class);
        Root<Component> rootCount = cCount.from(Component.class);
        cCount.select(cbCount.count(rootCount)).where(predicateListByFilterForm(form, rootCount, cbCount).toArray(new Predicate[0]));
        return new TabrResultQuery<>(tqData.getResultList(), entityManager.createQuery(cCount).getSingleResult());
    }

    private List<Predicate> predicateListByFilterForm(ComponentListReplaceFilterForm form, Root<Component> root, CriteriaBuilder cb) {
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(cb.equal(root.get(Component_.approved), true));
        predicateList.add(cb.or(
            cb.equal(root.get(Component_.processed), Boolean.FALSE),
            cb.and(
                cb.equal(root.get(Component_.processed), Boolean.TRUE),
                root.get(Component_.substituteComponent).isNull()
            ),
            root.get(Component_.substituteComponent).isNotNull()
        ));
        if (!form.isShowReplaceable()) {
            predicateList.add(root.get(Component_.substituteComponent).isNull());
        }
        String name = form.getName();
        if (StringUtils.isNotBlank(name)) {
            predicateList.add(cb.like(root.get(Component_.name), "%" + name + "%"));
        }
        Integer position = form.getPosition();
        if (position != null) {
            predicateList.add(cb.like(root.get(Component_.position).as(String.class), "%" + position + "%"));
        }
        List<Long> categoryIdList = form.getCategoryIdList();
        if (CollectionUtils.isNotEmpty(categoryIdList)) {
            predicateList.add(root.get(Component_.category).get(ComponentCategory_.id).in(categoryIdList));
        }
        List<Long> producerIdList = form.getProducerIdList();
        if (CollectionUtils.isNotEmpty(producerIdList)) {
            predicateList.add(root.get(Component_.producer).get(CompanyM.ID).in(producerIdList));
        }
        String description = form.getDescription();
        if (StringUtils.isNotBlank(description)) {
            predicateList.add(cb.like(root.get(Component_.description), "%" + description + "%"));
        }
        return predicateList;
    }

    @Override
    public TabrResultQuery<Component> queryDataByFilterForm(TabrIn input, ComponentListFilterForm form, Long selectedId) {
        CriteriaBuilder cbData = entityManager.getCriteriaBuilder();
        CriteriaQuery<Component> cqData = cbData.createQuery(Component.class);
        Root<Component> rootData = cqData.from(Component.class);
        CriteriaQuery<Component> selectData = cqData.select(rootData);
        selectData.where(predicateListByFilterForm(form, rootData, cbData).toArray(new Predicate[0]));
        List<TabrSorter> sorterList = input.getSorters();
        if (CollectionUtils.isNotEmpty(sorterList)) {
            List<Order> orderList = new ArrayList<>();
            if (selectedId == null) {
                TabrSorter sorter = sorterList.get(0);
                boolean isAsc = ASC.equals(sorter.getDir());
                Path<?> category = rootData.join(Component_.category, JoinType.LEFT).get(ComponentCategory_.name);
                orderList.add(isAsc ? cbData.asc(category) : cbData.desc(category));
                switch(sorter.getField()) {
                    case Component_.POSITION:
                        Path<?> position = rootData.get(Component_.position);
                        orderList.add(isAsc ? cbData.asc(position) : cbData.desc(position));
                        break;
                    case Component_.NAME:
                        Path<?> componentName = rootData.get(Component_.name);
                        orderList.add(isAsc ? cbData.asc(componentName) : cbData.desc(componentName));
                        break;
                    case Component_.PRODUCER:
                        Path<?> companyName = rootData.join(Component_.producer, JoinType.LEFT).get(CompanyM.NAME);
                        orderList.add(isAsc ? cbData.asc(companyName) : cbData.desc(companyName));
                        break;
                    case Component_.DESCRIPTION:
                        Path<?> description = rootData.get(Component_.description);
                        orderList.add(isAsc ? cbData.asc(description) : cbData.desc(description));
                        break;
                    case Component_.OKEI:
                        Path<?> symbolNational = rootData.join(Component_.okei, JoinType.LEFT).get(Okei_.symbolNational);
                        orderList.add(isAsc ? cbData.asc(symbolNational) : cbData.desc(symbolNational));
                        break;
                    case Component_.PURPOSE:
                        Path<?> purposeName = rootData.join(Component_.purpose, JoinType.LEFT).get(ComponentPurpose_.name);
                        orderList.add(isAsc ? cbData.asc(purposeName) : cbData.desc(purposeName));
                        break;
                    case Component_.INSTALLATION:
                        Path<?> installation = rootData.join(Component_.installation, JoinType.LEFT).get(ComponentInstallationType_.name);
                        orderList.add(isAsc ? cbData.asc(installation) : cbData.desc(installation));
                        break;
                    case Component_.KIND:
                        Path<?> kind = rootData.join(Component_.kind, JoinType.LEFT).get(ComponentKind_.name);
                        orderList.add(isAsc ? cbData.asc(kind) : cbData.desc(kind));
                        break;
                    case Component_.PURCHASE_COMPONENT:
                        Join<Component, Component> purchaseJoin = rootData.join(Component_.purchaseComponent, JoinType.LEFT);
                        Expression<?> purchaseComponent = cbData.concat(
                            purchaseJoin.get(Component_.position).as(String.class),
                            purchaseJoin.get(Component_.name)
                        );
                        orderList.add(isAsc ? cbData.asc(purchaseComponent) : cbData.desc(purchaseComponent));
                        break;
                    case Component_.SUBSTITUTE_COMPONENT:
                        Join<Component, Component> substituteJoin = rootData.join(Component_.substituteComponent, JoinType.LEFT);
                        Expression<?> substituteComponent = cbData.concat(
                            substituteJoin.get(Component_.position).as(String.class),
                            substituteJoin.get(Component_.name)
                        );
                        orderList.add(isAsc ? cbData.asc(substituteComponent) : cbData.desc(substituteComponent));
                        break;
                    case Component_.PRICE:
                        Path<?> price = rootData.get(Component_.price);
                        orderList.add(isAsc ? cbData.asc(price) : cbData.desc(price));
                        break;
                    case Component_.DELIVERY_TIME:
                        Path<?> deliveryTime = rootData.get(Component_.deliveryTime);
                        orderList.add(isAsc ? cbData.asc(deliveryTime) : cbData.desc(deliveryTime));
                        break;
                    default:
                        Path<?> id = rootData.get(Component_.id);
                        orderList.add(isAsc ? cbData.asc(id) : cbData.desc(id));
                }
                cqData.orderBy(orderList);
            } else {
                var comp = read(selectedId);
                orderList.add(cbData.desc(cbData.selectCase().when(cbData.equal(rootData.get(Component_.category), comp.getCategory().getId()), 1).otherwise(0)));
                orderList.add(cbData.desc(cbData.selectCase().when(cbData.equal(rootData.get(ObjAttr.ID), selectedId), 1).otherwise(0)));
                cqData.orderBy(orderList);
            }
        }
        TypedQuery<Component> tqData = entityManager.createQuery(selectData);
        tqData.setFirstResult(input.getStart());
        tqData.setMaxResults(input.getSize());
        //
        CriteriaBuilder cbCount = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cCount = cbCount.createQuery(Long.class);
        Root<Component> rootCount = cCount.from(Component.class);
        cCount.select(cbCount.count(rootCount)).where(predicateListByFilterForm(form, rootCount, cbCount).toArray(new Predicate[0]));
        return new TabrResultQuery<>(tqData.getResultList(), entityManager.createQuery(cCount).getSingleResult());
    }

    private List<Predicate> predicateListByFilterForm(ComponentListFilterForm form, Root<Component> root, CriteriaBuilder cb) {
        List<Predicate> predicateList = new ArrayList<>();
        String name = form.getName();
        if (StringUtils.isNotBlank(name)) {
            predicateList.add(cb.like(root.get(Component_.name), "%" + name + "%"));
        }
        Integer position = form.getPosition();
        if (position != null) {
            predicateList.add(cb.like(root.get(Component_.position).as(String.class), "%" + position + "%"));
        }
        List<Long> categoryIdList = form.getCategoryIdList();
        if (CollectionUtils.isNotEmpty(categoryIdList)) {
            predicateList.add(root.get(Component_.category).get(ComponentCategory_.id).in(categoryIdList));
        }
        List<Long> producerIdList = form.getProducerIdList();
        if (CollectionUtils.isNotEmpty(producerIdList)) {
            predicateList.add(root.get(Component_.producer).get(ObjAttr.ID).in(producerIdList));
        }
        String description = form.getDescription();
        if (StringUtils.isNotBlank(description)) {
            predicateList.add(cb.like(root.get(Component_.description), "%" + description + "%"));
        }
        // Жизненный цикл
        Long lifecycleId = form.getLifecycleId();
        if (Objects.equals(ComponentLifecycle.NEW.getId(), lifecycleId)) {
            predicateList.add(cb.and(
                cb.equal(root.get(Component_.approved), Boolean.FALSE),
                cb.isNull(root.get(Component_.position))
            ));
            // Запуск
            Long launchId = form.getLaunchId();
            if (launchId != null) {
                Join<Product, LaunchProduct> launchProductJoin = root.join(Component_.bomItemList)
                    .join(BomItem_.bom).join(ObjAttr.PRODUCT)
                    .join(ObjAttr.LAUNCH_PRODUCT_LIST);
                predicateList.add(cb.equal(launchProductJoin.get(ObjAttr.LAUNCH), launchId));
            }
            String product = form.getProduct();
            if (StringUtils.isNotBlank(product)) {
                Join<Bom, Product> productJoin = root.join(Component_.bomItemList).join(BomItem_.bom).join(ObjAttr.PRODUCT);
                predicateList.add(cb.like(productJoin.get(ObjAttr.CONDITIONAL_NAME), "%" + product + "%"));
            }
        }
        if (Objects.equals(ComponentLifecycle.DESIGN.getId(), lifecycleId)) {
            predicateList.add(cb.equal(root.get(Component_.approved), Boolean.TRUE));
        }
        if (Objects.equals(ComponentLifecycle.INDUSTRIAL.getId(), lifecycleId)) {
            predicateList.add(root.get(Component_.position).isNotNull());
        }
        return predicateList;
    }

    @Override
    public Component getByPosition(Integer position) {
        return componentRepository.findFirstByPosition(position);
    }

    @Override
    public TabrResultQuery<Component> findTableCompReplacementData(TabrIn input, DynamicObject form) {
        CriteriaBuilder cbData = entityManager.getCriteriaBuilder();
        CriteriaQuery<Component> cqData = cbData.createQuery(Component.class);
        Root<Component> rootData = cqData.from(Component.class);
        CriteriaQuery<Component> selectData = cqData.select(rootData);
        selectData.where(predicateListCompReplacement(form, rootData, cbData).toArray(new Predicate[0]));
        List<TabrSorter> sorterList = input.getSorters();
        if (CollectionUtils.isNotEmpty(sorterList)) {
            TabrSorter sorter = sorterList.get(0);
            Expression<?> expression;
            switch(sorter.getField()) {
                case Component_.POSITION:
                    expression = rootData.get(Component_.position);
                    break;
                case Component_.NAME:
                    expression = rootData.get(Component_.name);
                    break;
                case Component_.PRODUCER:
                    expression = rootData.join(Component_.producer, JoinType.LEFT).get(CompanyM.NAME);
                    break;
                case Component_.CATEGORY:
                    expression = rootData.join(Component_.category, JoinType.LEFT).get(ComponentCategory_.name);
                    break;
                case Component_.DESCRIPTION:
                    expression = rootData.get(Component_.description);
                    break;
                case Component_.OKEI:
                    expression = rootData.join(Component_.okei, JoinType.LEFT).get(Okei_.symbolNational);
                    break;
                case Component_.PURPOSE:
                    expression = rootData.join(Component_.purpose, JoinType.LEFT).get(ComponentPurpose_.name);
                    break;
                case Component_.INSTALLATION:
                    expression = rootData.join(Component_.installation, JoinType.LEFT).get(ComponentInstallationType_.name);
                    break;
                case Component_.PRICE:
                    expression = rootData.get(Component_.price);
                    break;
                case Component_.SUBSTITUTE_COMPONENT:
                    expression = rootData.join(Component_.substituteComponent, JoinType.LEFT).get(Component_.position);
                    break;
                default:
                    expression = rootData.get(Component_.id);
            }
            cqData.orderBy(ASC.equals(sorter.getDir()) ? cbData.asc(expression) : cbData.desc(expression));
        }
        TypedQuery<Component> tqData = entityManager.createQuery(selectData);
        tqData.setFirstResult(input.getStart());
        tqData.setMaxResults(input.getSize());

        CriteriaBuilder cbCount = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cCount = cbCount.createQuery(Long.class);
        Root<Component> rootCount = cCount.from(Component.class);
        cCount.select(cbCount.count(rootCount)).where(predicateListCompReplacement(form, rootCount, cbCount).toArray(new Predicate[0]));
        return new TabrResultQuery<>(tqData.getResultList(), entityManager.createQuery(cCount).getSingleResult());
    }

    private List<Predicate> predicateListCompReplacement(DynamicObject form, Root<Component> root, CriteriaBuilder cb) {
        List<Predicate> predicateList = new ArrayList<>();
        List<Long> includeComponentTypeList = List.of(ComponentType.COMPONENT.getId(), ComponentType.NEW_COMPONENT.getId());
        predicateList.add(root.get(Component_.type).in(includeComponentTypeList));
        predicateList.add(cb.or(
            cb.equal(root.get(Component_.processed), Boolean.FALSE),
            cb.and(
                cb.equal(root.get(Component_.processed), Boolean.TRUE),
                root.get(Component_.substituteComponent).isNull()
            ),
            root.get(Component_.substituteComponent).isNotNull()
        ));
        var showReplaceable = form.boolNotNull(ObjAttr.SHOW_REPLACEABLE, false);
        if (!showReplaceable) predicateList.add(root.get(Component_.substituteComponent).isNull());

        var name = form.stringNotNull(ObjAttr.NAME, "");
        if (!name.isBlank()) predicateList.add(cb.like(root.get(Component_.name), "%" + name + "%"));

        var position = form.intValue(ObjAttr.POSITION);
        if (position != null) predicateList.add(cb.equal(root.get(Component_.position), position));

        var categoryIdList = form.listLong(ObjAttr.CATEGORY_ID_LIST);
        if (!categoryIdList.isEmpty()) predicateList.add(root.get(Component_.category).in(categoryIdList));

        var producerIdList = form.listLong(ObjAttr.PRODUCER_ID_LIST);
        if (!producerIdList.isEmpty()) predicateList.add(root.get(Component_.producer).in(producerIdList));

        var description = form.stringNotNull(ObjAttr.DESCRIPTION, "");
        if (!description.isBlank()) predicateList.add(cb.like(root.get(Component_.description), "%" + description + "%"));
        return predicateList;
    }

    @Override
    public TabrResultQuery<Component> findTableSpecCompReplacementData(TabrIn input, DynamicObject form) {
        CriteriaBuilder cbData = entityManager.getCriteriaBuilder();
        CriteriaQuery<Component> cqData = cbData.createQuery(Component.class);
        Root<Component> rootData = cqData.from(Component.class);
        CriteriaQuery<Component> selectData = cqData.select(rootData);
        selectData.where(predicateListSpecCompReplacement(form, rootData, cbData).toArray(new Predicate[0]));
        List<TabrSorter> sorterList = input.getSorters();
        if (CollectionUtils.isNotEmpty(sorterList)) {
            TabrSorter sorter = sorterList.get(0);
            Expression<?> expression;
            switch(sorter.getField()) {
                case Component_.POSITION:
                    expression = rootData.get(Component_.position);
                    break;
                case Component_.NAME:
                    expression = rootData.get(Component_.name);
                    break;
                case Component_.PRODUCER:
                    expression = rootData.join(Component_.producer, JoinType.LEFT).get(CompanyM.NAME);
                    break;
                case Component_.CATEGORY:
                    expression = rootData.join(Component_.category, JoinType.LEFT).get(ComponentCategory_.name);
                    break;
                case Component_.DESCRIPTION:
                    expression = rootData.get(Component_.description);
                    break;
                case Component_.OKEI:
                    expression = rootData.join(Component_.okei, JoinType.LEFT).get(Okei_.symbolNational);
                    break;
                case Component_.PURPOSE:
                    expression = rootData.join(Component_.purpose, JoinType.LEFT).get(ComponentPurpose_.name);
                    break;
                case Component_.INSTALLATION:
                    expression = rootData.join(Component_.installation, JoinType.LEFT).get(ComponentInstallationType_.name);
                    break;
                case Component_.PRICE:
                    expression = rootData.get(Component_.price);
                    break;
                default:
                    expression = rootData.get(Component_.id);
            }
            cqData.orderBy(ASC.equals(sorter.getDir()) ? cbData.asc(expression) : cbData.desc(expression));
        }
        TypedQuery<Component> tqData = entityManager.createQuery(selectData);
        tqData.setFirstResult(input.getStart());
        tqData.setMaxResults(input.getSize());

        CriteriaBuilder cbCount = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cCount = cbCount.createQuery(Long.class);
        Root<Component> rootCount = cCount.from(Component.class);
        cCount.select(cbCount.count(rootCount)).where(predicateListSpecCompReplacement(form, rootCount, cbCount).toArray(new Predicate[0]));
        return new TabrResultQuery<>(tqData.getResultList(), entityManager.createQuery(cCount).getSingleResult());
    }

    private List<Predicate> predicateListSpecCompReplacement(DynamicObject form, Root<Component> root, CriteriaBuilder cb) {
        List<Predicate> predicateList = new ArrayList<>();
        var showDesign = form.boolNotNull(ObjAttr.SHOW_DESIGN, false);
        if (showDesign) cb.equal(root.get(Component_.approved), true);
        predicateList.add(cb.or(
            cb.equal(root.get(Component_.processed), Boolean.FALSE),
            cb.and(
                cb.equal(root.get(Component_.processed), Boolean.TRUE),
                root.get(Component_.substituteComponent).isNull()
            ),
            root.get(Component_.substituteComponent).isNotNull()
        ));

        var showReplaceable = form.boolNotNull(ObjAttr.SHOW_REPLACEABLE, false);
        if (!showReplaceable) predicateList.add(root.get(Component_.substituteComponent).isNull());

        var name = form.stringNotNull(ObjAttr.NAME, "");
        if (!name.isBlank()) predicateList.add(cb.like(root.get(Component_.name), "%" + name + "%"));

        var position = form.intValue(ObjAttr.POSITION);
        if (position != null) predicateList.add(cb.equal(root.get(Component_.position), position));

        var categoryIdList = form.listLong(ObjAttr.CATEGORY_ID_LIST);
        if (!categoryIdList.isEmpty()) predicateList.add(root.get(Component_.category).in(categoryIdList));

        var producerIdList = form.listLong(ObjAttr.PRODUCER_ID_LIST);
        if (!producerIdList.isEmpty()) predicateList.add(root.get(Component_.producer).in(producerIdList));

        var description = form.stringNotNull(ObjAttr.DESCRIPTION, "");
        if (!description.isBlank()) predicateList.add(cb.like(root.get(Component_.description), "%" + description + "%"));

        return predicateList;
    }

    @Override
    public void setSubstituteComponent(Long oldId, Long newId) {
        // Выполняем удаление заменяемого компонента из bom_items таблицы
        // если в пределах одного бома есть заменяемый компонент и компонент заместителя
        // Для остальных bom_item выставляем заместителя
        {
            for (var bomItem : bomItemService.getAllByComponentId(oldId)) {
                boolean noChanged = true;
                for (var comparedBomItem : bomItemService.getAllByBomId(bomItem.getBom().getId())) {
                    // Может быть только единственное совпадение
                    if (Objects.equals(comparedBomItem.getComponent().getId(), newId)) {
                        bomItemService.delete(bomItem);
                        noChanged = false;
                        break;
                    }
                }
                if (noChanged) {
                    Component component = new Component();
                    component.setId(newId);
                    bomItem.setComponent(component);
                    bomItemService.save(bomItem);
                }
            }
        }
        // Конструкция далее разбираетя с заменами
        // Если в пределах 1 бом итема находится замещаемый и заместитель
        // то перебрасываем флаг закупки на заместитель, а замещающий удаляем
        {
            for (var replacement : bomItemReplacementService.getAllByComponentId(oldId)) {
                boolean noChanged = true;
                for (var comparedReplacement : bomItemReplacementService.getAllByBomItemId(replacement.getBomItem().getId())) {
                    // Может быть только единственное совпадение
                    if (Objects.equals(comparedReplacement.getComponent().getId(), newId)) {
                        comparedReplacement.setPurchase(replacement.isPurchase());
                        bomItemReplacementService.delete(replacement);
                        bomItemReplacementService.save(comparedReplacement);
                        noChanged = false;
                        break;
                    }
                }
                if (noChanged) {
                    Component component = new Component();
                    component.setId(newId);
                    replacement.setComponent(component);
                    bomItemReplacementService.save(replacement);
                }
            }
        }
        // Выставляем поле заместителя
        Component oldComponent = read(oldId);
        Component newComponent = read(newId);
        oldComponent.setSubstituteComponent(newComponent);
        save(oldComponent);
    }

    @Override
    public int setAnalogReplacement(Long oldId, Long newId, LocalDate replacementDate) {
        String strQuery =
            "SELECT DISTINCT\n" +
            "  bi.*\n" +
            "FROM (\n" +
            "  SELECT\n" +
            "    b.id b_id,\n" +
            "    bi.id bi_id,\n" +
            "    b.create_date,\n" +
            "    RANK() OVER (partition by b.product_id ORDER BY l.year DESC, l.number DESC) r\n" +
            "  FROM\n" +
            "    boms b\n" +
            "    JOIN\n" +
            "    bom_items bi\n" +
            "    ON\n" +
            "    b.id = bi.bom_id\n" +
            "    AND bi.component_id = :oldId\n" +
            "\n" +
            "    LEFT JOIN\n" +
            "    bom_attributes ba\n" +
            "    ON\n" +
            "    ba.bom_id = b.id\n" +
            "    AND ba.accept_date IS NOT NULL\n" +
            "\n" +
            "    LEFT JOIN\n" +
            "    launch l\n" +
            "    ON\n" +
            "    l.id = ba.launch_id\n" +
            "  ) res\n" +
            "  JOIN\n" +
            "  bom_items bi\n" +
            "  ON\n" +
            "  res.bi_id = bi.id\n" +
            "WHERE\n" +
            "  (res.r = 1 OR res.create_date >= :replacementDate)\n" +
            "  AND res.b_id NOT IN (\n" +           // бом не должен содержать добавляемый компонент замены
            "    SELECT DISTINCT sub_b.id\n" +
            "    FROM\n" +
            "      boms sub_b\n" +
            "      JOIN\n" +
            "      bom_items sub_bi\n" +
            "      ON\n" +
            "      sub_b.id = sub_bi.bom_id\n" +
            "    WHERE \n" +
            "      sub_bi.component_id = :newId\n" +
            "  )\n" +
            "  AND res.bi_id NOT IN (\n" +             // замены для позиции в спецификации не должны содержать добавляемой замены
            "    SELECT DISTINCT bir.bom_item_id\n" +
            "    FROM bom_item_replacements bir\n" +
            "    WHERE bir.component_id = :newId\n" +
            "  )";
        Query query = entityManager.createNativeQuery(strQuery, BomItem.class);
        query.setParameter("oldId", oldId);
        query.setParameter("newId", newId);
        query.setParameter("replacementDate", replacementDate);
        //
        Component newComponent = new Component();
        newComponent.setId(newId);
        @SuppressWarnings("unchecked")
        List<BomItemReplacement> replacementList = ((List<BomItem>) query.getResultList())
            .stream().map(bomItem -> {
                BomItemReplacement replacement = new BomItemReplacement();
                replacement.setComponent(newComponent);
                replacement.setBomItem(bomItem);
                replacement.setStatus(BomItemReplacementStatus.CATALOG);
                replacement.setReplacementDate(LocalDate.now());
                replacement.setStatusDate(LocalDate.now());
                return replacement;
            })
            .collect(Collectors.toList());
        if (!replacementList.isEmpty()) bomItemReplacementService.saveAll(replacementList);
        return replacementList.size();
    }
}