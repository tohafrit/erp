package ru.korundm.repository.view

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.view.LaunchProductView

interface LaunchProductViewRepository : JpaRepository<LaunchProductView, Long>