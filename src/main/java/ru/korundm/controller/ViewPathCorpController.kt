package ru.korundm.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import ru.korundm.constant.RequestPath

@Controller
@RequestMapping(RequestPath.CORP)
class ViewPathCorpController {

    @GetMapping("/index")
    fun index() = "corp/index"

    @GetMapping("/news", "/news/**")
    fun news() = "corp/section/news"

    @GetMapping("/news-edition")
    fun newsEdition() = "corp/section/newsEdition"

    @GetMapping("/company-detail")
    fun companyDetail() = "corp/section/companyDetail"

    @GetMapping("/office-supply")
    fun officeSupply() = "corp/section/officeSupply"

    @GetMapping("/trip")
    fun trip() = "corp/section/trip"

    @GetMapping("/gratitude")
    fun gratitude() = "corp/section/gratitude"

    @GetMapping("/corporate-document-category")
    fun corporateDocumentCategory() = "corp/section/corporateDocumentCategory"

    @GetMapping("/message-type")
    fun messageType() = "corp/section/messageType"

    @GetMapping("/message-template")
    fun messageTemplate() = "corp/section/messageTemplate"

    @GetMapping("/administration-office-demand")
    fun administrationOfficeDemand(model: ModelMap): String {
        // TODO позже переписать используя отдел, пока в демонстрационных целях поставлять вручную ИСТИНА или ЛОЖЬ
        model.addAttribute("isAho", true)
        return "corp/section/administrationOfficeDemand"
    }

    @GetMapping("/subdivision")
    fun subdivision(model: ModelMap) = "/corp/section/subdivision"
}