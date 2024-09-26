package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.OnecUser
import ru.korundm.repository.OnecUserRepository

interface OnecUserService : CommonService<OnecUser> {

    fun getByOnecId(onecId: String): OnecUser?
    fun getAllActive(): List<OnecUser>
}

@Service
@Transactional
class OnecUserServiceImpl(
    private val onecUserRepository: OnecUserRepository
) : OnecUserService {
    override fun getAll(): List<OnecUser> = onecUserRepository.findAll()

    override fun getAllById(idList: List<Long>): List<OnecUser> = onecUserRepository.findAllById(idList)

    override fun save(obj: OnecUser) = onecUserRepository.save(obj)

    override fun saveAll(objectList: List<OnecUser>): List<OnecUser> = onecUserRepository.saveAll(objectList)

    override fun read(id: Long): OnecUser? = onecUserRepository.findById(id).orElse(null)

    override fun delete(obj: OnecUser) = onecUserRepository.delete(obj)

    override fun deleteById(id: Long) = onecUserRepository.deleteById(id)

    override fun getByOnecId(onecId: String): OnecUser? = onecUserRepository.findFirstByOnecId(onecId)

    override fun getAllActive(): List<OnecUser> = onecUserRepository.findAllByActiveIsTrue()
}