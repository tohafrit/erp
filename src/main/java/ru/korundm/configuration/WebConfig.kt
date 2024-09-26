package ru.korundm.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.CacheControl
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.multipart.MultipartResolver
import org.springframework.web.multipart.commons.CommonsMultipartResolver
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.CookieLocaleResolver
import org.springframework.web.servlet.view.InternalResourceViewResolver
import org.springframework.web.servlet.view.JstlView
import ru.korundm.constant.BaseConstant
import ru.korundm.helper.AutowireHelper
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.*

/**
 * Конфигурация для работы WebMvc
 * Date:   08.05.2018
 */
@EnableWebMvc
@Configuration
@ComponentScan("ru.korundm.controller")
class WebConfig : WebMvcConfigurer {

    /*** Регистрация http конвертеров */
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        val jacksonHttpMessageConverter = MappingJackson2HttpMessageConverter()
        jacksonHttpMessageConverter.setPrettyPrint(true)
        converters.add(jacksonHttpMessageConverter)
    }

    /*** Регистрация обработчиков ресурсов */
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler(RESOURCE_HANDLER_PATH)
            .addResourceLocations(RESOURCE_LOCATION_PATH)
            .setCacheControl(CacheControl.maxAge(Duration.ofHours(RESOURCE_CACHE_DURATION)))
    }

    /*** Валидация данных */
    override fun getValidator(): Validator {
        val validator = LocalValidatorFactoryBean()
        validator.setValidationMessageSource(messageSource())
        return validator
    }

    @Bean
    fun viewResolver(): InternalResourceViewResolver {
        val resolver = InternalResourceViewResolver()
        resolver.setViewClass(JstlView::class.java)
        resolver.setExposeContextBeansAsAttributes(true)
        resolver.order = VIEW_RESOLVER_ORDER
        resolver.setPrefix(VIEW_RESOLVER_PREFIX)
        resolver.setSuffix(VIEW_RESOLVER_SUFFIX)
        return resolver
    }

    @Bean
    fun localeResolver(): LocaleResolver {
        val resolver = CookieLocaleResolver()
        resolver.setDefaultLocale(Locale(COUNTRY_CODE.lowercase(), COUNTRY_CODE))
        resolver.cookieName = COOKIE_NAME
        resolver.cookieMaxAge = COOKIE_MAX_AGE
        return resolver
    }

    @Bean
    fun messageSource(): ResourceBundleMessageSource {
        val source = ResourceBundleMessageSource()
        source.setBasenames(*MESSAGE_SOURCE_PATHS)
        source.setDefaultEncoding(StandardCharsets.UTF_8.displayName())
        return source
    }

    /*** Для загрузки файлов и работы атрибута enctype="multipart/form-data" */
    @Bean
    fun multipartResolver(): MultipartResolver {
        val resolver = CommonsMultipartResolver()
        resolver.setMaxUploadSizePerFile(BaseConstant.FILE_SIZE_LIMIT)
        resolver.setDefaultEncoding(StandardCharsets.UTF_8.displayName())
        return resolver
    }

    /*** Метод для получения помощника для аннотации @Autowired/@Inject */
    @Bean
    fun autowireHelper() = AutowireHelper

    companion object {

        const val RESOURCE_HANDLER_PATH = "/resources/**"
        const val RESOURCE_LOCATION_PATH = "/resources/"
        const val RESOURCE_CACHE_DURATION = 1L

        const val VIEW_RESOLVER_ORDER = 1
        const val VIEW_RESOLVER_PREFIX = "/WEB-INF/view/"
        const val VIEW_RESOLVER_SUFFIX = ".jsp"

        const val COUNTRY_CODE = "RU"
        const val COOKIE_NAME = "localeInfo"
        const val COOKIE_MAX_AGE = -1

        val MESSAGE_SOURCE_PATHS = arrayOf("i18/erp", "i18/error", "i18/service", "i18/excel")
    }
}