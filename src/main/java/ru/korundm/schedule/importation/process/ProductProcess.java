package ru.korundm.schedule.importation.process;

import eco.dao.EcoProductService;
import eco.entity.EcoUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.UserService;
import ru.korundm.entity.ClassificationGroup;
import ru.korundm.entity.ProductLetter;
import ru.korundm.entity.Product;
import ru.korundm.entity.ProductType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.PRODUCT
 * @author mazur_ea
 * Date:   28.02.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class ProductProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EcoProductService ecoProductService;

    @Autowired
    private UserService userService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM Product").executeUpdate();
        ecoProductService.getAll().forEach(ecoProduct -> {
            Product product = new Product();
            product.setSerial(true);
            product.setId(ecoProduct.getId());
            product.setConditionalName(ecoProduct.getProductName());
            product.setTechSpecName(ecoProduct.getFullName());
            product.setProductionName(ecoProduct.getProductionName());
            product.setDecimalNumber(ecoProduct.getDNumber());
            product.setArchiveDate(ecoProduct.getArchiveDate() == null ? null : ecoProduct.getArchiveDate().atStartOfDay());
            product.setPosition(ecoProduct.getPosition() == null ? null : Integer.valueOf(ecoProduct.getPosition()));
            product.setComment(ecoProduct.getNote());
            product.setPrice(ecoProduct.getPrice());
            product.setExportPrice(ecoProduct.getExportPrice());

            // Краткая тех. хар-ка (в эко всегда заполнен)
            Long ecoProductTypeId = ecoProduct.getProductType().getId();
            ProductType type = new ProductType();
            if (ecoProductTypeId.equals(4L)) {
                type.setId(3L);
            } else if (ecoProductTypeId.equals(8L)) {
                type.setId(4L);
            } else if (ecoProductTypeId.equals(16L)) {
                type.setId(5L);
            } else if (ecoProductTypeId.equals(32L)) {
                product.setSerial(false);
                type.setId(6L);
            } else if (ecoProductTypeId.equals(128L)) {
                type.setId(7L);
            } else if (ecoProductTypeId.equals(256L)) {
                type.setId(8L);
            } else if (ecoProductTypeId.equals(512L)) {
                type.setId(9L);
            } else if (ecoProductTypeId.equals(1024L)) {
                type.setId(10L);
            } else if (ecoProductTypeId.equals(8192L)) {
                type.setId(11L);
            } else if (ecoProductTypeId.equals(2048L)) {
                type.setId(12L);
            } else {
                type.setId(ecoProductTypeId);
            }
            product.setType(type);

            // Литера
            Long testStatusId = ecoProduct.getTestStatus();
            if (testStatusId != null) {
                ProductLetter productLetter = new ProductLetter();
                if (testStatusId.equals(4L)) {
                    productLetter.setId(3L);
                } else {
                    productLetter.setId(testStatusId);
                }
                product.setLetter(productLetter);
            }

            // Ведущий
            EcoUserInfo constructor = ecoProduct.getConstructor();
            if (constructor != null) {
                // В базе ECO Еремин указан с именем "А" и отчеством "А"
                if (StringUtils.isNotBlank(constructor.getLastName()) && constructor.getLastName().equals("Еремин")) {
                    product.setLead(userService.read(constructor.getId()));
                } else {
                    product.setLead(userService.findByFirstNameAndLastName(constructor.getLastName(), constructor.getFirstName()));
                }
            }

            // Классификационная группа
            Long classGroupId = ecoProduct.getClassGroup();
            if (classGroupId != null) {
                Long productClassGroupId = null;
                if (classGroupId == 4001104 || classGroupId == 3994876) {
                    productClassGroupId = 1L;
                } else if (classGroupId == 4001106 || classGroupId == 3994878) {
                    productClassGroupId = 2L;
                } else if (classGroupId == 4001107 || classGroupId == 3994880) {
                    productClassGroupId = 3L;
                } else if (classGroupId == 4001108 || classGroupId == 3994882) {
                    productClassGroupId = 4L;
                } else if (classGroupId == 4001109 || classGroupId == 3994897) {
                    productClassGroupId = 5L;
                } else if (classGroupId == 4001110 || classGroupId == 3994883) {
                    productClassGroupId = 6L;
                } else if (classGroupId == 4001111 || classGroupId == 3994884) {
                    productClassGroupId = 7L;
                } else if (classGroupId == 4001112 || classGroupId == 3994885) {
                    productClassGroupId = 8L;
                } else if (classGroupId == 3994886) {
                    productClassGroupId = 9L;
                } else if (classGroupId == 3994889) {
                    productClassGroupId = 10L;
                } else if (classGroupId == 3994893) {
                    productClassGroupId = 11L;
                } else if (classGroupId == 3994874) {
                    productClassGroupId = 12L;
                } else if (classGroupId == 3990871) {
                    productClassGroupId = 13L;
                } else if (classGroupId == 3994895) {
                    productClassGroupId = 14L;
                } else if (classGroupId == 3994896) {
                    productClassGroupId = 15L;
                }
                if (productClassGroupId != null) {
                    ClassificationGroup classificationGroup = new ClassificationGroup();
                    classificationGroup.setId(productClassGroupId);
                    product.setClassificationGroup(classificationGroup);
                }
            }

            product.setExamAct(ecoProduct.getPeriodicalExamAct());
            product.setExamActDate(ecoProduct.getPeriodicalExamActDate());
            product.setFamilyDecimalNumber(ecoProduct.getFamilyDnumber());
            product.setSuffix(ecoProduct.getSuffix());
            product.setTemplatePath(ecoProduct.getTemplatePath());

            em.createNativeQuery("INSERT INTO products(id, ver, conditional_name, tech_spec_name, production_name, type_id, decimal_number, letter_id, archive_date, position, lead_id, classification_group_id, comment, price, export_price, exam_act, exam_act_date, family_decimal_number, suffix, template_path, serial) " +
                "VALUES (:id, 0, :conditionalName, :techSpecName, :productionName, :typeId, :decimalNumber, :letterId, :archiveDate, :position, :leadId, :classificationGroupId, :comment, :price, :exportPrice, :examAct, :examActDate, :familyDecimalNumber, :suffix, :templatePath, :serial)")
                .setParameter("id", product.getId())
                .setParameter("conditionalName", product.getConditionalName())
                .setParameter("techSpecName", product.getTechSpecName())
                .setParameter("productionName", product.getProductionName())
                .setParameter("typeId", product.getType().getId())
                .setParameter("decimalNumber", product.getDecimalNumber())
                .setParameter("letterId", product.getLetter() == null ? null : product.getLetter().getId())
                .setParameter("archiveDate", product.getArchiveDate())
                .setParameter("position", product.getPosition())
                .setParameter("leadId", product.getLead() == null ? null : product.getLead().getId())
                .setParameter("classificationGroupId", product.getClassificationGroup() == null ? null : product.getClassificationGroup().getId())
                .setParameter("comment", product.getComment())
                .setParameter("price", product.getPrice())
                .setParameter("exportPrice", product.getExportPrice())
                .setParameter("examAct", product.getExamAct())
                .setParameter("examActDate", product.getExamActDate())
                .setParameter("familyDecimalNumber", product.getFamilyDecimalNumber())
                .setParameter("suffix", product.getSuffix())
                .setParameter("templatePath", product.getTemplatePath())
                .setParameter("serial", product.getSerial())
                .executeUpdate();
        });
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}