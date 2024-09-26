package eco.dao;

import eco.entity.*;
import eco.repository.EcoMatValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class EcoMatValueService {

    @PersistenceContext(unitName = "ecoEntityManagerFactory")
    private EntityManager em;

    @Autowired
    private EcoMatValueRepository matValueRepository;

    public List<EcoMatValue> getAll() {
        return matValueRepository.findAll();
    }

    public EcoMatValue getBySerialNumber(String serialNumber) {
        return matValueRepository.findBySerialNumber(serialNumber);
    }

    /*public List<EcoMatValue> getMatValueList(
        Long entityId,
        DataTablesInput dataTablesInput
    ) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EcoMatValue> criteria = cb.createQuery(EcoMatValue.class);
        Root<EcoInOutDocument> root = criteria.from(EcoInOutDocument.class);
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(cb.equal(root.get(EcoInOutDocument_.id), entityId));
        Join<EcoInOutDocument, EcoMvInOutDocReference> mvInOutDocReferenceJoin = root.join(EcoInOutDocument_.mvInOutDocReferenceList, JoinType.LEFT);
        Join<EcoMvInOutDocReference, EcoMatValue> matValueJoin = mvInOutDocReferenceJoin.join(EcoMvInOutDocReference_.matValue, JoinType.INNER);
        CriteriaQuery<EcoMatValue> select = criteria.select(matValueJoin);
        select.where(predicateList.toArray(new Predicate[0]));

        if (!dataTablesInput.getOrder().isEmpty()) {
            Order order = dataTablesInput.getOrder().get(0);
            List<Path<?>> orderExpressionList = new ArrayList<>();
            switch (order.getColumnName()) {
                case "product":
                    orderExpressionList.add(matValueJoin.get(EcoMatValue_.product).get(EcoProduct_.fullName));
                    break;
                case "serialNumber":
                    orderExpressionList.add(matValueJoin.get(EcoMatValue_.serialNumber));
                    break;
                case "cell":
                    orderExpressionList.add(matValueJoin.get(EcoMatValue_.storeCell).get(EcoStoreCell_.name));
                    break;
                case "contract":
                    orderExpressionList.add(matValueJoin.get(EcoMatValue_.contractSection).get(EcoContractSection_.contract).get(EcoContract_.contractNumber));
                    break;
                case "customer":
                    orderExpressionList.add(matValueJoin.get(EcoMatValue_.contractSection).get(EcoContractSection_.contract).get(EcoContract_.customer).get(EcoCompany_.name));
                    break;
                case "letter":
                    orderExpressionList.add(matValueJoin.get(EcoMatValue_.productionShipmentLetter).get(EcoProductionShipmentLetter_.letterNumber));
                    break;
                case "place":
                    orderExpressionList.add(matValueJoin.get(EcoMatValue_.storagePlace).get(EcoStoragePlace_.place));
                    break;
            }
            List<javax.persistence.criteria.Order> orderList = BaseConstant.SORT_ASC.equals(order.getDir()) ?
                orderExpressionList.stream().map(cb::asc).collect(Collectors.toList()) : orderExpressionList.stream().map(cb::desc).collect(Collectors.toList());
            criteria.orderBy(orderList);
        }
        TypedQuery<EcoMatValue> typedQuery = em.createQuery(select);
        typedQuery.setFirstResult(dataTablesInput.getStart());
        typedQuery.setMaxResults(dataTablesInput.getLength());
        return typedQuery.getResultList();
    }*/

    public long getMatValueCount(Long entityId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
        Root<EcoInOutDocument> root = criteria.from(EcoInOutDocument.class);
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(cb.equal(root.get(EcoInOutDocument_.id), entityId));
        Join<EcoInOutDocument, EcoMvInOutDocReference> mvInOutDocReferenceJoin = root.join(EcoInOutDocument_.mvInOutDocReferenceList, JoinType.LEFT);
        Join<EcoMvInOutDocReference, EcoMatValue> matValueJoin = mvInOutDocReferenceJoin.join(EcoMvInOutDocReference_.matValue, JoinType.INNER);
        criteria.select(cb.count(root)).where(predicateList.toArray(new Predicate[0]));
        return em.createQuery(criteria).getSingleResult();
    }
}