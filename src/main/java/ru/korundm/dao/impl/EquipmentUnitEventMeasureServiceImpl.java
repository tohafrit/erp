package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.EquipmentUnitEventMeasureService;
import ru.korundm.entity.Equipment;
import ru.korundm.entity.EquipmentUnit;
import ru.korundm.entity.EquipmentUnitEventMeasure;
import ru.korundm.repository.EquipmentUnitEventMeasureRepository;

import java.util.List;

@Service
@Transactional
public class EquipmentUnitEventMeasureServiceImpl implements EquipmentUnitEventMeasureService {

    private final EquipmentUnitEventMeasureRepository equipmentUnitEventMeasureRepository;

    public EquipmentUnitEventMeasureServiceImpl(EquipmentUnitEventMeasureRepository equipmentUnitEventMeasureRepository) {
        this.equipmentUnitEventMeasureRepository = equipmentUnitEventMeasureRepository;
    }

    @Override
    public List<EquipmentUnitEventMeasure> getAll() { return equipmentUnitEventMeasureRepository.findAll(); }

    @Override
    public List<EquipmentUnitEventMeasure> getAllById(List<Long> idList) { return equipmentUnitEventMeasureRepository.findAllById(idList); }

    @Override
    public EquipmentUnitEventMeasure save(EquipmentUnitEventMeasure object) { return equipmentUnitEventMeasureRepository.save(object); }

    @Override
    public List<EquipmentUnitEventMeasure> saveAll(List<EquipmentUnitEventMeasure> objectList) { return equipmentUnitEventMeasureRepository.saveAll(objectList); }

    @Override
    public EquipmentUnitEventMeasure read(long id) { return equipmentUnitEventMeasureRepository.getOne(id); }

    @Override
    public void delete(EquipmentUnitEventMeasure object) { equipmentUnitEventMeasureRepository.delete(object); }

    @Override
    public void deleteById(long id) { equipmentUnitEventMeasureRepository.deleteById(id); }

    @Override
    public void deleteAllByEquipment(Equipment equipment) { equipmentUnitEventMeasureRepository.deleteAllByEquipmentUnitEvent_EquipmentUnit_Equipment(equipment); }

    @Override
    public void deleteAllByEquipmentUnitIn(List<EquipmentUnit> equipmentUnitList) {
        equipmentUnitEventMeasureRepository.deleteAllByEquipmentUnitEvent_EquipmentUnitIn(equipmentUnitList);
    }

    @Override
    public void deleteAllByEquipmentUnitEventId(Long id) {
        equipmentUnitEventMeasureRepository.deleteAllByEquipmentUnitEvent_Id(id);
    }
}