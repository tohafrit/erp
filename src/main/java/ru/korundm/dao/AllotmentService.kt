package ru.korundm.dao

import ru.korundm.entity.Allotment
import ru.korundm.entity.Lot
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrResultQuery

interface AllotmentService : CommonService<Allotment> {

    fun getAllForUnapprovedLaunch(productId: Long?): List<Allotment>?
    fun getAllForApprovedLaunch(launchProductId: Long?): List<Allotment>?
    fun deleteAll(allotmentList: List<Allotment>)
    fun getCountAllByLot(lot: Lot): Int
    fun getAllByMatValueListLetterId(letterId: Long?): List<Allotment>
    fun findTableDataByContractSection(tableInput: TabrIn, sectionId: Long?): TabrResultQuery<Allotment>
    fun getAllByShipmentWaybillId(id: Long?): List<Allotment>
}