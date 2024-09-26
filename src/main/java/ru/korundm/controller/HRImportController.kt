package ru.korundm.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.dao.OnecUserService
import ru.korundm.dao.SubdivisionService
import ru.korundm.entity.OnecUser
import ru.korundm.entity.Subdivision
import ru.korundm.integration.onec.hr.dao.CommonServiceImpl

@Controller
class HRImportController(
    private val commonServiceImpl: CommonServiceImpl,
    private val subdivisionService: SubdivisionService,
    private val onecUserService: OnecUserService
) {
    @GetMapping("/importSubdivision")
    fun importSubdivision(): String {
        val subdivisionList = commonServiceImpl.getSubdivisionList()?.map { subdivision ->
                (subdivisionService.getByOnecId(subdivision.id) ?: Subdivision()).apply {
                    name = subdivision.description
                    onecId = subdivision.id
                    sort = subdivision.sort
                    formed = subdivision.formed
                    disbanded = subdivision.disbanded
                    parentId = subdivision.parent
                }
        }?.toList()
        subdivisionService.saveAll(subdivisionList)
        return "redirect:/corp/subdivision"
    }

    @GetMapping("/importUser")
    fun importUser(): String {
        val userList = commonServiceImpl.getUserList()?.map { user ->
            (onecUserService.getByOnecId(user.id) ?: OnecUser()).apply {
                onecId = user.id
                code = user.code
                name = user.individual.name
                surname = user.individual.surname
                patronymic = user.individual.patronymic
                birthday = user.individual.birthday
                active = !user.active
            }
        }?.toList()
        onecUserService.saveAll(userList)
        return "redirect:/corp/index"
    }
}