package asu.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы components
 * @author mazur_ea
 * Date:   15.07.2019
 */
@Entity
@Table(name = "COMPONENTS")
@ToString
@Setter @Getter
@EqualsAndHashCode(callSuper = true)
public class AsuComponent extends AsuComponentAbstract implements Serializable {

    @Column(name = "compTypeID")
    private Long compTypeId;

    @Column(name = "pos")
    private Long pos;

    @Column(name = "insuranceStock")
    private Long insuranceStock;

    @Column(name = "hand")
    private Boolean hand;

    @Column(name = "zip")
    private Boolean zip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentID")
    private AsuComponent parent;

    @Column(name = "main")
    private Boolean main;

    @Column(name = "reserveStock")
    private Long reserveStock;

    @Column(name = "account")
    private Boolean account;

    @Column(name = "minNorm")
    private Long minNorm;

    @Column(name = "delLong")
    private Long delLong;

    @Column(name = "rawMat")
    private Boolean rawMat;
}