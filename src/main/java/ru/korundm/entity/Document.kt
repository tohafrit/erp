package ru.korundm.entity

import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant
import ru.korundm.enumeration.DocumentParentType
import java.time.LocalDateTime
import javax.persistence.*

/**
 * Сущность с описанием таблицы documents
 * @author zhestkov_an
 * Date:   22.03.2021
 */
@Entity
@Table(name = "documents")
data class Document(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "document")
    @GenericGenerator(name = "document", strategy = BaseConstant.GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Convert(converter = DocumentParentType.CustomConverter::class)
    @Column(name = "parent_type")
    var parentType: DocumentParentType? = null // тип владельца (enum DocumentParentType.kt)

    @Column(name = "parent_id")
    var parentId: Long? = null // идентификатор владельца

    @Column(name = "doc_name")
    var docName: String? = null // название документа

    @Column(name = "note")
    var note: String? = null // комментарий к документу

    @Column(name = "file_name")
    var fileName: String? = null // имя файла

    @Column(name = "file_type")
    var fileType: Long? = null // тип файла

    @Column(name = "order_index")
    var orderIndex: Long? = null //

    @Column(name = "modify_date")
    var modifyDate: LocalDateTime? = null //

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modify_user")
    var modifyUser: User? = null //

    @OneToMany(mappedBy = "document")
    var labourProtocolList = mutableListOf<LabourProtocol>()
}