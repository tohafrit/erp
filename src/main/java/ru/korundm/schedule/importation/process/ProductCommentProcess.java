package ru.korundm.schedule.importation.process;

import eco.dao.EcoProductCommentService;
import eco.entity.EcoUserInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.ProductCommentService;
import ru.korundm.dao.UserService;
import ru.korundm.entity.Product;
import ru.korundm.entity.ProductComment;
import ru.korundm.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.korundm.constant.BaseConstant.SQL_DISABLE_FOREIGN_KEY_CHECKS;
import static ru.korundm.constant.BaseConstant.SQL_ENABLE_FOREIGN_KEY_CHECKS;

/**
 * Процесс переноса ECOPLAN.COMMENT
 * @author zhestkov_an
 * Date:   06.10.2020
 */
@Component
@Scope(SCOPE_SINGLETON)
public class ProductCommentProcess {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProductCommentService productCommentService;

    @Autowired
    private EcoProductCommentService ecoProductCommentService;

    @Autowired
    private UserService userService;

    @Transactional(propagation = REQUIRES_NEW)
    public void schedule() {
        em.createNativeQuery(SQL_DISABLE_FOREIGN_KEY_CHECKS).executeUpdate();
        em.createQuery("DELETE FROM ProductComment").executeUpdate();
        List<ProductComment> productCommentList = ecoProductCommentService.getAll().stream()
            .filter(ecoProductComment -> ecoProductComment.getComment() != null).map(ecoProductComment -> {
            ProductComment productComment = new ProductComment();
            productComment.setId(ecoProductComment.getId());
            if (ecoProductComment.getProduct() != null) {
                Product product = new Product();
                product.setId(ecoProductComment.getProduct().getId());
                productComment.setProduct(product);
            }
            // Автор (кто создал пользовательский комментарий)
            EcoUserInfo user = ecoProductComment.getUser();
            if (user != null) {
                User mUser = userService.findByFirstNameAndLastName(user.getLastName(), user.getFirstName());
                if (mUser == null) mUser = userService.read(1L);
                productComment.setUser(mUser);
            }
            productComment.setCreationDate(ecoProductComment.getCreationDate());
            productComment.setComment(ecoProductComment.getComment());
            return productComment;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(productCommentList)) {
            productCommentService.saveAll(productCommentList);
        }
        em.createNativeQuery(SQL_ENABLE_FOREIGN_KEY_CHECKS).executeUpdate();
    }
}