package asu.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы tn_project
 * @author mazur_ea
 * Date:   01.10.2019
 */
@Entity
@Table(name = "tn_project")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuContract implements Serializable {

    @Id
    @Column(name = "ROWID")
    private Long id;

    @Column(name = "cname")
    private String name;

    @Column(name = "cDate")
    private Long cDate;

    @Column(name = "valid")
    private Long valid;
}