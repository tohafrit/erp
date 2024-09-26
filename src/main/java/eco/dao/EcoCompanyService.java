package eco.dao;

import eco.entity.EcoCompany;
import eco.repository.EcoCompanyRepository;
import org.springframework.stereotype.Service;
import ru.korundm.enumeration.CompanyTypeEnum;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class EcoCompanyService {

    /** Список внутренних организаций */
    private static final List<Long> INNER_CUSTOMERS = List.of(CompanyTypeEnum.KORUND_M.getId(), CompanyTypeEnum.NIISI.getId(), CompanyTypeEnum.SAPSAN.getId(), CompanyTypeEnum.OAO_KORUND_M.getId());

    private final EcoCompanyRepository companyRepository;

    public EcoCompanyService(EcoCompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @PersistenceContext(unitName = "ecoEntityManagerFactory")
    private EntityManager entityManager;

    public List<EcoCompany> getAll() {
        return companyRepository.findAll();
    }

    /*public List<EcoCompany> getAllByParams(boolean type, DataTablesInput dtInput) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EcoCompany> criteria = criteriaBuilder.createQuery(EcoCompany.class);
        Root<EcoCompany> root = criteria.from(EcoCompany.class);
        List<Predicate> predicateList = getPredicateList(type, dtInput, root, criteriaBuilder);
        CriteriaQuery<EcoCompany> select = criteria.select(root);
        select.where(predicateList.toArray(new Predicate[0]));
        // Сортировка
        if (!dtInput.getOrder().isEmpty()) {
            Order order = dtInput.getOrder().get(0);
            Expression<?> orderExpression = "legalAddress".equals(order.getColumnName()) ? root.get(EcoCompany_.legalAddress) :
                root.get(EcoCompany_.name);
            criteria.orderBy(BaseConstant.SORT_ASC.equals(order.getDir()) ? criteriaBuilder.asc(orderExpression) : criteriaBuilder.desc(orderExpression));
        }
        TypedQuery<EcoCompany> typedQuery = entityManager.createQuery(select);
        typedQuery.setFirstResult(dtInput.getStart());
        typedQuery.setMaxResults(dtInput.getLength());
        return typedQuery.getResultList();
    }*/

   /* public Long getCountByParams(boolean type, DataTablesInput dtInput) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
        Root<EcoCompany> root = criteria.from(EcoCompany.class);
        List<Predicate> predicateList = getPredicateList(type, dtInput, root, criteriaBuilder);
        criteria.select(criteriaBuilder.count(root)).where(predicateList.toArray(new Predicate[0]));
        return entityManager.createQuery(criteria).getSingleResult();
    }*/

    /*private List<Predicate> getPredicateList(boolean type, DataTablesInput dtInput, Root<EcoCompany> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicateList = new ArrayList<>();
        if (type) {
            predicateList.add(root.get(EcoCompany_.companyType).in(INNER_CUSTOMERS));
        }
        String search = dtInput.getSearch().getValue().toLowerCase();
        if (StringUtils.isNotBlank(search)) {
            predicateList.add(
                criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(EcoCompany_.name)), "%" + search + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(EcoCompany_.legalAddress)), "%" + search + "%")
                )
            );
        }
        return predicateList;
    }*/
}