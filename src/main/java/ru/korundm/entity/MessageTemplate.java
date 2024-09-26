package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы message_templates
 * @author zhestkov_an
 * Date:   17.12.2018
 */
@Entity
@Table(name = "message_templates")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class MessageTemplate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @Column(name = "active", nullable = false)
    private boolean active = true; // актуальность шаблона

    @Column(name = "email_from", nullable = false)
    private String emailFrom; // от кого

    @Column(name = "email_to", nullable = false)
    private String emailTo; // кому

    @Column(name = "subject", nullable = false)
    private String subject; // тема сообщения

    @Column(name = "message", nullable = false)
    private String message; // сообщение

    @Column(name = "cc")
    private String cc; // копия (адреса через пробел)

    @Column(name = "bcc")
    private String bcc; // скрытая копия (адреса через пробел)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private MessageType type; // тип письма

    @OneToMany(mappedBy = "messageTemplate")
    private transient List<MessageHistory> messageHistoryList = new ArrayList<>(); // список историй для писем
}