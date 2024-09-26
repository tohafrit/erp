package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.LaborProtectionInstruction

interface LaborProtectionInstructionRepository : JpaRepository<LaborProtectionInstruction, Long>
