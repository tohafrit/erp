@file:Suppress("SpringMVCViewInspection")

package ru.korundm.controller

import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import ru.korundm.dao.FileStorageService
import ru.korundm.exception.AjaxAuthorizeException
import ru.korundm.util.FileStorageUtil.decodeURLHash
import ru.korundm.util.FileStorageUtil.download
import ru.korundm.util.KtCommonUtil.isAjax
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class BaseController(
    private val messageSource: MessageSource,
    private val fileStorageService: FileStorageService
) {

    @GetMapping("/index")
    fun index() = "index"

    /**
     * TODO посмотреть возможность обхода данной проблемы
     *  При устаревшей сессии после авторизации пользователя перебрасывает на страницу j_security_check.
     *  Она не обрабатывается Spring, при повторной отправке обрабатывается
     */
    @RequestMapping("/j_security_check")
    fun escapeSecurityCheckPage() = "redirect:index"

    @GetMapping("/error")
    fun error(response: HttpServletResponse) = if (response.status == 404) "redirect:not-found" else "redirect:index"

    @GetMapping("/not-found")
    fun notFound() = "notFound"

    @RequestMapping("/auth")
    fun auth(
        request: HttpServletRequest,
        redirectAttributes: RedirectAttributes,
        @RequestParam(required = false) error: String?,
        @RequestParam(required = false) logout: String?
    ): String {
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("auth.usernameOrPassword", arrayOf(), request.locale))
            return "redirect:index"
        }
        if (logout != null) {
            try {
                request.logout()
            } catch (ignore: ServletException) {}
            return "redirect:index"
        }
        // Если попадаем в эту точку, то имеем ajax запрос при таймауте авторизации - необходимо сделать редирект из ajax в страницу авторизации
        if (request.isAjax()) throw AjaxAuthorizeException()
        return "auth"
    }

    @GetMapping("/download-file/{urlHash}")
    fun downloadFile(response: HttpServletResponse, @PathVariable urlHash: String) = fileStorageService.read(decodeURLHash(urlHash))?.run { download(response, this) }
}