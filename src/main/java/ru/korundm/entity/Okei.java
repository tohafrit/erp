package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы okeis
 * @author surov_pv
 * Date:   13.03.2018
 */
@Entity
@Table(name = "okeis")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
@org.hibernate.annotations.Immutable
public class Okei implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @Column(name = "code", unique = true, nullable = false)
    private String code; // код ЕИ

    @Column(name = "coefficient", nullable = false)
    private Double coefficient; // коэффициент ЕИ

    @Column(name = "name", nullable = false)
    private String name; // название ЕИ

    @Column(name = "symbol_national", nullable = false)
    private String symbolNational; // национальное обозначение  ЕИ

    @Column(name = "symbol_international", nullable = false)
    private String symbolInternational; // международное обозначение ЕИ

    @Column(name = "code_letter_national", nullable = false)
    private String codeLetterNational; // национальное кодовое буквенное обозначение ЕИ

    @Column(name = "code_letter_international", nullable = false)
    private String codeLetterInternational; // международное кодовое буквенное обозначение ЕИ
}