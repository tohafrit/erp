package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.AppSettings
import ru.korundm.enumeration.AppSettingsAttr

interface AppSettingsRepository : JpaRepository<AppSettings, Long> {

   fun findFirstByAttr(attr: AppSettingsAttr): AppSettings?
}