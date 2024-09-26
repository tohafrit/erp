package ru.korundm.dao;

import ru.korundm.entity.BomAttribute;

import java.util.List;

public interface BomAttributeService extends CommonService<BomAttribute> {

    List<BomAttribute> getAllByBomId(Long bomId);

    BomAttribute getByLaunchIdAndBomId(Long launchId, Long bomId);
}