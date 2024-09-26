package ru.korundm.integration.pacs.dao

import org.springframework.stereotype.Service
import ru.korundm.integration.pacs.entity.PACSAccessPoint
import ru.korundm.integration.pacs.repository.PACSAccessPointRepository

@Service
class PACSAccessPointService(
    private val accessPointRepository: PACSAccessPointRepository
) {

    fun getAll(): List<PACSAccessPoint> = accessPointRepository.findAll()

    fun getAllByDoorIndex(doorIndex: Int) = accessPointRepository.findAllByDoorIndex(doorIndex)
}