package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY;

/**
 * Сущность с описанием таблицы хранения комментариев к изделию (ex. ECOPLAN.COMMENTS)
 * @author zhestkov_an
 * Date:   06.10.2020
 */
@Entity
@Table(name = "product_comment")
@ToString @Setter @Getter
@EqualsAndHashCode(of = "id")
public class ProductComment {

    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "productComment")
    @GenericGenerator(name = "productComment", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // изделие

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // автор

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate; // дата и время создания

    @Column(name = "comment", nullable = false, length = 1024)
    private String comment; // комментарий
}