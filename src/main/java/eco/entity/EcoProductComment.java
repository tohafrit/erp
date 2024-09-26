package eco.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Сущность с описанием таблицы пользовательских комментарии к изделию с указанием даты и автора
 * @author zhestkov_an
 * Date:   06.10.2020
 */
@Entity
@Table(name = "COMMENTS")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoProductComment implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // идентификатор

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private EcoProduct product; // изделие

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_user")
    private EcoUserInfo user; // автор

    @Column(name = "c_time")
    private LocalDateTime creationDate; // дата и время создания комментария

    @Column(name = "c_body")
    private String comment; // комментарий
}