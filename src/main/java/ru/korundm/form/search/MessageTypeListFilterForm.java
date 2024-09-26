package ru.korundm.form.search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Форма для поиска типов сообщений
 * @author zhestkov_an
 * Date:   04.02.2021
 */
@Getter
@Setter
@ToString
public class MessageTypeListFilterForm implements Serializable {

    private String name; // название типа сообщения
    private String description; // описание типа сообщения, может хранить передаваемые параментры по шаблону #FIELD_NAME#
    private String code; // уникальный код типа сообщения
}