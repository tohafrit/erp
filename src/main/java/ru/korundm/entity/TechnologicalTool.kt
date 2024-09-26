package ru.korundm.entity

import ru.korundm.enumeration.TechnologicalToolType
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "technological_tool")
data class TechnologicalTool(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null, // идентификатор
) {

    @Column(name = "sign", length = 128)
    var sign: String? = null // обозначение

    @Column(name = "name", length = 512, nullable = false)
    var name = "" // наименование

    @Column(name = "appointment", length = 512)
    var appointment: String? = null // назначение

    @Column(name = "link", length = 512)
    var link: String? = null // ссылка на файл

    @Column(name = "state", length = 512)
    var state: String? = null // состояние

    @Column(name = "issue_date")
    var issueDate: LocalDate? = null // дата выпуска

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null // кем выпущен

    @Convert(converter = TechnologicalToolType.CustomConverter::class)
    @Column(name = "type", nullable = false)
    var type: TechnologicalToolType = TechnologicalToolType.TOOLING // тип

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "technological_tool_xref_production_area",
        joinColumns = [JoinColumn(name = "technological_tool_id")],
        inverseJoinColumns = [JoinColumn(name = "production_area_id")]
    )
    var productionAreaList = mutableListOf<ProductionArea>() // список участков
}
