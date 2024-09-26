package ru.korundm.dto.launch;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter @Setter @ToString
@EqualsAndHashCode(of = "launchProductId")
public final class LaunchPlanning implements Serializable {

    private Long launchProductId;
    private String version; // версия
    private Long residueB1; // остаток Б1 : view V_LAUNCHABLE_PRODUCT_CALC поле UNALLOTED
    private Long residueReserve; // остаток задела : view V_LAUNCHABLE_PRODUCT_CALC поле REMAIN_RESERVE
    private Long residueReserveOtherProducts; // задел, использованный в составе других изделий : view V_LAUNCHABLE_PRODUCT_CALC поле REMAIN_RESERVE_IN_OTHER
    private Long unalloted; // претендетны : view V_LAUNCHABLE_PRODUCT_CALC поле UNLAUNCHED
    private Long usedForOther; // запускается в составе других изделий : view V_UNAPPROVED_LAUNCH_LP поле USED_FOR_OTHER
    private Long amountToLaunch; // итого к запуску : view V_UNAPPROVED_LAUNCH_LP поле AMOUNT_TO_LAUNCH
    private Long fromReserveForSale; // для использование заделов предыдущих запусков : view V_UNAPPROVED_LAUNCH_LP поле FROM_RESERVE_FOR_SALE
    private Long fromReserveForOther; // для использование заделов предыдущих запусков : view V_UNAPPROVED_LAUNCH_LP поле FROM_RESERVE_FOR_OTHER
    private Long fromReserveInOther; // для использование заделов предыдущих запусков : view V_UNAPPROVED_LAUNCH_LP поле FROM_RESERVE_IN_OTHER
    private Long amountToBuy; // итого к закупке : view V_UNAPPROVED_LAUNCH_LP поле AMOUNT_TO_BUY
}