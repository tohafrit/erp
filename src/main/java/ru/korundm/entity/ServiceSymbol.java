package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы хранения функциональностей перехода service_symbols
 * @author mazur_ea
 * Date:   28.06.2018
 */
@Entity
@Table(name = "service_symbol")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class ServiceSymbol implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "name", length = 16, nullable = false)
    private String name; // обозначение

    @Column(name = "code", length = 16, nullable = false)
    private String code; // кодовое представление обозначения

    @Column(name = "technological_process", nullable = false)
    private boolean technologicalProcess; // использование в техпроцессах

    @Column(name = "operation_card", nullable = false)
    private boolean operationCard; // использование в операционных картах

    @Column(name = "route_map", nullable = false)
    private boolean routeMap; // использование в маршрутных картах

    @Column(name = "description", length = 512, nullable = false)
    private String description; // описание

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}