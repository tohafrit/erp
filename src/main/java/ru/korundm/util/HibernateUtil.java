package ru.korundm.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import ru.korundm.constant.BaseConstant;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Класс для работы с объектами Hibernate
 * @author pakhunov_an
 * Date:   26.01.2018
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HibernateUtil {

    /**
     * Метод для получения сущности из proxy-сущности
     * @param entity proxy-сущность
     * @return unproxy-сущность
     */
    @SuppressWarnings("unchecked")
    public static <T> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            throw new NullPointerException("Entity passed for initialization is null");
        }
        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
        return entity;
    }

    /**
     * Метод для валидации сущности
     * @param entity сущность
     * @return ошибки валидации
     */
    @Deprecated
    public static <T> Set<ConstraintViolation<T>> entityValidate(T entity) {
        Validator validator = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(
                new ResourceBundleMessageInterpolator(
                    new PlatformResourceBundleLocator(BaseConstant.RESOURCE_BUNDLE_ERROR_MESSAGE)
                )
            )
            .buildValidatorFactory()
            .getValidator();
        return validator.validate(initializeAndUnproxy(entity));
    }
}