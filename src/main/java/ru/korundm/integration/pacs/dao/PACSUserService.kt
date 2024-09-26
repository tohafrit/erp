package ru.korundm.integration.pacs.dao

import org.springframework.stereotype.Service
import ru.korundm.integration.pacs.entity.PACSUser
import ru.korundm.integration.pacs.repository.PACSUserRepository

@Service
class PACSUserService(
    private val userRepository: PACSUserRepository
) {

    fun getAll(): List<PACSUser> = userRepository.findAll()

    fun getByFullName(name: String, firstName: String, midName: String) = userRepository.findTopByNameAndFirstNameAndMidName(name, firstName, midName)
}