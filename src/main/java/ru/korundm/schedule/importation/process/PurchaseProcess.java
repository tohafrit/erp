package ru.korundm.schedule.importation.process;

import eco.dao.EcoPurchaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.LaunchService;
import ru.korundm.dao.PurchaseService;
import ru.korundm.dao.UserService;
import ru.korundm.entity.Launch;
import ru.korundm.entity.Purchase;
import ru.korundm.constant.BaseConstant;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Процесс переноса PROC_PARAM
 * @author pakhunov_an
 * Date:   07.02.2020
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PurchaseProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private EcoPurchaseService ecoPurchaseService;

    @Autowired
    private LaunchService launchService;

    @Autowired
    private UserService userService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        purchaseService.deleteAll();
        LocalDateTime now = LocalDateTime.now();
        List<Purchase> purchaseList = ecoPurchaseService.getAll().stream().map(ecoPurchase -> {
            Purchase purchase = new Purchase();
            purchase.setId(ecoPurchase.getId());
            purchase.setName(ecoPurchase.getName());
            purchase.setType(ecoPurchase.getType());
            purchase.setNote(ecoPurchase.getNote());
            purchase.setPlanDate(ecoPurchase.getPlanDate());
            purchase.setLaunch(ecoPurchase.getLaunch() != null ? launchService.read(ecoPurchase.getLaunch().getId()) : null);
            List<Launch> launchList = ecoPurchase.getPurchaseLaunchList().stream().map(purchaseLaunch -> {
                Launch launch = new Launch();
                launch.setId(purchaseLaunch.getId());
                return launch;
            }).collect(Collectors.toList());
            purchase.setPurchaseLaunchList(launchList);
            return purchase;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(purchaseList)) {
            purchaseService.saveAll(purchaseList);
        }
    }
}