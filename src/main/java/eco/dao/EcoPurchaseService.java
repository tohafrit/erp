package eco.dao;

import eco.entity.EcoLaunch_;
import eco.entity.EcoPurchase;
import eco.entity.EcoPurchase_;
import eco.entity.EcoUserInfo_;
import eco.repository.EcoPurchaseRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.korundm.form.search.SearchPurchaseForm;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EcoPurchaseService {

    @PersistenceContext(unitName = "ecoEntityManagerFactory")
    private EntityManager entityManager;

    private final EcoPurchaseRepository ecoPurchaseRepository;

    public EcoPurchaseService(EcoPurchaseRepository ecoPurchaseRepository) {
        this.ecoPurchaseRepository = ecoPurchaseRepository;
    }

    public List<EcoPurchase> getAll() {
        return ecoPurchaseRepository.findAll();
    }

    public List<EcoPurchase> getAllById(List<Long> idList) {
        return ecoPurchaseRepository.findAllById(idList);
    }

    public EcoPurchase save(EcoPurchase object) {
        return ecoPurchaseRepository.save(object);
    }

    public List<EcoPurchase> saveAll(List<EcoPurchase> objectList) {
        return ecoPurchaseRepository.saveAll(objectList);
    }

    public EcoPurchase read(long id) {
        return ecoPurchaseRepository.getOne(id);
    }

    public void delete(EcoPurchase object) {
        ecoPurchaseRepository.delete(object);
    }

    public void deleteById(long id) {
        ecoPurchaseRepository.deleteById(id);
    }

    public List<String> getDistinctCreatedBy() {
        return ecoPurchaseRepository.findDistinctCreatedBy();
    }

    /*public List<EcoPurchase> getAllBySearchForm(SearchPurchaseForm form, DataTablesInput dtInput) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EcoPurchase> criteria = cb.createQuery(EcoPurchase.class);
        Root<EcoPurchase> root = criteria.from(EcoPurchase.class);
        List<Predicate> predicateList = getSearchFormPredicateList(form, root, cb);
        CriteriaQuery<EcoPurchase> select = criteria.select(root);
        select.where(predicateList.toArray(new Predicate[0]));
        // Сортировка
        if (!dtInput.getOrder().isEmpty()) {
            Order order = dtInput.getOrder().get(0);
            Expression<?> orderExpression;
            switch(order.getColumnName()) {
                case "name":
                    orderExpression = root.get(EcoPurchase_.name);
                    break;
                case "launch":
                    Path<EcoLaunch> launchPath = root.get(EcoPurchase_.launch);
                    orderExpression = cb.concat(
                        cb.concat(launchPath.get(EcoLaunch_.numberInYear).as(String.class), cb.literal("/")),
                        cb.substring(launchPath.get(EcoLaunch_.year).as(String.class), 3, 2)
                    );
                    break;
                case "note":
                    orderExpression = root.get(EcoPurchase_.note);
                    break;
                case "createdBy":
                    Path<EcoUserInfo> userPath = root.get(EcoPurchase_.user);
                    orderExpression = userPath.get(EcoUserInfo_.lastName);
                    break;
                default:
                    orderExpression = root.get(EcoPurchase_.planDate);
                    break;
            }
            criteria.orderBy(BaseConstant.SORT_ASC.equals(order.getDir()) ? cb.asc(orderExpression) : cb.desc(orderExpression));
        }
        TypedQuery<EcoPurchase> typedQuery = entityManager.createQuery(select);
        typedQuery.setFirstResult(dtInput.getStart());
        typedQuery.setMaxResults(dtInput.getLength());
        return typedQuery.getResultList();
    }*/

    /**
     * Расчет количества расчетов по форме поиска
     * @param form  форма {@link SearchPurchaseForm}
     * @return количество расчетов
     */
    public Long getCountBySearchForm(SearchPurchaseForm form) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
        Root<EcoPurchase> root = criteria.from(EcoPurchase.class);
        List<Predicate> predicateList = getSearchFormPredicateList(form, root, criteriaBuilder);
        criteria.select(criteriaBuilder.count(root)).where(predicateList.toArray(new Predicate[0]));
        return entityManager.createQuery(criteria).getSingleResult();
    }

    /**
     * Получение списка предикатов поисковой формы
     * @param form  форма {@link SearchPurchaseForm}
     * @param root {@link Root}
     * @param criteriaBuilder {@link CriteriaBuilder}
     * @return {@link List<Predicate>}
     */
    private List<Predicate> getSearchFormPredicateList(
            SearchPurchaseForm form,
            Root<EcoPurchase> root,
            CriteriaBuilder criteriaBuilder
    ) {
        List<Predicate> predicateList = new ArrayList<>();
        if (StringUtils.isNotBlank(form.getName())) {
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(EcoPurchase_.name)), "%" + form.getName().toLowerCase() + "%"));
        }
        Path<LocalDate> planDateOn = root.get(EcoPurchase_.planDate);
        if (form.getPlanDateFrom() != null) {
            predicateList.add(criteriaBuilder.greaterThanOrEqualTo(planDateOn, form.getPlanDateFrom()));
        }
        if (form.getPlanDateTo() != null) {
            predicateList.add(criteriaBuilder.lessThanOrEqualTo(planDateOn, form.getPlanDateTo()));
        }
        if (form.getCreatedBy() != null) {
            predicateList.add(criteriaBuilder.equal(root.get(EcoPurchase_.user).get(EcoUserInfo_.id), form.getCreatedBy()));
        }
        if (form.getLaunchId() != null) {
            predicateList.add(criteriaBuilder.equal(root.get(EcoPurchase_.launch).get(EcoLaunch_.id), form.getLaunchId()));
        }
        if (StringUtils.isNotBlank(form.getNote())) {
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(EcoPurchase_.note)), "%" + form.getNote().toLowerCase() + "%"));
        }
        return predicateList;
    }
}