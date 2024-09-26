package asu.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы componentnames
 * @author mazur_ea
 * Date:   15.07.2019
 */
@Entity
@Table(name = "COMPONENTNAMES")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuComponentName implements Serializable {

    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compID")
    private AsuComponent component;

    @Column(name = "analog")
    private Long analog;

    @Column(name = "moduleID")
    private Long moduleId;

    @Column(name = "name")
    private String name;

    @Column(name = "firmID")
    private Long firmId;

    @Column(name = "crUserID")
    private Long crUserID;

    @Column(name = "tcr")
    private Long tcr;

    @Column(name = "lmUserID")
    private Long lmUserId;

    @Column(name = "tlm")
    private Long tlm;
}