package asu.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы componentabstracts
 * @author mazur_ea
 * Date:   15.07.2019
 */
@Entity
@Table(name = "COMPONENTABSTRACTS")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AsuComponentAbstract implements Serializable {

    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupID")
    private AsuGrpComp group;
}