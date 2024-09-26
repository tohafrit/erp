package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.EquipmentTypeService;
import ru.korundm.entity.EquipmentType;
import ru.korundm.repository.EquipmentTypeRepository;

import java.util.List;

@Service
@Transactional
public class EquipmentTypeServiceImpl implements EquipmentTypeService {

    private final EquipmentTypeRepository equipmentTypeRepository;

    public EquipmentTypeServiceImpl(EquipmentTypeRepository equipmentTypeRepository) {
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    @Override
    public List<EquipmentType> getAll() { return equipmentTypeRepository.findAll(); }

    @Override
    public List<EquipmentType> getAllById(List<Long> idList) { return equipmentTypeRepository.findAllById(idList); }

    @Override
    public EquipmentType save(EquipmentType object) { return equipmentTypeRepository.save(object); }

    @Override
    public List<EquipmentType> saveAll(List<EquipmentType> objectList) { return equipmentTypeRepository.saveAll(objectList); }

    @Override
    public EquipmentType read(long id) { return equipmentTypeRepository.getOne(id); }

    @Override
    public void delete(EquipmentType object) { equipmentTypeRepository.delete(object); }

    @Override
    public void deleteById(long id) { equipmentTypeRepository.deleteById(id); }

    @Override
    public EquipmentType readByName(String name) { return equipmentTypeRepository.findEquipmentTypeByName(name); }

    @Override
    public List<EquipmentType> getAllByType(List<String> equipmentNameList) {
        return equipmentTypeRepository.findAllByNameInOrderByIdAsc(equipmentNameList);
    }
}