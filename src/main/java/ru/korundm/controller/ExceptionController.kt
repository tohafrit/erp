package ru.korundm.controller

import org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage
import org.apache.commons.lang3.exception.ExceptionUtils.getStackFrames
import org.hibernate.exception.ConstraintViolationException
import org.jboss.logging.Logger
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.ModelAndView
import ru.korundm.exception.AjaxAuthorizeException
import ru.korundm.exception.AlertUIException
import ru.korundm.util.KtCommonUtil.isAjax
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletRequest

/**
 * Контроллер для перехвата исключений
 * @author mazur_ea
 * Date:   16.04.2020
 */
@ControllerAdvice
class ExceptionController {

    private val log: Logger = Logger.getLogger(ExceptionController::class.java)

    class AjaxAlert(val message: String = "", val title: String = "", val trace: Array<String> = emptyArray())

    @ExceptionHandler(Exception::class)
    fun handle(exc: Exception, request: HttpServletRequest): Any {
        log.error(exc)
        return if (request.isAjax()) ResponseEntity(AjaxAlert(getRootCauseMessage(exc), "Internal server error", getStackFrames(exc)), HttpHeaders(), INTERNAL_SERVER_ERROR)
        else ModelAndView("error", INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(AlertUIException::class)
    fun handle(exc: AlertUIException, request: HttpServletRequest) =
        ResponseEntity(if (request.isAjax()) AjaxAlert(exc.message, exc.title) else null, HttpHeaders(), NOT_ACCEPTABLE)

    @ExceptionHandler(AjaxAuthorizeException::class)
    fun handleAjaxAuthorizeException() = ResponseEntity<Nothing>(UNAUTHORIZED)

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(exc: DataIntegrityViolationException, request: HttpServletRequest) = if (request.isAjax()) {
        log.error(exc)
        val msg = when (exc.cause) {
            is ConstraintViolationException -> "Невозможно выполнить операцию, поскольку она нарушает состояние зависимых данных"
            else -> "Невозможно выполнить операцию, поскольку система обнаружила нарушение целостности данных"
        }
        ResponseEntity(AjaxAlert(msg, "", getStackFrames(exc)), HttpHeaders(), NOT_ACCEPTABLE)
    } else null

    @ExceptionHandler(JpaObjectRetrievalFailureException::class)
    fun handleJpaObjectRetrievalFailureException(exc: EntityNotFoundException, request: HttpServletRequest) = if (request.isAjax()) {
        log.error(exc)
        ResponseEntity(AjaxAlert("Невозможно выполнить операцию, поскольку система обнаружила ошибку получения данных хранения", "", getStackFrames(exc)), HttpHeaders(), NOT_ACCEPTABLE)
    } else null
}