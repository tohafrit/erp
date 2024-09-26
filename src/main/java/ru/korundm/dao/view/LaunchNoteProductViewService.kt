package ru.korundm.dao.view

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.view.LaunchNoteProductView
import ru.korundm.repository.view.LaunchNoteProductViewRepository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface LaunchNoteProductViewService : CommonViewService<LaunchNoteProductView>

@Service
@Transactional(readOnly = true)
class LaunchNoteProductViewImpl(
    private val repository: LaunchNoteProductViewRepository
) : LaunchNoteProductViewService {

    private val cl = LaunchNoteProductView::class.java

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun getAll(): List<LaunchNoteProductView> = repository.findAll()

    override fun getAllByIdList(idList: List<Long>): List<LaunchNoteProductView> = repository.findAllById(idList)

    override fun read(id: Long): LaunchNoteProductView = repository.findById(id).orElse(null)
}