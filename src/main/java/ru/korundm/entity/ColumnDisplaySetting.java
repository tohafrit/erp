package ru.korundm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы column_display_settings
 * @author pakhunov_an
 * Date:   15.05.2018
 */
@Entity
@Table(name = "column_display_settings")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class ColumnDisplaySetting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id настройки

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user; // пользователь настройки

    @Column(name = "table_id")
    private String tableId; // идентификатор таблицы

    @Column(name = "column_order")
    private Integer order; // порядок следования колонки

    @Column(name = "column_name")
    private String name; // название колонки

    @Column(name = "toggle")
    private Boolean toggle; // переключение отображения колонки
}