package ru.korundm.schedule.importation.process;

import eco.dao.EcoProductChargesProtocolService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProductChargesProtocolService;
import ru.korundm.entity.Company;
import ru.korundm.entity.Product;
import ru.korundm.entity.ProductChargesProtocol;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.PRODUCT_CHARGES_PROTOCOL
 * @author zhestkov_an
 * Date:   13.04.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class ProductChargesProtocolProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProductChargesProtocolService productChargesProtocolService;

    @Autowired
    private EcoProductChargesProtocolService ecoProductChargesProtocolService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM ProductChargesProtocol").executeUpdate();
        List<ProductChargesProtocol> productChargesProtocolList = ecoProductChargesProtocolService.getAll().stream().map(ecoProductChargesProtocol -> {
            ProductChargesProtocol productChargesProtocol = new ProductChargesProtocol();
            productChargesProtocol.setId(ecoProductChargesProtocol.getId());
            productChargesProtocol.setProtocolNumber(ecoProductChargesProtocol.getProtocolNumber());
            productChargesProtocol.setProtocolDate(ecoProductChargesProtocol.getProtocolDate());
            productChargesProtocol.setProtocolNote(ecoProductChargesProtocol.getProtocolNote());
            productChargesProtocol.setPrice(ecoProductChargesProtocol.getPrice());
            productChargesProtocol.setPriceUnpack(ecoProductChargesProtocol.getPriceUnpack());
            productChargesProtocol.setMaterial(ecoProductChargesProtocol.getMaterial());
            productChargesProtocol.setAddMaterial(ecoProductChargesProtocol.getAddMaterial());
            productChargesProtocol.setHalfUnit(ecoProductChargesProtocol.getHalfUnit());
            productChargesProtocol.setRemainder(ecoProductChargesProtocol.getRemainder());
            productChargesProtocol.setSpecialEquipCharges(ecoProductChargesProtocol.getSpecialEquipCharges());
            productChargesProtocol.setComponentsOwn(ecoProductChargesProtocol.getComponentsOwn());
            productChargesProtocol.setPurchasedComponent(ecoProductChargesProtocol.getPurchasedComponent());
            productChargesProtocol.setPack(ecoProductChargesProtocol.getPack());
            productChargesProtocol.setLaunchCostEQ(ecoProductChargesProtocol.getLaunchCostEq());
            productChargesProtocol.setLaunchCostProd(ecoProductChargesProtocol.getLaunchCostProd());
            productChargesProtocol.setGearCost(ecoProductChargesProtocol.getGearCost());
            productChargesProtocol.setEnergy(ecoProductChargesProtocol.getEnergy());
            productChargesProtocol.setFuel(ecoProductChargesProtocol.getFuel());
            productChargesProtocol.setTransport(ecoProductChargesProtocol.getTransport());
            productChargesProtocol.setPartnerCharges(ecoProductChargesProtocol.getPartnerCharges());
            /*if (ecoProductChargesProtocol.getDocument() != null) {
                Document document = new Document();
                document.setId(ecoProductChargesProtocol.getDocument().getId());
                productChargesProtocol.setDocument(document);
            }*/
            if (ecoProductChargesProtocol.getProduct() != null) {
                productChargesProtocol.setProduct(new Product(ecoProductChargesProtocol.getProduct().getId()));
            }
            if (ecoProductChargesProtocol.getCompany() != null) {
                Company company = new Company();
                company.setId(ecoProductChargesProtocol.getCompany().getId());
                productChargesProtocol.setCompany(company);
            }
            return productChargesProtocol;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(productChargesProtocolList)) {
            productChargesProtocolService.saveAll(productChargesProtocolList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}