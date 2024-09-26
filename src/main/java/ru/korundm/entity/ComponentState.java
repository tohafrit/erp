package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "component_states")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class ComponentState implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "code", unique = true, length = 128, nullable = false)
    private String code; // код состояния

    @Column(name = "state", length = 128, nullable = false)
    private String state; // состояние
}
