package asu.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы prodmoduleinulists
 * @author zhestkov_an
 * Date:   07.09.2021
 */
@Entity
@Table(name = "PRODMODULEINULISTS")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class AsuProdModuleInUlist implements Serializable {

    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ulistID", nullable = false)
    private AsuUlist ulist; //

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prodModuleID")
    private AsuProdModule prodModule; //

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moduleID")
    private AsuModule module; //

    @Column(name = "posPassport")
    private String posPassport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prodModuleSeatID")
    private AsuProdModuleStateOnProdSite stateOnProdSite; //

    @Column(name = "comments")
    private String comments;

    // Геттеры и сеттеры для корректного взаимодействия с котлин-классами

    public AsuUlist getUlist() { return ulist; }

    public AsuProdModule getProdModule() { return prodModule; }
}