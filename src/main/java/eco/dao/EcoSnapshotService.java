package eco.dao;

import eco.entity.*;
import eco.repository.EcoSnapshotRepository;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Service;
import ru.korundm.dto.purchase.Snapshot;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Service
public class EcoSnapshotService {

    /** Часть процедуры ECOPROC_PKG.guaranty_launch_insert */
    private static final String GUARANTY_LAUNCH_INSERT =
            "SELECT \n" +
            "       b.bom_id as \"bomId\", P.ID as \"productId\", V.AMOUNT as \"amount\", " +
            "       V.from_reserve_for_sale + V.from_reserve_for_other + V.from_reserve_in_other as \"reserve\", \n" +
            "       nvl(amount_contract,0) as \"amountContract\", nvl(amount_unpaid,0) as \"amountUnpaid\", " +
            "       nvl(v.unalloted,0) as \"unalloted\", nvl(amount_internal,0) as \"amountInternal\"\n" +
            "   FROM\n" +
            "   (\n" +
            "       SELECT product.id product_id, \n" +
            "               decode (?, 0, max_approved.bom_id, 1, max_bom.bom_id, 2, from_launch.bom_id) bom_id\n" +
            "           FROM product, \n" +
            "           (\n" +
            "               SELECT product_id, id bom_id\n" +
            "                   FROM\n" +
        "                       (\n" +
            "                       SELECT product_id, id, rank() OVER (PARTITION BY product_id ORDER BY major DESC, minor DESC, modification DESC) rnk\n" +
            "                           FROM bom\n" +
    "                           )\n" +
                "               WHERE rnk = 1\n" +
            "           ) max_bom,\n" +
            "           (\n" +
            "               -- максимальный утвержденный бом для каждого продукта\n" +
            "               SELECT product_id, id bom_id\n" +
            "                   FROM\n" +
            "                   (\n" +
            "                       SELECT b.product_id, b.id, rank() OVER (PARTITION BY b.product_id ORDER BY b.major DESC, b.minor DESC, b.modification DESC, l.year DESC, l.number_in_year DESC) rnk\n" +
            "                           FROM bom b, bom_attribute ba, launch l\n" +
            "                           WHERE\n" +
            "                               ba.bom_id = b.id AND ba.launch_id = l.id AND BA.APPROVE_DATE IS NOT NULL\n" +
            "                   )\n" +
            "                   WHERE rnk = 1\n" +
            "           ) max_approved,\n" +
            "           (\n" +
            "               SELECT product_id, PL.BOM_ID\n" +
            "                   FROM v_lp_calcvalues v, production_lot pl\n" +
            "                   WHERE v.launch_id = ? AND PL.LAUNCH_PRODUCT_ID = V.LAUNCH_PRODUCT_ID\n" +
            "           ) from_launch\n" +
        "               WHERE product.id = max_bom.product_id(+) \n" +
            "               AND product.id = max_approved.product_id(+)\n" +
            "               AND product.id = from_launch.product_id(+)\n" +
            "   )  b, \n" +
            "   (\n" +
            "       SELECT a.group_id, sum(decode(C.CONTRACT_TYPE, 8, 0, A.AMOUNT)) amount_contract,\n" +
            "               sum(decode(nvl(a.paid, 0),0,decode(C.CONTRACT_TYPE, 8, 0, A.AMOUNT), 0)) amount_unpaid,\n" +
            "               sum(decode(C.CONTRACT_TYPE, 8, A.AMOUNT, 0)) amount_internal\n" +
            "           FROM\n" +
            "               ALLOTMENT a,\n" +
            "               LOT l,\n" +
            "               LOT_GROUP g,\n" +
            "               CONTRACT_SECTION s,\n" +
            "               CONTRACT_SECTION s0,\n" +
            "               CONTRACT c\n" +
            "           WHERE l.ID = a.LOT_ID\n" +
            "               AND g.ID = l.GROUP_ID\n" +
            "               AND s.ID = g.CONTRACT_SECTION_ID\n" +
            "               AND c.ID = s.CONTRACT_ID\n" +
            "               AND s.CONTRACT_ID = s0.CONTRACT_ID\n" +
            "               AND s0.SECTION_NUMBER = 0\n" +
            "           GROUP BY a.group_id\n" +
            "   ) amounts,\n" +
            "   product p, V_LP_CALCVALUES v\n" +
            "       WHERE V.LAUNCH_ID = ?\n" +
            "           AND V.PRODUCT_ID = b.product_id\n" +
            "           AND V.PRODUCT_ID = p.id\n" +
            "           AND (V.AMOUNT > 0 or V.from_reserve_for_sale + V.from_reserve_for_other + V.from_reserve_in_other > 0)\n" +
            "           AND P.PRODUCT_TYPE != 2048\n" +
            "           AND V.LAUNCH_PRODUCT_ID = amounts.group_id(+)";

    /** Часть процедуры ECOPROC_PKG.select_boms удалить proc_tmp_bom */
    private static final String PROC_TMP_BOM_DELETE =
            "DELETE FROM proc_tmp_bom";

    /** Часть процедуры ECOPROC_PKG.select_boms для selection_type = 0 */
    private static final String PROC_TMP_BOM_SELECTION_TYPE_ZERO =
            "INSERT INTO proc_tmp_bom\n" +
            "(\n" +
            "   SELECT \n" +
            "       product.id product_id, max_approved.bom_id, max_approved.attribute_id\n" +
            "   FROM \n" +
            "       product, \n" +
            "       (\n" +
            "           -- максимальный утвержденный бом для каждого продукта\n" +
            "           SELECT \n" +
            "               product_id, id bom_id, attribute_id\n" +
            "           FROM\n" +
            "               (\n" +
            "                   SELECT \n" +
            "                       b.product_id, b.id, RANK() OVER (PARTITION BY b.product_id ORDER BY b.major DESC, b.minor DESC, b.modification DESC, l.year DESC, l.number_in_year DESC) rnk, ba.id attribute_id\n" +
            "                   FROM \n" +
            "                       bom b, \n" +
            "                       bom_attribute \n" +
            "                       ba, \n" +
            "                       launch l\n" +
            "                    WHERE\n" +
            "                       ba.bom_id = b.id\n" +
            "                       AND ba.launch_id = l.id\n" +
            "                       AND BA.APPROVE_DATE IS NOT NULL\n" +
            "                )\n" +
            "            WHERE rnk = 1\n" +
            "        ) max_approved\n" +
            "    WHERE product.id = max_approved.product_id(+)\n" +
            ")";

    /** Часть процедуры ECOPROC_PKG.select_boms для selection_type = 1 */
    private static final String PROC_TMP_BOM_SELECTION_TYPE_ONE =
            "INSERT INTO proc_tmp_bom\n" +
            "(\n" +
            "   SELECT \n" +
            "       product.id product_id, max_bom.bom_id, (select max(bom_attribute.id) from bom_attribute where bom_id = max_bom.bom_id) attribute_id\n" +
            "   FROM product, \n" +
            "       (  -- бом максимальной версии для каждого продукта\n" +
            "           SELECT \n" +
            "               product_id, id bom_id\n" +
            "           FROM\n" +
            "               (\n" +
            "                   SELECT \n" +
            "                       product_id, id, RANK() OVER (PARTITION BY product_id ORDER BY major DESC, minor DESC, modification DESC) rnk\n" +
            "                   FROM bom\n" +
            "               )\n" +
            "           WHERE rnk = 1\n" +
            "       ) max_bom\n" +
            "   WHERE product.id = max_bom.product_id(+) \n" +
            ")";

    /** Часть процедуры ECOPROC_PKG.select_boms для selection_type = 2 */
    private static final String PROC_TMP_BOM_SELECTION_TYPE_TWO =
            "INSERT INTO proc_tmp_bom\n" +
            "(\n" +
            "   SELECT \n" +
            "       product.id product_id, from_launch.bom_id,\n" +
            "       (\n" +
            "           SELECT \n" +
            "               MAX(bom_attribute.id) " +
            "           FROM \n" +
            "               bom_attribute \n" +
            "           WHERE \n" +
            "               bom_id = from_launch.bom_id" +
            "       ) attribute_id\n" +
            "   FROM \n" +
            "       product, \n" +
            "       (\n" +
            "           SELECT \n" +
            "               product_id, PL.BOM_ID\n" +
            "           FROM \n" +
            "               v_lp_calcvalues v, \n" +
            "               production_lot pl\n" +
            "           WHERE v.launch_id = PARAM_LAUNCH_ID AND PL.LAUNCH_PRODUCT_ID = V.LAUNCH_PRODUCT_ID\n" +
            "       ) from_launch\n" +
            "   WHERE product.id = from_launch.product_id(+)\n" +
            ")";

    /** Часть процедуры ECOPROC_PKG.REFRESH_CALC_SNAPSHOT */
    private static final String PROC_CALC_SNAPSHOT =
            "SELECT \n" +
            "   SNAPSHOT_PARAM, PARENT_BOM_ID, CHILD_BOM_ID, PRODUCT_ID, LAUNCH, TOTAL_CONTRACTOR, \n" +
            "   ecoproc_pkg.get_mult(amount_str), ecoproc_pkg.get_mult(amount_str_contract),\n" +
            "   ecoproc_pkg.get_mult(amount_str_unpaid), ecoproc_pkg.get_mult(amount_str_unalloted),\n" +
            "   ecoproc_pkg.get_mult(amount_str_internal), reserve_amount\n" +
            "FROM\n" +
            "   (\n" +
            "       SELECT \n" +
            "           BOM_ID as PARENT_BOM_ID, CHILD_BOM_ID, PRODUCT_ID, AMOUNT, TOTAL_CONTRACTOR,\n" +
            "           LEVEL, SYS_CONNECT_BY_PATH(P.PRODUCT_NAME , '/*/') \"Path\", \n" +
            "           substr(SYS_CONNECT_BY_PATH(Amount, '*'),2) AMOUNT_STR, \n" +
            "           substr(SYS_CONNECT_BY_PATH(Amount_contract, '*'),2) AMOUNT_STR_contract,\n" +
            "           substr(SYS_CONNECT_BY_PATH(Amount_unpaid, '*'),2) AMOUNT_STR_unpaid,\n" +
            "           substr(SYS_CONNECT_BY_PATH(Amount_unalloted, '*'),2) AMOUNT_STR_unalloted,\n" +
            "           substr(SYS_CONNECT_BY_PATH(Amount_internal, '*'),2) AMOUNT_STR_internal,\n" +
            "           reserve_amount\n" +
            "       FROM\n" +
            "          (\n" +
            "               -- состав изделий\n" +
            "               SELECT \n" +
            "                   S.BOM_ID, nvl(b2.bom_id, 0) child_bom_id, S.SUB_PRODUCT_ID product_id, S.SUB_PRODUCT_COUNT amount, \n" +
            "                   S.SUB_PRODUCT_COUNT amount_contract, S.SUB_PRODUCT_COUNT amount_unpaid, \n" +
            "                   S.SUB_PRODUCT_COUNT amount_unalloted, S.SUB_PRODUCT_COUNT amount_internal, 0 reserve_amount,\n" +
            "                   decode(\n" +
            "                               b.app_contractor_mark,1, \n" +
            "                               nvl2(" +
            "                                       b2.attribute_id, \n" +
            "                                       (\n" +
            "                                           SELECT \n" +
            "                                               prodstaff_contractor \n" +
            "                                           FROM \n" +
            "                                               bom_attribute \n" +
            "                                           WHERE \n" +
            "                                               id = b2.attribute_id" +
            "                                       ), \n" +
            "                                       s.contractor\n" +
            "                                   ),  \n" +
            "                                   s.contractor\n" +
            "                   ) total_contractor\n" +
            "               FROM \n" +
            "                   proc_tmp_bom b1, proc_tmp_bom b2, bom_spec_item s, bom b\n" +
            "               WHERE \n" +
            "                   S.BOM_ID = b1.bom_id AND S.SUB_PRODUCT_ID = b2.product_id AND B.ID(+) = b2.bom_id\n" +
            "           UNION ALL\n" +
            "               -- вторая таблица, данные снимка\n" +
            "               SELECT \n" +
            "                   NULL BOM_ID, S.BOM_ID AS CHILD_BOM_ID, S.PRODUCT_ID,\n" +
            "                   S.AMOUNT, amount_contract, amount_unpaid, amount_unalloted, amount_internal,\n" +
            "                   s.reserve_amount, BA.PRODSTAFF_CONTRACTOR\n" +
            "               FROM \n" +
            "                   PROC_SNAPSHOT S, PROC_SNAPSHOT_PARAM G, BOM B, \n" +
            "                   (" +
            "                       SELECT \n" +
            "                           maxba.bom_id, PRODSTAFF_CONTRACTOR \n" +
            "                       FROM \n" +
            "                           (" +
            "                               SELECT \n" +
            "                                   max(id) id, bom_id\n" +
            "                               FROM \n" +
            "                                   bom_attribute\n" +
            "                               GROUP BY bom_id" +
            "                           ) maxba, bom_attribute \n" +
            "                        WHERE \n" +
            "                           maxba.id = bom_attribute.id" +
            "                   ) ba\n" +
            "               WHERE \n" +
            "                   S.SNAPSHOT_PARAM_ID = G.ID AND S.BOM_ID = B.ID AND S.AMOUNT > 0\n" +
            "                   AND B.ID = BA.BOM_ID (+) AND G.ID = SNAPSHOT_PARAM AND origin_attribute_id = LAUNCH\n" +
            "           ) tbl, PRODUCT p\n" +
            "       WHERE \n" +
            "           P.ID = PRODUCT_ID START WITH BOM_ID IS NULL CONNECT BY NOCYCLE PRIOR CHILD_BOM_ID = BOM_ID\n" +
            "       ORDER BY SYS_CONNECT_BY_PATH(P.PRODUCT_NAME , '/*/')\n" +
            "   ) result; ";

    @PersistenceContext(unitName = "ecoEntityManagerFactory")
    private EntityManager em;

    private final EcoSnapshotRepository ecoSnapshotRepository;

    public EcoSnapshotService(EcoSnapshotRepository ecoSnapshotRepository) {
        this.ecoSnapshotRepository = ecoSnapshotRepository;
    }

    public List<EcoSnapshot> getAll() {
        return ecoSnapshotRepository.findAll();
    }

    public List<EcoSnapshot> getAllById(List<Long> idList) {
        return ecoSnapshotRepository.findAllById(idList);
    }

    public EcoSnapshot save(EcoSnapshot object) {
        return ecoSnapshotRepository.save(object);
    }

    public List<EcoSnapshot> saveAll(List<EcoSnapshot> objectList) {
        return ecoSnapshotRepository.saveAll(objectList);
    }

    public EcoSnapshot read(long id) {
        return ecoSnapshotRepository.getOne(id);
    }

    public void delete(EcoSnapshot object) {
        ecoSnapshotRepository.delete(object);
    }

    public void deleteById(long id) {
        ecoSnapshotRepository.deleteById(id);
    }

    /*public List<EcoSnapshot> getAllByParams(Long snapshotParameterId, DataTablesInput dtInput) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EcoSnapshot> criteria = cb.createQuery(EcoSnapshot.class);
        Root<EcoSnapshot> root = criteria.from(EcoSnapshot.class);
        CriteriaQuery<EcoSnapshot> select = criteria.select(root);

        // Ограничения
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(cb.equal(root.get(EcoSnapshot_.snapshotParameter), snapshotParameterId));
        predicateList.add(cb.gt(root.get(EcoSnapshot_.amount), 0));
        select.where(predicateList.toArray(new Predicate[0]));

        // Сортировка
        if (!dtInput.getOrder().isEmpty()) {
            Order order = dtInput.getOrder().get(0);
            Expression<?> orderExpression;
            switch(order.getColumnName()) {
                case "productName":
                    orderExpression = root.get(EcoSnapshot_.product).get(EcoProduct_.productName);
                    break;
                case "version":
                    orderExpression = root.get(EcoSnapshot_.bom).get(EcoBom_.version);
                    break;
                case "count":
                    orderExpression = root.get(EcoSnapshot_.count);
                    break;
                case "reserveAmount":
                    orderExpression = root.get(EcoSnapshot_.reserveAmount);
                    break;
                default:
                    orderExpression = root.get(EcoSnapshot_.id);
                    break;
            }
            criteria.orderBy(BaseConstant.SORT_ASC.equals(order.getDir()) ? cb.asc(orderExpression) : cb.desc(orderExpression));
        }

        return em.createQuery(criteria).getResultList();
    }*/

    @SuppressWarnings("unchecked")
    public List<Snapshot> getSnapshotList(Long type, Long launchId) {
        return em.createNativeQuery(GUARANTY_LAUNCH_INSERT)
                .setParameter(1, type)
                .setParameter(2, launchId)
                .setParameter(3, launchId)
                .unwrap(org.hibernate.query.NativeQuery.class)
                .addScalar("bomId", StandardBasicTypes.LONG)
                .addScalar("productId", StandardBasicTypes.LONG)
                .addScalar("amount", StandardBasicTypes.INTEGER)
                .addScalar("reserve", StandardBasicTypes.INTEGER)
                .addScalar("amountContract", StandardBasicTypes.INTEGER)
                .addScalar("amountUnpaid", StandardBasicTypes.INTEGER)
                .addScalar("unalloted", StandardBasicTypes.INTEGER)
                .addScalar("amountInternal", StandardBasicTypes.INTEGER)
                .setResultTransformer(Transformers.aliasToBean(Snapshot.class))
                .getResultList();
    }

    public void deleteProcTmpBom() {
        em.createNativeQuery(PROC_TMP_BOM_DELETE).executeUpdate();
    }

    public void insertProcTmpBom(long selectionType) {
        Query query;
        switch((int) selectionType) {
            case 0:
                query = em.createNamedQuery(PROC_TMP_BOM_SELECTION_TYPE_ZERO);
                break;
            case 1:
                query = em.createNamedQuery(PROC_TMP_BOM_SELECTION_TYPE_ONE);
                break;
            default:
                query = em.createNamedQuery(PROC_TMP_BOM_SELECTION_TYPE_TWO);
                break;
        }
        query.executeUpdate();
    }
}