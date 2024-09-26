package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.util.CommonUtil;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с причинами изменений (подкатегория для Извещений об изменении ТП)
 * @author pakhunov_an
 * Date:   25.10.2019
 */
@Entity
@Table(name = "reason_changes")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class ReasonChange implements Serializable {

    /** Длина кода */
    public static final int LENGTH_CODE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "code", nullable = false)
    private Integer code; // код

    @Column(name = "reason", nullable = false)
    private String reason; // причина

    /**
     * Метод для получения кода с ведущим нулем
     * @return код с ведущим нулем
     */
    public String getCodeZero() {
        return CommonUtil.formatZero(String.valueOf(code), LENGTH_CODE);
    }
}