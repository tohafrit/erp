package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.enumeration.SpecificationImportDetailType;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "specification_import_details")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class SpecificationImportDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "rnumber", nullable = false)
    private Integer rowNumber; // номер строки

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bom_id")
    private Bom bom; // версия

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "component_id")
    private Component component; // компонент

    @Convert(converter = SpecificationImportDetailType.CustomConverter.class)
    @Column(name = "type", nullable = false)
    private SpecificationImportDetailType type; // тип

    @Column(name = "description", length = 512, nullable = false)
    private String description; // описание действия
}