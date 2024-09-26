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
 * Сущность с описанием таблицы PRODMODULESTATEONPRODSITE
 * @author pakhunov_an
 * Date:   25.11.2019
 */
@Entity
@Table(name = "PRODMODULESTATEONPRODSITE")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuProdModuleStateOnProdSite implements Serializable {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "personParti")
    private boolean personParti;

    @Column(name = "description")
    private String description;

    @Column(name = "firstOnPS")
    private boolean firstOnPS;
}