package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.EquipmentUnitEventTypeService;
import ru.korundm.entity.EquipmentUnitEventType;
import ru.korundm.repository.EquipmentUnitEventTypeRepository;

import java.util.List;

@Service
@Transactional
public class EquipmentUnitEventTypeServiceImpl implements EquipmentUnitEventTypeService {

    private final EquipmentUnitEventTypeRepository equipmentUnitEventTypeRepository;

    public EquipmentUnitEventTypeServiceImpl(EquipmentUnitEventTypeRepository equipmentUnitEventTypeRepository) {
        this.equipmentUnitEventTypeRepository = equipmentUnitEventTypeRepository;
    }

    @Override
    public List<EquipmentUnitEventType> getAll() { return equipmentUnitEventTypeRepository.findAll(); }

    @Override
    public List<EquipmentUnitEventType> getAllById(List<Long> idList) { return equipmentUnitEventTypeRepository.findAllById(idList); }

    @Override
    public EquipmentUnitEventType save(EquipmentUnitEventType object) { return equipmentUnitEventTypeRepository.save(object); }

    @Override
    public List<EquipmentUnitEventType> saveAll(List<EquipmentUnitEventType> objectList) { return equipmentUnitEventTypeRepository.saveAll(objectList); }

    @Override
    public EquipmentUnitEventType read(long id) { return equipmentUnitEventTypeRepository.getOne(id); }

    @Override
    public void delete(EquipmentUnitEventType object) { equipmentUnitEventTypeRepository.delete(object); }

    @Override
    public void deleteById(long id) { equipmentUnitEventTypeRepository.deleteById(id); }
}