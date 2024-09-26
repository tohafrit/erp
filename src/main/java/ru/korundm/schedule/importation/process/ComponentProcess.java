package ru.korundm.schedule.importation.process;

import eco.dao.EcoBomComponentService;
import eco.entity.EcoBomComponent;
import eco.entity.EcoUnit;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.CompanyService;
import ru.korundm.dao.ComponentService;
import ru.korundm.dao.OkeiService;
import ru.korundm.entity.ComponentCategory;
import ru.korundm.entity.ComponentInstallationType;
import ru.korundm.entity.ComponentKind;
import ru.korundm.entity.Okei;
import ru.korundm.enumeration.ComponentType;
import ru.korundm.constant.BaseConstant;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Процесс заполнения таблицы components
 * @author mazur_ea
 * Date:   29.05.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ComponentProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EcoBomComponentService ecoBomComponentService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private OkeiService okeiService;

    @Autowired
    private CompanyService companyService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM Component").executeUpdate();
        List<ru.korundm.entity.Component> list = ecoBomComponentService.getAll().stream().map(ecoEntity -> {
            ru.korundm.entity.Component entity = new ru.korundm.entity.Component();
            entity.setId(ecoEntity.getId());
            entity.setPosition(NumberUtils.isDigits(ecoEntity.getCell()) ? Integer.parseInt(ecoEntity.getCell()) : null);
            entity.setName(StringUtils.defaultIfEmpty(ecoEntity.getName(), ""));
            if (ecoEntity.getManufacturer() != null) entity.setProducer(companyService.getByName(ecoEntity.getManufacturer()));

            ComponentCategory category = new ComponentCategory();
            category.setId(ecoEntity.getCategory().getId());
            entity.setCategory(category);

            entity.setDescription(ecoEntity.getDescription());

            EcoUnit unit = ecoEntity.getUnit();
            if (unit != null) {
                Okei okei = okeiService.getByCode(unit.getOkei());
                entity.setOkei(okei);
            }

            entity.setProcessed(ecoEntity.getIsProcessed() != null && ecoEntity.getIsProcessed());
            entity.setApproved(!Objects.equals(ecoEntity.getType(), ComponentType.NEW_COMPONENT.getId()));
            entity.setType(ecoEntity.getType());

            if (ecoEntity.getKind() > 0) {
                ComponentKind componentKind = new ComponentKind();
                componentKind.setId(ecoEntity.getKind());
                entity.setKind(componentKind);
            }

            entity.setModifiedDatetime(LocalDateTime.now());
            entity.setPrice(ecoEntity.getPrice());

            entity.setDeliveryTime(ecoEntity.getDeliveryTerms() != null ? ecoEntity.getDeliveryTerms().intValue() : null);
            entity.setDocPath(ecoEntity.getDocPath());
            entity.setPurchaseComponentDate(ecoEntity.getPurchaseCompDate());

            if (!Objects.equals(ecoEntity.getInstallationType(), 0L)) {
                ComponentInstallationType installationType = new ComponentInstallationType();
                installationType.setId(ecoEntity.getInstallationType());
                entity.setInstallation(installationType);
            }

            EcoBomComponent ecoComponentProxy = ecoEntity.getComponentProxy();
            if (ecoComponentProxy != null) {
                ru.korundm.entity.Component substituteComponent = new ru.korundm.entity.Component();
                substituteComponent.setId(ecoComponentProxy.getId());
                entity.setSubstituteComponent(substituteComponent);
            }

            EcoBomComponent ecoPurchaseComponent = ecoEntity.getPurchaseComponent();
            if (ecoPurchaseComponent != null) {
                ru.korundm.entity.Component purchaseComponent = new ru.korundm.entity.Component();
                purchaseComponent.setId(ecoPurchaseComponent.getId());
                entity.setPurchaseComponent(purchaseComponent);
            }
            return entity;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(list)) {
            componentService.saveAll(list);
        }
        em.createNativeQuery(BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}