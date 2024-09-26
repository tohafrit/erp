package asu.dao;

import asu.entity.*;
import asu.repository.AsuInvoiceStringRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.korundm.dto.decipherment.DeciphermentDataInvoice;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AsuInvoiceStringService {

    @PersistenceContext(unitName = "asuEntityManagerFactory")
    private EntityManager em;

    private final AsuInvoiceStringRepository asuInvoiceStringRepository;

    public AsuInvoiceStringService(AsuInvoiceStringRepository asuInvoiceStringRepository) {
        this.asuInvoiceStringRepository = asuInvoiceStringRepository;
    }

    public AsuInvoiceString read(long id) {
        return asuInvoiceStringRepository.findFirstById(id);
    }

    /**
     * Метод получения накладных для состава расшифровки
     * @param cell группа-позиция компонента
     * @param plantIdList список идентификаторов предприятия
     * @param dateBegin дата начала поиска
     * @param dateEnd дата окончания поиска
     * @return список накладных для состава расшифровки {@link DeciphermentDataInvoice}
     */
    public List<DeciphermentDataInvoice> getDeciphermentInvoices(String cell, List<Long> plantIdList, LocalDate dateBegin, LocalDate dateEnd) {
        if (!StringUtils.isNumeric(cell) || cell.length() != 6) return new ArrayList<>();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AsuInvoiceString> c = cb.createQuery(AsuInvoiceString.class);
        Root<AsuInvoiceString> root = c.from(AsuInvoiceString.class);
        Join<AsuInvoiceString, AsuPost> postJoin = root.join("postList", JoinType.INNER);
        c.orderBy(
            cb.asc(postJoin.get(AsuPost_.compName).get(AsuComponentName_.component).get(AsuComponent_.group).get(AsuGrpComp_.nomGrp)),
            cb.asc(postJoin.get(AsuPost_.compName).get(AsuComponentName_.component).get(AsuComponent_.pos)),
            cb.asc(postJoin.get(AsuPost_.timeIn))
        );
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(cb.equal(postJoin.get(AsuPost_.compName).get(AsuComponentName_.component).get(AsuComponent_.pos), Long.valueOf(StringUtils.substring(cell, 2))));
        predicateList.add(cb.equal(postJoin.get(AsuPost_.compName).get(AsuComponentName_.component).get(AsuComponent_.group).get(AsuGrpComp_.nomGrp), Long.valueOf(StringUtils.substring(cell, 0, 2))));
        if (CollectionUtils.isNotEmpty(plantIdList)) {
            predicateList.add(root.get(AsuInvoiceString_.invoice).get(AsuInvoice_.plant).get(AsuPlant_.id).in(plantIdList));
        }
        if (dateBegin != null) {
            predicateList.add(cb.ge(root.get(AsuInvoiceString_.invoice).get(AsuInvoice_.dateIn), dateBegin.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()));
        }
        if (dateEnd != null) {
            predicateList.add(cb.le(root.get(AsuInvoiceString_.invoice).get(AsuInvoice_.dateIn), dateEnd.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()));
        }
        // Получаем непринятые накладные
        Map<AsuInvoiceString, BigDecimal> notAcceptableInvoiceMap = getNonAcceptableInvoiceMap(cell, plantIdList, dateBegin, dateEnd);
        if (MapUtils.isNotEmpty(notAcceptableInvoiceMap)) {
            predicateList.add(cb.not(root.get(AsuInvoiceString_.id).in(notAcceptableInvoiceMap.keySet().stream().map(AsuInvoiceString::getId).collect(Collectors.toList()))));
        }
        CriteriaQuery<AsuInvoiceString> select = c.select(root).where(predicateList.toArray(new Predicate[0])).distinct(Boolean.TRUE);
        List<AsuInvoiceString> asuInvoiceStringList = em.createQuery(select).getResultList();

        // Составление строк накладных расшифровок для общих строк накладных и неподтвержденных
        List<DeciphermentDataInvoice> invoiceList = new ArrayList<>();
        for (AsuInvoiceString asuInvoiceString : asuInvoiceStringList) {
            invoiceList.add(createDeciphermentDataInvoice(asuInvoiceString));
        }
        for (Map.Entry<AsuInvoiceString, BigDecimal> entry : notAcceptableInvoiceMap.entrySet()) {
            DeciphermentDataInvoice deciphermentDataInvoice = createDeciphermentDataInvoice(entry.getKey());
            deciphermentDataInvoice.setNotAcceptedQuantity(entry.getValue().multiply(BigDecimal.valueOf(entry.getKey().getCoeff())).setScale(2, RoundingMode.HALF_UP).doubleValue());
            invoiceList.add(deciphermentDataInvoice);
        }
        return invoiceList;
    }

    /**
     * Метод получения строки с данными накладной для расшифровки
     * @param asuInvoiceString строка накладной
     * @return строки с данными накладной для расшифровки {@link DeciphermentDataInvoice}
     */
    private DeciphermentDataInvoice createDeciphermentDataInvoice(AsuInvoiceString asuInvoiceString) {
        DeciphermentDataInvoice deciphermentDataInvoice = new DeciphermentDataInvoice();
        deciphermentDataInvoice.setId(asuInvoiceString.getId());
        deciphermentDataInvoice.setName(asuInvoiceString.getInvoice().getName());
        deciphermentDataInvoice.setDate(Instant.ofEpochMilli(asuInvoiceString.getInvoice().getDateIn()).atZone(ZoneId.systemDefault()).toLocalDate());
        deciphermentDataInvoice.setContractNumber(asuInvoiceString.getInvoice().getContract().getName());
        if (asuInvoiceString.getInvoice().getSupplier() != null) {
            deciphermentDataInvoice.setSupplierName(asuInvoiceString.getInvoice().getSupplier().getName());
        }
        deciphermentDataInvoice.setPrice(BigDecimal.valueOf(asuInvoiceString.getPrice()).divide(BigDecimal.valueOf(asuInvoiceString.getCoeff()), 2, RoundingMode.HALF_UP).doubleValue());
        deciphermentDataInvoice.setInitialQuantity(BigDecimal.valueOf(asuInvoiceString.getQuan()).multiply(BigDecimal.valueOf(asuInvoiceString.getCoeff())).setScale(2, RoundingMode.HALF_UP).doubleValue());
        deciphermentDataInvoice.setCurrentQuantity(BigDecimal.valueOf(asuInvoiceString.getCurrentQuantity()).setScale(2, RoundingMode.HALF_UP).doubleValue());
        deciphermentDataInvoice.setReservedQuantity(BigDecimal.valueOf(asuInvoiceString.getReserveQuantity()).setScale(2, RoundingMode.HALF_UP).doubleValue());
        deciphermentDataInvoice.setWastedQuantity(BigDecimal.valueOf(asuInvoiceString.getWasteQuantity()).multiply(BigDecimal.valueOf(asuInvoiceString.getCoeff())).setScale(2, RoundingMode.HALF_UP).doubleValue());
        return deciphermentDataInvoice;
    }

    /**
     * Метод получения словаря накладных с непринятым количеством
     * @param cell группа-позиция компонента
     * @param plantIdList список идентификаторов предприятия
     * @param dateBegin дата начала поиска
     * @param dateEnd дата окончания поиска
     * @return словарь накладных с непринятым количеством
     */
    private Map<AsuInvoiceString, BigDecimal> getNonAcceptableInvoiceMap(
        String cell,
        List<Long> plantIdList,
        LocalDate dateBegin,
        LocalDate dateEnd
    ) {
        if (!StringUtils.isNumeric(cell) || cell.length() != 6) return new HashMap<>();

        StringBuilder sb = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();
        sb.append("" +
            "SELECT\n" +
            "  si.id,\n" +
            "  CAST(si.quan * si.coeff AS decimal) - COALESCE(SUM(tp.nach_kol), 0) AS qDiff\n" +
            "FROM\n" +
            "  INVOICES i\n" +
            "  JOIN\n" +
            "  INVOICESTRINGS si \n" +
            "  ON\n" +
            "  i.ID = si.invoiceID\n" +
            "  AND si.orderRecord REGEXP \"[0-9]{6}\"\n" +
            "  AND LENGTH(si.orderRecord) = 6\n" +
            "  AND si.orderRecord = :cell\n");
        parameters.put("cell", cell);
        if (dateBegin != null) {
            sb.append(" AND i.dateIn >= :dateBegin\n");
            parameters.put("dateBegin", dateBegin.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
        }
        if (dateEnd != null) {
            sb.append(" AND i.dateIn <= :dateEnd\n");
            parameters.put("dateEnd", dateEnd.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
        }
        if (CollectionUtils.isNotEmpty(plantIdList)) {
            sb.append(" AND i.plantID IN (:plantIdList)\n");
            parameters.put("plantIdList", plantIdList);
        }
        sb.append("" +
            "LEFT JOIN\n" +
            "  tab_post tp\n" +
            "  ON\n" +
            "  si.ID = tp.isID\n" +
            "GROUP BY \n" +
            "  1\n" +
            "HAVING\n" +
            "  qDiff > 0");
        Query nativeQuery = em.createNativeQuery(sb.toString());
        parameters.forEach(nativeQuery::setParameter);

        @SuppressWarnings("unchecked")
        List<Object[]> resultList = nativeQuery.getResultList();
        Map<AsuInvoiceString, BigDecimal> resultMap = new HashMap<>();
        resultList.forEach(item -> resultMap.put(em.find(AsuInvoiceString.class, ((BigInteger) item[0]).longValue()), (BigDecimal) item[1]));
        return resultMap;
    }
}