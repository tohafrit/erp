package ru.korundm.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Класс для хранения модели глобальных данных
 * @author mazur_ea
 * Date:   02.06.2020
 */
@Getter @Setter
public final class GlobalDataModel {

    private String title;
    private String leftMenu;
    private List<Object> topMenuList;
}