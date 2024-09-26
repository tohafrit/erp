package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Сущность с описанием таблицы message_histories
 * @author zhestkov_an
 * Date:   18.12.2018
 */
@Entity
@Table(name = "message_histories")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class MessageHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "fields")
    private String fields; // поля при отправке

    @Column(name = "departure_date", nullable = false)
    private LocalDateTime departureDate; // дата отправки

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // пользователь

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_template_id", nullable = false)
    private MessageTemplate messageTemplate; // история письма
}
