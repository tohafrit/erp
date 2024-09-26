package ru.korundm.enumeration;

import lombok.Getter;

/**
 * Типы поисков
 * @author surov_pv
 * Date:   11.03.2019
 */
@Getter
public enum ContractSearchType {

    /** По номеру договора */
    NUMBER("number", "contract.search.form.byNumber"),

    /** По внешнему номеру */
    EXTERNAL_NUMBER("externalNumber","contract.search.form.byExternalNumber"),

    /** По заказчику */
    CUSTOMER("customer", "contract.search.form.byCustomer"),

    /** По номеру счета */
    NUMBER_ACCOUNT("numberAccount", "contract.search.form.byNumberAccount"),

    /** По изделию */
    PRODUCT("product", "contract.search.form.byProduct");

    private final String value; // значение

    private final String localKey; // ключ названия для отображения

    ContractSearchType(String value, String localKey) {
        this.value = value;
        this.localKey = localKey;
    }
}