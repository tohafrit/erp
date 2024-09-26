package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.EquipmentUnitProductionAreaService;
import ru.korundm.entity.Equipment;
import ru.korundm.entity.EquipmentUnit;
import ru.korundm.entity.EquipmentUnitProductionArea;
import ru.korundm.repository.EquipmentUnitProductionAreaRepository;

import java.util.List;

@Service
@Transactional
public class EquipmentUnitProductionAreaServiceImpl implements EquipmentUnitProductionAreaService {

    private final EquipmentUnitProductionAreaRepository equipmentUnitProductionAreaRepository;

    public EquipmentUnitProductionAreaServiceImpl(EquipmentUnitProductionAreaRepository equipmentUnitProductionAreaRepository) {
        this.equipmentUnitProductionAreaRepository = equipmentUnitProductionAreaRepository;
    }

    @Override
    public List<EquipmentUnitProductionArea> getAll() { return equipmentUnitProductionAreaRepository.findAll(); }

    @Override
    public List<EquipmentUnitProductionArea> getAllById(List<Long> idList) { return equipmentUnitProductionAreaRepository.findAllById(idList); }

    @Override
    public EquipmentUnitProductionArea save(EquipmentUnitProductionArea object) { return equipmentUnitProductionAreaRepository.save(object); }

    @Override
    public List<EquipmentUnitProductionArea> saveAll(List<EquipmentUnitProductionArea> objectList) { return equipmentUnitProductionAreaRepository.saveAll(objectList); }

    @Override
    public EquipmentUnitProductionArea read(long id) { return equipmentUnitProductionAreaRepository.getOne(id); }

    @Override
    public void delete(EquipmentUnitProductionArea object) { equipmentUnitProductionAreaRepository.delete(object); }

    @Override
    public void deleteById(long id) { equipmentUnitProductionAreaRepository.deleteById(id); }

    @Override
    public void deleteAllByEquipment(Equipment equipment) { equipmentUnitProductionAreaRepository.deleteAllByEquipmentUnit_Equipment(equipment); }

    @Override
    public void deleteAllByEquipmentUnit(EquipmentUnit equipmentUnit) { equipmentUnitProductionAreaRepository.deleteAllByEquipmentUnit(equipmentUnit); }

    @Override
    public void deleteAllByEquipmentUnitIn(List<EquipmentUnit> equipmentUnitList) { equipmentUnitProductionAreaRepository.deleteAllByEquipmentUnitIn(equipmentUnitList); }
}