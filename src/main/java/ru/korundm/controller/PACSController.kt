package ru.korundm.controller

import org.springframework.web.bind.annotation.RestController
import ru.korundm.integration.pacs.dao.PACSAccessPointService
import ru.korundm.integration.pacs.dao.PACSUserService

@RestController
class PACSController(
    private val accessPointService: PACSAccessPointService,
    private val passageService: PACSAccessPointService,
    private val userService: PACSUserService
)