package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class ShipmentPermissionKind(
    val id: Long,
    val property: String
) : EnumConvertible<Long> {

    NOT_SET(0, "shipmentPermissionKind.notSet"),
    MAIN(1, "shipmentPermissionKind.main"),
    WITH_RETURN_TO_SAFEKEEPING(2, "shipmentPermissionKind.withReturnToSafekeeping"),
    VP_FOR_PRODUCTION(4, "shipmentPermissionKind.vpForProduction"),
    FOLLOWED_BY_PAPERWORK(8, "shipmentPermissionKind.followedByPaperwork"),
    REGULAR_VP(16, "shipmentPermissionKind.regularVp"),
    TRANSFER_TO_PERSONAL_CARD(32, "shipmentPermissionKind.transferToPersonalCard");

    companion object {
        fun getById(id: Long) = values().first { it.id == id }
    }

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<ShipmentPermissionKind, Long>(ShipmentPermissionKind::class.java)
}