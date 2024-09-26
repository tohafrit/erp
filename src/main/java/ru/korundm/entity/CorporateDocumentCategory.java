package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы corporate_document_categories
 * @author zhestkov_an
 * Date:   02.02.2021
 */
@Entity
@Table(name = "corporate_document_categories")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class CorporateDocumentCategory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @Size(min = 1, message = "{common.hibernate.required}")
    @Column(name = "name")
    private String name; // название категории документов

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CorporateDocumentCategory parent; // родительская категория

    @Column(name = "description", length = 512)
    private String description; // описание

    @Column(name = "sort", nullable = false)
    private int sort; // сортировка

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<CorporateDocumentCategory> childList = new ArrayList<>();
}