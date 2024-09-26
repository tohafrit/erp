package ru.korundm.dao;

import ru.korundm.entity.ComponentAttributeValue;

import java.util.List;

public interface ComponentAttributeValueService extends CommonService<ComponentAttributeValue> {

    List<ComponentAttributeValue> getAllByAttributeIdIn(List<Long> idList);
}