package ru.korundm.form.search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Форма для поиска типов сообщений
 * @author zhestkov_an
 * Date:   05.02.2021
 */
@Getter
@Setter
@ToString
public class MessageTemplateListFilterForm implements Serializable {

    private String messageTypeName; // название типа сообщения
    private Boolean active = true; // актуальность шаблона
    private String subject; // тема сообщения
}