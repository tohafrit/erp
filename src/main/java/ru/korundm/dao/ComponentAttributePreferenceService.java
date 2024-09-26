package ru.korundm.dao;

import ru.korundm.entity.ComponentAttributePreference;

import java.util.List;

public interface ComponentAttributePreferenceService extends CommonService<ComponentAttributePreference> {

    List<ComponentAttributePreference> getAllByAttributeId(Long attributeId);

    List<ComponentAttributePreference> getAllByAttributeIdIn(List<Long> idList);
}