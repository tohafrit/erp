package ru.korundm.schedule.importation.process;

import eco.dao.EcoLotGroupService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.LotGroupService;
import ru.korundm.entity.ContractSection;
import ru.korundm.entity.LotGroup;
import ru.korundm.entity.Product;
import ru.korundm.entity.ServiceType;
import ru.korundm.enumeration.LotGroupKind;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.LOT_GROUP
 * @author zhestkov_an
 * Date:   27.03.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class LotGroupProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private LotGroupService lotGroupService;

    @Autowired
    private EcoLotGroupService ecoLotGroupService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM LotGroup").executeUpdate();
        List<LotGroup> lotGroupList = ecoLotGroupService.getAll().stream().map(ecoLotGroup -> {
            LotGroup lotGroup = new LotGroup();
            lotGroup.setId(ecoLotGroup.getId());
            if (ecoLotGroup.getContractSection() != null) {
                lotGroup.setContractSection(new ContractSection(ecoLotGroup.getContractSection().getId()));
            }
            if (ecoLotGroup.getProduct() != null) {
                Product product = new Product();
                product.setId(ecoLotGroup.getProduct().getId());
                lotGroup.setProduct(product);
            }
            lotGroup.setOrderIndex(ecoLotGroup.getOrderIndex());
            lotGroup.setNote(ecoLotGroup.getNote());
            Long ecoKind = ecoLotGroup.getKind();
            ServiceType type = new ServiceType();
            if (ecoKind == LotGroupKind.MANUFACTURING.getId()) {
                type.setId(1L);
            } else if (ecoKind == LotGroupKind.REPAIRS.getId()) {
                type.setId(8L);
            } else if (ecoKind == LotGroupKind.REWORK.getId()) {
                type.setId(6L);
            }
            lotGroup.setServiceType(type);
            lotGroup.setBom(ecoLotGroup.getBom());
            return lotGroup;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(lotGroupList)) {
            lotGroupService.saveAll(lotGroupList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}