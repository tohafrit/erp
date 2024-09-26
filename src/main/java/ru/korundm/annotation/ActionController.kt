package ru.korundm.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Аннотация для метки контроллеров действий
 * @author mazur_ea
 * Date:   09.11.2020
 */
@RestController
@RequestMapping
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
annotation class ActionController(

    @get: AliasFor(attribute = "path", annotation = RequestMapping::class)
    val value: Array<String> = [],

    @get: AliasFor(annotation = RequestMapping::class)
    val name: String = "",

    @get: AliasFor(attribute = "value", annotation = RequestMapping::class)
    val path: Array<String> = [],

    @get: AliasFor(annotation = RequestMapping::class)
    val method: Array<RequestMethod> = [],

    @get: AliasFor(annotation = RequestMapping::class)
    val params: Array<String> = [],

    @get: AliasFor(annotation = RequestMapping::class)
    val headers: Array<String> = [],

    @get: AliasFor(annotation = RequestMapping::class)
    val consumes: Array<String> = [],

    @get: AliasFor(annotation = RequestMapping::class)
    val produces: Array<String> = [MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE]
)