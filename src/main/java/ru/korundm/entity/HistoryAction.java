package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием типов исторических действий
 * @author mazur_ea
 * Date:   14.10.2019
 */
@Entity
@Table(name = "history_actions")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class HistoryAction implements Serializable {

    /** Тип вставки */
    public final String INSERT = "insert";
    /** Тип обновления */
    public final String UPDATE = "update";
    /** Тип удаления */
    public final String DELETE = "delete";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "type", length = 8, nullable = false, unique = true)
    private String type; // тип
}