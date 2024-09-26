package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.Document

interface DocumentRepository : JpaRepository<Document, Long>