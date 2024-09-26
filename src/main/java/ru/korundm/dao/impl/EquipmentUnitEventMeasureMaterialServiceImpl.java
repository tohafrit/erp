package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.EquipmentUnitEventMeasureMaterialService;
import ru.korundm.entity.Equipment;
import ru.korundm.entity.EquipmentUnit;
import ru.korundm.entity.EquipmentUnitEventMeasureMaterial;
import ru.korundm.repository.EquipmentUnitEventMeasureMaterialRepository;

import java.util.List;

@Service
@Transactional
public class EquipmentUnitEventMeasureMaterialServiceImpl implements EquipmentUnitEventMeasureMaterialService {

    private final EquipmentUnitEventMeasureMaterialRepository equipmentUnitEventMeasureMaterialRepository;

    public EquipmentUnitEventMeasureMaterialServiceImpl(EquipmentUnitEventMeasureMaterialRepository equipmentUnitEventMeasureMaterialRepository) {
        this.equipmentUnitEventMeasureMaterialRepository = equipmentUnitEventMeasureMaterialRepository;
    }

    @Override
    public List<EquipmentUnitEventMeasureMaterial> getAll() { return equipmentUnitEventMeasureMaterialRepository.findAll(); }

    @Override
    public List<EquipmentUnitEventMeasureMaterial> getAllById(List<Long> idList) { return equipmentUnitEventMeasureMaterialRepository.findAllById(idList); }

    @Override
    public EquipmentUnitEventMeasureMaterial save(EquipmentUnitEventMeasureMaterial object) { return equipmentUnitEventMeasureMaterialRepository.save(object); }

    @Override
    public List<EquipmentUnitEventMeasureMaterial> saveAll(List<EquipmentUnitEventMeasureMaterial> objectList) {
        return equipmentUnitEventMeasureMaterialRepository.saveAll(objectList);
    }

    @Override
    public EquipmentUnitEventMeasureMaterial read(long id) { return equipmentUnitEventMeasureMaterialRepository.getOne(id); }

    @Override
    public void delete(EquipmentUnitEventMeasureMaterial object) { equipmentUnitEventMeasureMaterialRepository.delete(object); }

    @Override
    public void deleteById(long id) { equipmentUnitEventMeasureMaterialRepository.deleteById(id); }

    @Override
    public void deleteAllByEquipment(Equipment equipment) {
        equipmentUnitEventMeasureMaterialRepository.deleteAllByEquipmentUnitEventMeasure_EquipmentUnitEvent_EquipmentUnit_Equipment(equipment);
    }

    @Override
    public void deleteAllByEquipmentUnitIn(List<EquipmentUnit> equipmentUnitList) {
        equipmentUnitEventMeasureMaterialRepository.deleteAllByEquipmentUnitEventMeasure_EquipmentUnitEvent_EquipmentUnitIn(equipmentUnitList);
    }

    @Override
    public void deleteAllByEquipmentUnitEventId(Long id) {
        equipmentUnitEventMeasureMaterialRepository.deleteAllByEquipmentUnitEventMeasure_EquipmentUnitEvent_Id(id);
    }
}
