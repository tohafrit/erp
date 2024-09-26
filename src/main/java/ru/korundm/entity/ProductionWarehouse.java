package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.util.CommonUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Сущность с описанием таблицы производственных участков production_warehouses (ex. tab_uchasts)
 * @author pakhunov_an
 * Date:   02.04.2018
 */
@Entity
@Table(name = "production_warehouses")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class ProductionWarehouse {

    /** Длина номера склада */
    private static final int LENGTH_CODE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @NotNull
    @org.hibernate.validator.constraints.Range(max = 99L)
    @Column(name = "code", nullable = false, unique = true, scale = 2)
    private Integer code; // код

    @Size(min = 1, max = 128)
    @Column(name = "name", length = 128, nullable = false, unique = true)
    private String name; // наименование

    /**
     * Метод для получения кода склада с ведущими нулями
     * @return код склада
     */
    public String getFormatCode() {
        return CommonUtil.formatZero(getCode().toString(), LENGTH_CODE);
    }
}