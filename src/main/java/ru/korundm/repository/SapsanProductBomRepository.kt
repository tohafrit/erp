package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.SapsanProductBom

interface SapsanProductBomRepository : JpaRepository<SapsanProductBom, Long>