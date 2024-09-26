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
 * Сущность с описанием таблицы message_types
 * @author zhestkov_an
 * Date:   17.12.2018
 */
@Entity
@Table(name = "message_types")
@Setter
@Getter
@ToString
@EqualsAndHashCode(of = "id")
public class MessageType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @Column(name = "name", nullable = false, length = 128)
    private String name; // название типа сообщения

    @Column(name = "description", nullable = false, length = 1024)
    private String description; // описание типа сообщения, может хранить передаваемые параментры по шаблону #FIELD_NAME#

    @Column(name = "code", unique = true, nullable = false)
    private String code; // уникальный код типа сообщения

    @OneToMany(mappedBy = "type")
    private List<MessageTemplate> messageTemplateList = new ArrayList<>(); // список шаблонов писем
}