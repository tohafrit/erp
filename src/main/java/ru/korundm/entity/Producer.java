package ru.korundm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * Сущность с описанием таблицы производителей producers // TODO пренести в сущность Company
 * @author pakhunov_an
 * Date:   26.01.2018
 */
@Entity
@Table(name = "producers")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class Producer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @Size(min = 1, max = 128)
    @Column(name = "name", nullable = false)
    private String name; // наименование

    @OneToMany(mappedBy = "producer", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Equipment> equipmentList = new ArrayList<>();
}