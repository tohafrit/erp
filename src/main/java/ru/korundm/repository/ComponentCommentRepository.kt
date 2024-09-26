package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ComponentComment

interface ComponentCommentRepository : JpaRepository<ComponentComment, Long>