package ru.korundm.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

import java.lang.reflect.InvocationTargetException;

/**
 * Валидатор форм поиска
 * @author pakhunov_an
 * Date:   07.04.2018
 */
@JBossLog
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Deprecated(forRemoval = true)
public final class FormValidatorUtil {

    /**
     * Метод проверки id объекта сущности или самого объекта ({@link Long} или {@link Integer} или {@link String}) формы пришедшей в контроллер.
     * Если id сущности или объект ({@link Long} или {@link Integer} или {@link String}) меньше либо равно нуля или null, то будет возвращен null.
     * @param object объект проверки
     * @return null, если проверка не пройдена
     */
    @Deprecated(forRemoval = true)
    public static <T> T assertFormId(T object) {
        if (object != null) {
            try {
                Long id;
                if (object.getClass().equals(Long.class)) {
                    id = (Long) object;
                } else if (object.getClass().equals(Integer.class)) {
                    id = ((Integer) object).longValue();
                } else if (object.getClass().equals(String.class) && CommonUtil.isLong((String) object)) {
                    id = Long.valueOf((String) object);
                } else {
                    id = (Long) object.getClass().getMethod("getId").invoke(object);
                }
                if (id != null && id > 0) {
                    return object;
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                log.error("Error FormValidatorUtil.assertFormId - " + ex.getMessage());
            }
        }
        return null;
    }

    /**
     * Метод проверки параметра форма на валидность через метод {@link FormValidatorUtil#assertFormId(Object)}
     * при результате null которого вернется true, иначе false
     * @param object объект проверки
     * @return true - объект невалиден, иначе false
     */
    @Deprecated(forRemoval = true)
    public static boolean formIdNotValid(Object object) {
        return assertFormId(object) == null;
    }

    /**
     * Метод проверки параметра форма на валидность через метод {@link FormValidatorUtil#assertFormId(Object)}
     * при результате null которого вернется false, иначе true
     * @param object объект проверки
     * @return true - объект валиден, иначе false
     */
    @Deprecated(forRemoval = true)
    public static boolean formIdValid(Object object) {
        return !formIdNotValid(object);
    }
}