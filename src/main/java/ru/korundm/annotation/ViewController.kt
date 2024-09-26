package ru.korundm.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * Аннотация для метки контроллеров представлений
 * @author mazur_ea
 * Date:   09.11.2020
 */
@Controller
@RequestMapping
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
annotation class ViewController(

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
    val produces: Array<String> = []
)