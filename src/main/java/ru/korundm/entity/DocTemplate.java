package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.enumeration.DocTemplateKey;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность для описания таблицы для хранения ссылок на файлы шаблонов
 */
@Entity
@Table(name = "doc_templates")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class DocTemplate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Enumerated(EnumType.STRING)
    @Column(name = "key", updatable = false, nullable = false, length = 64)
    private DocTemplateKey key; // уникальный ключ шаблона для связи с системой

    @Column(name = "name", nullable = false, length = 128)
    private String name; // название файла при выгрузке шаблона пользователем

    @ManyToMany
    @JoinTable(
        name = "doc_template_xref_tag",
        joinColumns = @JoinColumn(name = "template_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<DocTemplateTag> tagsList = new ArrayList<>(); // список тегов шаблона
}
