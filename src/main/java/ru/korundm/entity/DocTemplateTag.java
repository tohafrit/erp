package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.enumeration.DocTemplateTagKey;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность для описания таблицы для хранения тегов для их замены в шаблонах
 */
@Entity
@Table(name = "doc_template_tags")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class DocTemplateTag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Enumerated(EnumType.STRING)
    @Column(name = "key", updatable = false, nullable = false, length = 64)
    private DocTemplateTagKey key; // уникальный ключ тега для связки с системой и для использования в шаблонах

    @Column(name = "value", length = 512)
    private String value; // статичное значение тега для подстановки

    @ManyToMany(mappedBy = "tagsList", cascade = CascadeType.ALL)
    private List<DocTemplate> templatesList = new ArrayList<>(); // список шаблонов, использующих тег
}
