package asu.dao;

import asu.entity.*;
import asu.repository.AsuProdModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AsuProdModuleService {

    /** Список кодов операций означающих, что прод. модуль взят на участок */
    public static final List<String> MODULE_TAKEN_SITE_OPCODES_LIST =
        List.of("967911", "967953", "967963", "967967", "967999", "967931", "967941", "967942", "967943", "967980");

    @PersistenceContext(unitName = "asuEntityManagerFactory")
    private EntityManager emAsu;

    @Autowired
    private AsuProdModuleRepository asuProdModuleRepository;

    @Autowired
    private AsuOperationService asuOperationService;

    @Autowired
    private AsuProdModuleStateOnProdSiteService asuProdModuleStateOnProdSiteService;

    @Autowired
    private AsuUchService asuUchService;

    @PersistenceContext
    private EntityManager entityManager;

    public List<AsuProdModule> getAll() {
        return asuProdModuleRepository.findAll();
    }

    private static final String STATUSQUERY = "select SIGN(count(*)) as wasSold"
        + " from tab_module inner join PRODMODULES on tab_module.ROWID = PRODMODULES.moduleID"
        + " inner join PRODMODULEINULISTS on PRODMODULEINULISTS.prodModuleID = PRODMODULES.ID "
        + " inner join ULISTS on PRODMODULEINULISTS.ulistID = ULISTS.ID"
        + " inner join PRODSTATIONS on ULISTS.prodStationID = PRODSTATIONS.ID"
        + " inner join STATIONS on PRODSTATIONS.stationID = STATIONS.ID"
        + " inner join PRODSTATIONSALES on PRODSTATIONSALES.prodStationID = PRODSTATIONS.ID"
        + " inner join PRODSTATIONSALESTATES on PRODSTATIONSALES.saleStateID = PRODSTATIONSALESTATES.ID"
        + " where PRODMODULES.ID = :id"
        + " and PRODSTATIONSALESTATES.code in (1,6)"
        + " and ULISTS.date = ( select max(UL.date)"
        + " from ULISTS UL"
        + " where UL.prodStationID = ULISTS.prodStationID"
        + " )";


    private static final String ULISTQUERY = "select ULISTS.ID   as ulistID" // distinct ??? 1-ая часть запроса определяет упаковочные листы станции, в которую входит продМодуль напрямую
        + ", 1 as o"       //              2-ая часть запроса определяет упаковочный лист станции, в которую входит станция, в которую входит продМодуль напрямую
        + ", ULISTS.date as d"
        + " from ULISTS inner join PRODMODULEINULISTS on ULISTS.ID = PRODMODULEINULISTS.ulistID"
        + " inner join ( select :pmID as prodModuleID"
        + ", max(mmBefore.timePer) as timeBeg"
        + ", min(mmAfter.timePer)  as timeEnd"
        + " from ( select unix_timestamp('1970-01-01 03:00:01')*1000 as timePer"
        + " UNION"
        + " select MODULEMOVES.timePer"
        + " from MODULEMOVES"
        + " where MODULEMOVES.prodModuleID = :pmID"
        + " and MODULEMOVES.timePer <= :time"
        + ") as mmBefore"
        + ", ( select unix_timestamp('2037-01-01 03:00:01')*1000 as timePer"
        + " UNION"
        + " select MODULEMOVES.timePer"
        + " from MODULEMOVES"
        + " where MODULEMOVES.prodModuleID = :pmID"
        + " and MODULEMOVES.timePer > :time"
        + ") as mmAfter"
        + ") as mmInterval on mmInterval.prodModuleID = PRODMODULEINULISTS.prodModuleID"
        + " inner join PRODSTATIONS on ULISTS.prodStationID   = PRODSTATIONS.ID"
        + " inner join STATIONS     on PRODSTATIONS.stationID = STATIONS.ID"
        + " inner join tab_module   on STATIONS.moduleID      = tab_module.ROWID"
        + " inner join PRODMODULES  on PRODMODULES.moduleID   = tab_module.ROWID"
        + " where PRODMODULEINULISTS.prodModuleID = :pmID"
        + " and PRODMODULEINULISTS.prodModuleID = PRODMODULES.ID"
        + " and ULISTS.date between mmInterval.timeBeg and mmInterval.timeEnd"
        + "   and PRODMODULES.num = PRODSTATIONS.num"
        + " UNION "
        + " select ULISTS.ID as ulistID"
        + ", 2as o"
        + ", ULISTS.date as d"
        + " from ULISTS inner join PRODMODULEINULISTS on ULISTS.ID = PRODMODULEINULISTS.ulistID"
        + " inner join ( select :pmID as prodModuleID"
        + ", max(mmBefore.timePer) as timeBeg"
        + ", min(mmAfter.timePer)  as timeEnd"
        + " from (select unix_timestamp('1970-01-01 03:00:01')*1000 as timePer"
        + " UNION"
        + " select MODULEMOVES.timePer"
        + " from MODULEMOVES"
        + " where MODULEMOVES.prodModuleID = :pmID"
        + " and MODULEMOVES.timePer <= :time"
        + ") as mmBefore"
        + ", ( select unix_timestamp('2037-01-01 03:00:01')*1000 as timePer"
        + " UNION"
        + " select MODULEMOVES.timePer"
        + " from MODULEMOVES"
        + " where MODULEMOVES.prodModuleID = :pmID"
        + " and MODULEMOVES.timePer > :time"
        + ") as mmAfter"
        + ") as mmInterval on mmInterval.prodModuleID = PRODMODULEINULISTS.prodModuleID"
        + " inner join PRODSTATIONS on ULISTS.prodStationID = PRODSTATIONS.ID"
        + " inner join STATIONS on PRODSTATIONS.stationID = STATIONS.ID"
        + " inner join tab_module on STATIONS.moduleID = tab_module.ROWID"
        + " inner join PRODMODULES on PRODMODULES.moduleID = tab_module.ROWID"
        + " where PRODMODULEINULISTS.prodModuleID = :pmID"
        + " and PRODMODULEINULISTS.prodModuleID != PRODMODULES.ID"
        + " and ULISTS.date between mmInterval.timeBeg and mmInterval.timeEnd"
        + " and PRODMODULES.num = PRODSTATIONS.num"
        + " order by 2 desc, 3 desc, 1 desc"
        + " limit 1"
    ;
    public AsuProdModule read(long id) {
        return asuProdModuleRepository.getOne(id);
    }

    public AsuProdModule getByParams(int num, String code1, String code2) {
        return asuProdModuleRepository.findTopByNumAndModule_Code1AndModule_Code2(num, code1, code2);
    }

    public AsuProdModule getByCods(String code1, String code2) {
        return asuProdModuleRepository.findTopByModule_Code1AndModule_Code2(code1, code2);
    }

    public Boolean isWasSoldInStructureOfStation(AsuProdModule pm) {
        Query query = entityManager.createNativeQuery(STATUSQUERY, Boolean.class);
      //  query.setParameter("id",   pm.getId());
        return (Boolean)query.getSingleResult();
    }

    public AsuUlist getUlistOnDate (AsuModuleMove moduleMove) {

        if (moduleMove == null||moduleMove.getProdModule() == null) return null;
        Query query = entityManager.createNativeQuery(ULISTQUERY, AsuUlist.class);
        query.setParameter("pmID", moduleMove.getProdModule().getId());
        query.setParameter("time", moduleMove.getTimePer());
        return (AsuUlist)query.getSingleResult();
    }

    /**
     * Получение списка прод. модулей (изделий), находящихся на заданном участке в заданном состоянии
     * @param codeSite   код участка {@link String}
     * @param codeState  состояние {@link String}
     * @return список прод. модулей
     */
    public List<AsuProdModule> getGivenSiteStateProdModuleList(
        String codeSite,
        String codeState
    ) {
        if (!asuUchService.existsByCode(codeSite) && !asuProdModuleStateOnProdSiteService.existsByCode(codeState)) {
            return Collections.emptyList();
        }
        AsuUch asuUchByCode = asuUchService.getByCode(codeSite);
        CriteriaBuilder criteriaBuilder = emAsu.getCriteriaBuilder();
        CriteriaQuery<AsuProdModule> criteria = criteriaBuilder.createQuery(AsuProdModule.class);
        Root<AsuProdModule> root = criteria.from(AsuProdModule.class);
        SetJoin<AsuProdModule, AsuModuleMove> moduleMoveSetJoin = root.join(AsuProdModule_.moduleMoveSet, JoinType.INNER);
        List<Predicate> predicateList = getSiteStateProdModulePredicateList(root, criteriaBuilder, asuUchByCode, moduleMoveSetJoin);
        CriteriaQuery<AsuProdModule> select = criteria.select(root);
        select.where(predicateList.toArray(new Predicate[0]));
        criteria.orderBy(
            criteriaBuilder.asc(moduleMoveSetJoin.get(AsuModuleMove_.prodSite)),
            criteriaBuilder.asc(root.get(AsuProdModule_.num))
        );
        return emAsu.createQuery(criteria).getResultList();
    }

    /**
     * Получение списка предикатов
     * @param root сущность для получения полей {@link Root}
     * @param cb конструктор критерий {@link CriteriaBuilder}
     * @param asuUchByCode участок {@link AsuUch}
     * @param moduleMoveSetJoin join
     * @return список {@link List} предикатов {@link Predicate}
     */
    private List<Predicate> getSiteStateProdModulePredicateList(
        Root<AsuProdModule> root,
        CriteriaBuilder cb,
        AsuUch asuUchByCode,
        SetJoin<AsuProdModule, AsuModuleMove> moduleMoveSetJoin
    ) {
        List<AsuOperation> asuOperationList = asuOperationService.getAllByCode(MODULE_TAKEN_SITE_OPCODES_LIST);
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(moduleMoveSetJoin.get(AsuModuleMove_.prodSite).isNotNull());
        predicateList.add(cb.equal(moduleMoveSetJoin.get(AsuModuleMove_.arch), false));
        predicateList.add(moduleMoveSetJoin.get(AsuModuleMove_.prodSiteOperation).in(asuOperationList));
        predicateList.add(cb.equal(root.get(AsuProdModule_.isRealBarcode), true));
        if (asuUchByCode != null) {
            predicateList.add(cb.equal(moduleMoveSetJoin.get(AsuModuleMove_.prodSite), asuUchByCode));
        }
        return predicateList;
    }
}
