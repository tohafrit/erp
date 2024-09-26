package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы видов компонентов component_installation_types (ex. ECOPLAN.T_INSTALLATION_TYPE)
 * @author mazur_ea
 * Date: 26.04.2019
 */
@Entity
@Table(name = "component_installation_types")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class ComponentInstallationType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @Column(name = "name", unique = true, nullable = false)
    private String name; // наименование
}