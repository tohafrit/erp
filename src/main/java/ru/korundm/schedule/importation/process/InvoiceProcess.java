package ru.korundm.schedule.importation.process;

import eco.dao.EcoInvoiceService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.InvoiceService;
import ru.korundm.entity.ContractSection;
import ru.korundm.entity.Invoice;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.INVOICE
 * @author zhestkov_an
 * Date:   24.04.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class InvoiceProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private EcoInvoiceService ecoInvoiceService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM Invoice").executeUpdate();
        List<Invoice> invoiceList = ecoInvoiceService.getAll().stream().map(ecoInvoice -> {
            Invoice invoice = new Invoice();
            invoice.setId(ecoInvoice.getId());
            invoice.setContractSection(new ContractSection(ecoInvoice.getContractSection().getId()));
            invoice.setNumber(ecoInvoice.getInvoiceNumber());
            invoice.setCreatedDate(ecoInvoice.getInvoiceDate().toLocalDate());
            invoice.setPrice(ecoInvoice.getAmount());
            invoice.setNote(ecoInvoice.getNote());
            /*if (ecoInvoice.getDocument() != null) {
                Document document = new Document();
                document.setId(ecoInvoice.getDocument().getId());
                invoice.setDocument(document);
            }*/
            invoice.setStatus(ecoInvoice.getInvoiceStatus());
            invoice.setType(ecoInvoice.getInvoiceType());
            invoice.setDateValidBefore(ecoInvoice.getGoodThroughDate().toLocalDate());
            invoice.setPaid(ecoInvoice.getPaidAmount());
            return invoice;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(invoiceList)) {
            invoiceService.saveAll(invoiceList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}