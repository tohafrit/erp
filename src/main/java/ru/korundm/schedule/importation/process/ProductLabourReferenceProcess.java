package ru.korundm.schedule.importation.process;

import eco.dao.EcoProductLabourReferenceService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProductLabourReferenceService;
import ru.korundm.entity.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.PRODUCT_LABOUR
 * @author zhestkov_an
 * Date:   01.05.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class ProductLabourReferenceProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProductLabourReferenceService productLabourReferenceService;

    @Autowired
    private EcoProductLabourReferenceService ecoProductLabourReferenceService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM ProductLabourReference").executeUpdate();
        List<ProductLabourReference> productLabourReferenceList = ecoProductLabourReferenceService.getAll().stream().filter(Objects::nonNull).map(ecoPLr -> {
            ProductLabourReference productLabourReference = new ProductLabourReference();
            ProductLabourId id = new ProductLabourId();
            if (ecoPLr.getProduct() != null) {
                Product product = new Product();
                product.setId(ecoPLr.getProduct().getId());
                productLabourReference.setProduct(product);
                id.setProductId(ecoPLr.getProduct().getId());
            }
            if (ecoPLr.getLabour() != null) {
                Labour labour = new Labour();
                labour.setId(ecoPLr.getLabour().getId());
                productLabourReference.setLabour(labour);
            }
            productLabourReference.setLabourTime(ecoPLr.getLabourTime());
            if (ecoPLr.getProductChargesProtocol() != null) {
                ProductChargesProtocol productChargesProtocol = new ProductChargesProtocol();
                productChargesProtocol.setId(ecoPLr.getProductChargesProtocol().getId());
                productLabourReference.setProductChargesProtocol(productChargesProtocol);
                id.setLabourPriceId(ecoPLr.getLabourPrice().getId());
            }
            productLabourReference.setOrderIndex(ecoPLr.getOrderIndex());
            if (ecoPLr.getLabourPrice() != null) {
                LabourPrice labourPrice = new LabourPrice();
                labourPrice.setId(ecoPLr.getLabourPrice().getId());
                productLabourReference.setLabourPrice(labourPrice);
            }
            productLabourReference.setId(id);
            return productLabourReference;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(productLabourReferenceList)) {
            productLabourReferenceService.saveAll(productLabourReferenceList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}