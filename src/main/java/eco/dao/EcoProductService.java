package eco.dao;

import eco.entity.EcoProduct;
import eco.repository.EcoProductRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.korundm.dto.product.ReportLineAnalogStatus;
import ru.korundm.dto.product.ReportLineMissAnalog;
import ru.korundm.dto.product.ReportLineSpecLaunch;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EcoProductService {

    @PersistenceContext(unitName = "ecoEntityManagerFactory")
    private EntityManager entityManager;

    private final EcoProductRepository productRepository;

    public EcoProductService(EcoProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public EcoProduct read(long id) {
        return productRepository.getOne(id);
    }

    public List<EcoProduct> getAll() {
        return productRepository.findAll();
    }

    public boolean existById(Long id) {
        return id != null && productRepository.existsById(id);
    }

    public List<EcoProduct> getHierarchyProductListByBomId(Long bomId) {
        return productRepository.findHierarchyProductListByBomId(bomId);
    }

    @SuppressWarnings("unchecked")
    public List<ReportLineSpecLaunch> reportSpecForLaunch(Long launchId) {
        String nativeQuery =
            "select p.id, p.PRODUCT_NAME,\n" +
            "       pl.BOM_ID BOM_ID,\n" +
            "       s1.PRODUCT_VER LAUNCH_VER,\n" +
            "       s1.LAUNCH_NUM LAUNCH_NUM,\n" +
            "       u.LAST_NAME,\n" +
            "       first_for_launch.first_approved,\n" +
            "       modified_for_launch.modified\n" +
            "from   launch_product lp, product p, production_lot pl, user_info u, t_product_type pt,\n" +
            "        (-- версия продукта к запуску и к какому запуску утв.\n" +
            "        select b.id BOM_ID,\n" +
            "               to_char(b.MAJOR)||'.'||to_char(b.MINOR)||'['||b.MODIFICATION||']' PRODUCT_VER,\n" +
            "               SS.LAUNCH_NUM LAUNCH_NUM\n" +
            "          from   bom b,\n" +
            "               ( select max_number.bom_id,  to_char(max_number.num) || '/' || to_char(max_number.year, 'yy') launch_num from\n" +
            "                    ( -- максимальный год запуска, к которому утверждалась ЗC\n" +
            "                    select ba.BOM_ID, max(year) max_year\n" +
            "                    from bom_attribute ba, launch l\n" +
            "                    where ba.APPROVE_DATE is not null\n" +
            "                    and   ba.LAUNCH_ID = l.id\n" +
            "                    group by bom_id\n" +
            "                    ) max_year,\n" +
            "                    ( -- максимальный номер запуска в каждом году, к которому утвержадалась ЗС\n" +
            "                    select ba.BOM_ID, l.year, max(number_in_year) num\n" +
            "                    from bom_attribute ba, launch l\n" +
            "                    where ba.APPROVE_DATE is not null\n" +
            "                    and   ba.LAUNCH_ID = l.id\n" +
            "                    group by bom_id, year\n" +
            "                    ) max_number\n" +
            "                    where\n" +
            "                    max_year.bom_id = max_number.bom_id\n" +
            "                    and max_year.max_year = max_number.year\n" +
            "                ) SS\n" +
            "        where  b.MAJOR > 0\n" +
            "        and       b.id = SS.bom_id (+)\n" +
            "          )s1,\n" +
            "       (-- продукты, впервые утвержденные к текущему запуску\n" +
            "        select product.id, 'x' first_approved\n" +
            "        from\n" +
            "        product\n" +
            "        where id in\n" +
            "        (      \n" +
            "            -- продукты, утвержденные к текущему запуску\n" +
            "            select product_id\n" +
            "            from bom, bom_attribute\n" +
            "            where bom.id = bom_attribute.bom_id\n" +
            "            and bom_attribute.launch_id = :in_launch_id\n" +
            "            and bom_attribute.approve_date is not null\n" +
            "\n" +
            "            minus \n" +
            "\n" +
            "            -- продукты, которые были утверждены до текущего запуска\n" +
            "            select product_id\n" +
            "            from bom, bom_attribute, launch \n" +
            "            where bom.id = bom_attribute.bom_id\n" +
            "            and approve_date is not null\n" +
            "            and launch_id < :in_launch_id\n" +
            "         )\n" +
            "      ) first_for_launch,\n" +
            "        (\n" +
            "            select id, 'x' modified\n" +
            "            from\n" +
            "            product\n" +
            "            where id in\n" +
            "            (\n" +
            "                -- продукты, утвержденные к текущему запуску, у которых 1 бом с одним атрибутом\n" +
            "                select product_id\n" +
            "                from bom, bom_attribute\n" +
            "                where bom.id = bom_attribute.bom_id\n" +
            "                and bom_attribute.launch_id = :in_launch_id\n" +
            "                and bom_attribute.approve_date is not null\n" +
            "                and bom.id in\n" +
            "                (   -- бомы, у которых только 1 атрибут\n" +
            "                    select bom_id from\n" +
            "                    (   -- количество атрибутов у каждого бома\n" +
            "                        select bom_id, count(*) cnt\n" +
            "                        from bom_attribute\n" +
            "                        group by bom_id\n" +
            "                    )\n" +
            "                    where cnt = 1\n" +
            "                )\n" +
            "\n" +
            "                intersect \n" +
            "\n" +
            "                -- продукты, которые использовались до текущего запуска\n" +
            "                select product_id\n" +
            "                from bom, bom_attribute, launch \n" +
            "                where bom.id = bom_attribute.bom_id\n" +
            "                and approve_date is not null and accept_date is not null\n" +
            "                and launch_id < :in_launch_id\n" +
            "            )\n" +
            "    ) modified_for_launch\n" +
            "where  lp.LAUNCH_ID = :in_launch_id\n" +
            "and       lp.PRODUCT_ID = p.id\n" +
            "and    first_for_launch.id(+) = p.id\n" +
            "and    modified_for_launch.id(+) = p.id\n" +
            "and       pl.LAUNCH_PRODUCT_ID = lp.ID\n" +
            "and       p.BOM_TYPE <> 3\n" +
            "and    p.PRODUCT_TYPE < 256\n" +
            "and    pl.AMOUNT > 0\n" +
            "and       u.id(+) = p.constructor_id\n" +
            "and       pl.BOM_ID = s1.BOM_ID (+)\n" +
            "and   p.PRODUCT_TYPE = pt.TYPE_ID\n" +
            "order by pt.ORDER_INDEX, p.PRODUCT_NAME";
        return (List<ReportLineSpecLaunch>) entityManager.createNativeQuery(nativeQuery)
            .setParameter("in_launch_id", launchId)
            .getResultList().stream().map(obj -> {
                Object[] row = (Object[]) obj;
                ReportLineSpecLaunch line = new ReportLineSpecLaunch();
                line.setProductId(((BigDecimal) row[0]).longValue());
                line.setProductName(row[1] == null ? null : (String) row[1]);
                line.setBomId(row[2] == null ? null : ((BigDecimal) row[2]).longValue());
                line.setLaunchVersion(row[3] == null ? null : (String) row[3]);
                line.setLaunchNumber(row[4] == null ? null : (String) row[4]);
                line.setLastName(row[5] == null ? null : (String) row[5]);
                line.setFirstApproved(row[6] == null ? null : ((Character) row[6]).toString());
                line.setModified(row[7] == null ? null : ((Character) row[7]).toString());
                return line;
            }).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<ReportLineMissAnalog> reportMissAnalogs(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return Collections.emptyList();
        }
        String nativeQuery = "select cell, name, description, purch_cell, \n" +
            "        purch_name, purch_descr, prod, VERSION, str_agg(launch)\n" +
            "        from\n" +
            "        (\n" +
            "            select distinct\n" +
            "            bc.cell, bc.name, bc.description, purch.cell purch_cell, \n" +
            "            purch.name purch_name, purch.description purch_descr, \n" +
            "            P.PRODUCT_NAME prod, B.VERSION, l.name launch\n" +
            "            from bom_item bi,\n" +
            "            bom_item_component bic, bom_component bc, \n" +
            "            bom_component purch, product p, \n" +
            "            bom b left outer join bom_attribute ba on \n" +
            "            ba.bom_id = b.id and BA.APPROVE_DATE is not null \n" +
            "            left outer join launch l on ba.launch_id = l.id\n" +
            "            where bic.component_id = bc.id\n" +
            "            and bic.kd = 1\n" +
            "            and bc.purchase_component_id = purch.id\n" +
            "            and bi.id = bic.bom_item_id\n" +
            "            and b.id = bi.bom_id\n" +
            "            and p.id = b.product_id\n" +
            "            and (trunc(B.CREATED) >= :a_from and trunc(B.CREATED) <= :a_to\n" +
            "            or trunc(BA.APPROVE_DATE) >= :a_from and trunc(BA.APPROVE_DATE) <= :a_to)\n" +
            "            and not exists\n" +
            "            (\n" +
            "                select id from bom_item_component bic1\n" +
            "                where bic1.bom_item_id = bic.bom_item_id\n" +
            "                and bic1.component_id = bc.purchase_component_id\n" +
            "            )\n" +
            "        )\n" +
            "        group by cell, name, description, purch_cell, \n" +
            "        purch_name, purch_descr, prod, VERSION\n" +
            "        order by cell";
        return (List<ReportLineMissAnalog>) entityManager.createNativeQuery(nativeQuery)
            .setParameter("a_from", startDate)
            .setParameter("a_to", endDate)
            .getResultList().stream().map(obj -> {
                Object[] row = (Object[]) obj;
                ReportLineMissAnalog line = new ReportLineMissAnalog();
                line.setCell(row[0] == null ? null : (String) row[0]);
                line.setName(row[1] == null ? null : (String) row[1]);
                line.setDescription(row[2] == null ? null : (String) row[2]);
                line.setPurCell(row[3] == null ? null : (String) row[3]);
                line.setPurName(row[4] == null ? null : (String) row[4]);
                line.setPurDescription(row[5] == null ? null : (String) row[5]);
                line.setProductName(row[6] == null ? null : (String) row[6]);
                line.setVersion(row[7] == null ? null : (String) row[7]);
                line.setLaunches(row[8] == null ? null : (String) row[8]);
                return line;
            }).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public List<ReportLineAnalogStatus> reportAnalogStatus(LocalDate startDate, LocalDate endDate, List<Long> statusIdList) {
        if (startDate == null || endDate == null || CollectionUtils.isEmpty(statusIdList)) {
            return Collections.emptyList();
        }
        String nativeQuery = "select kd_comp.cell, kd_comp.name, kd_comp.description,\n" +
            "        comp.cell pur_cell, comp.name pur_name, comp.description pur_description, UI.NAME_WITH_INITIALS,\n" +
            "        p.product_name, version, approved, accepted, s.descr \n" +
            "        from\n" +
            "        (\n" +
            "            select b_id, kd_comp, analog_comp, status, version, product_id,\n" +
            "            str_agg(approved) approved, str_agg(accepted) accepted\n" +
            "            from\n" +
            "            (\n" +
            "                select distinct b.id b_id, bic_kd.component_id kd_comp,\n" +
            "                bic.component_id analog_comp, bic.status, b.version, b.product_id,\n" +
            "                decode(ba.approve_date, null, null, l.name) approved,\n" +
            "                decode(ba.accept_date, null, null, l.name) accepted \n" +
            "                from\n" +
            "                bom b left outer join bom_attribute ba\n" +
            "                on b.id = ba.bom_id\n" +
            "                left outer join launch l\n" +
            "                on l.id = ba.launch_id,\n" +
            "                bom_item bi, bom_item_component bic,\n" +
            "                bom_item_component bic_kd\n" +
            "                where \n" +
            "                b.id = bi.bom_id\n" +
            "                and bi.id = bic_kd.bom_item_id\n" +
            "                and bi.id = bic.bom_item_id\n" +
            "                and bic_kd.kd = 1\n" +
            "                --and bic.kd = 0\n" +
            "                and bitand(power(2,bic.status), :a_flags) > 0\n" +
            "                and (trunc(B.CREATED) >= :a_from and trunc(B.CREATED) <= :a_to\n" +
            "                or trunc(BA.APPROVE_DATE) >= :a_from and trunc(BA.APPROVE_DATE) <= :a_to)\n" +
            "            )\n" +
            "            group by b_id, kd_comp, analog_comp, status, version, product_id\n" +
            "        ),\n" +
            "        product p left outer join user_info ui\n" +
            "        on P.CONSTRUCTOR_ID = ui.id,\n" +
            "        bom_component kd_comp, \n" +
            "        bom_component comp, t_bic_status s\n" +
            "        where kd_comp = kd_comp.id\n" +
            "        and analog_comp = comp.id\n" +
            "        and p.id = product_id\n" +
            "        and status = s.id\n" +
            "        order by kd_comp.cell";
        return (List<ReportLineAnalogStatus>) entityManager.createNativeQuery(nativeQuery)
            .setParameter("a_flags", statusIdList.stream().mapToInt(el -> (int) Math.pow(2, el)).sum())
            .setParameter("a_from", startDate)
            .setParameter("a_to", endDate)
            .getResultList().stream().map(obj -> {
                Object[] row = (Object[]) obj;
                ReportLineAnalogStatus line = new ReportLineAnalogStatus();
                line.setCell(row[0] == null ? null : (String) row[0]);
                line.setName(row[1] == null ? null : (String) row[1]);
                line.setDescription(row[2] == null ? null : (String) row[2]);
                line.setPurCell(row[3] == null ? null : (String) row[3]);
                line.setPurName(row[4] == null ? null : (String) row[4]);
                line.setPurDescription(row[5] == null ? null : (String) row[5]);
                line.setDeveloper(row[6] == null ? null : (String) row[6]);
                line.setProductName(row[7] == null ? null : (String) row[7]);
                line.setVersion(row[8] == null ? null : (String) row[8]);
                line.setApproved(row[9] == null ? null : (String) row[9]);
                line.setAccepted(row[10] == null ? null : (String) row[10]);
                line.setStatus(row[11] == null ? null : (String) row[11]);
                return line;
            }).collect(Collectors.toList());
    }
}