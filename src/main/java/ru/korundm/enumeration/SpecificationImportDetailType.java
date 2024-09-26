package ru.korundm.enumeration;

import lombok.Getter;
import ru.korundm.helper.converter.EnumConverter;
import ru.korundm.helper.converter.EnumConvertible;

import javax.persistence.Converter;

@Getter
public enum SpecificationImportDetailType implements EnumConvertible<Long> {

    NEW_COMPONENT(1,"Новый компонент из bom-файла"),
    EXIST_COMPONENT(2, "Компонент из базы данных"),
    MISTAKE_COMPONENT(3, "Ошибка");

    private final long id;
    private final String description;

    SpecificationImportDetailType(long id, String description) {
        this.id = id;
        this.description = description;
    }

    @Converter
    public static class CustomConverter extends EnumConverter<SpecificationImportDetailType, Long> {
        public CustomConverter() { super(SpecificationImportDetailType.class); }
    }

    @Override
    public Long toValue() {
        return this.id;
    }
}