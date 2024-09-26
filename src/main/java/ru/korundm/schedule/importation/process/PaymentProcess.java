package ru.korundm.schedule.importation.process;

import eco.dao.EcoPaymentService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.PaymentService;
import ru.korundm.entity.Account;
import ru.korundm.entity.ContractSection;
import ru.korundm.entity.Invoice;
import ru.korundm.entity.Payment;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.PAYMENT
 * @author zhestkov_an
 * Date:   24.04.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class PaymentProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EcoPaymentService ecoPaymentService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM Payment").executeUpdate();
        List<Payment> paymentList = ecoPaymentService.getAll().stream().map(ecoPayment -> {
            Payment payment = new Payment();
            payment.setId(ecoPayment.getId());
            if (ecoPayment.getInvoice() != null) {
                payment.setInvoice(new Invoice(ecoPayment.getInvoice().getId()));
            }
            payment.setNumber(ecoPayment.getNumber());
            payment.setDate(ecoPayment.getDate().toLocalDate());
            payment.setAmount(ecoPayment.getAmount());
            payment.setNote(ecoPayment.getNote());
            if (ecoPayment.getAccount() != null) {
                payment.setAccount(new Account(ecoPayment.getAccount().getId()));
            }
            payment.setCode1C(ecoPayment.getCode1C());
            if (ecoPayment.getContractSection() != null) {
                payment.setContractSection(new ContractSection(ecoPayment.getContractSection().getId()));
            }
            payment.setAdvanceInvoiceNumber(ecoPayment.getAdvanceInvoice());
            return payment;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(paymentList)) {
            paymentService.saveAll(paymentList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}