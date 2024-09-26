package ru.korundm.integration.pacs.dao

import org.springframework.stereotype.Service
import ru.korundm.integration.pacs.entity.PACSPassage
import ru.korundm.integration.pacs.entity.PACSUser
import ru.korundm.integration.pacs.repository.PACSPassageRepository
import java.time.LocalDateTime

@Service
class PACSPassageService(
    private val passageRepository: PACSPassageRepository
) {

    fun getAll(): List<PACSPassage> = passageRepository.findAll()

    fun lastEnter(user: PACSUser, time: LocalDateTime): PACSPassage? = passageRepository.findTopByUserAndTimeAfterOrderByTimeDesc(user, time)
}