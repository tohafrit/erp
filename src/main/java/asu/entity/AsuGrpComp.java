package asu.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы tab_grupcom
 * @author mazur_ea
 * Date:   15.07.2019
 */
@Entity
@Table(name = "tab_grupcom")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuGrpComp implements Serializable {

    @Id
    @Column(name = "ROWID")
    private Long id;

    @Column(name = "nom_grp")
    private Long nomGrp;

    @Column(name = "fractQuan")
    private boolean fractQuan;

    @Column(name = "naz_grp")
    private String nazGrp;
}