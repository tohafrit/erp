package asu.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы ULISTS
 * @author pakhunov_an
 * Date:   03.12.2019
 */
@Entity
@Table(name = "ULISTS")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuUlist implements Serializable {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "date")
    private long date;

    @OneToMany(mappedBy = "ulist")
    private List<AsuProdModuleInUlist> asuProdModuleInUlistList = new ArrayList<>();

    // Геттеры и сеттеры для корректного взаимодействия с котлин-классами

    public List<AsuProdModuleInUlist> getAsuProdModuleInUlistList() { return asuProdModuleInUlistList; }
}