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
 * Сущность с описанием таблицы tab_wydcomp
 * @author pakhunov_an
 * Date:   26.11.2019
 */
@Entity
@Table(name = "tab_wydcomp")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuCompOut implements Serializable {

    @Id
    @Column(name = "ROWID")
    private Long id;

    @Column(name = "quan_comp")
    private int quanComp;

    @Column(name = "timeOut")
    private long timeOut;

    @Column(name = "timeSold")
    private long timeSold;
}