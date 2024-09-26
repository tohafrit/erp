package ru.korundm.controller.view.corp

import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath

@ViewController([RequestPath.View.Corp.SUBDIVISION])
class SubdivisionViewCorpController {

    @GetMapping("/list")
    fun list() = "corp/include/subdivision/list"
}