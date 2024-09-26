package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.PrivilegeGroupService;
import ru.korundm.entity.PrivilegeGroup;
import ru.korundm.repository.PrivilegeGroupRepository;

import java.util.List;

@Service
@Transactional
public class PrivilegeGroupServiceImpl implements PrivilegeGroupService {

    private final PrivilegeGroupRepository privilegeGroupRepository;

    public PrivilegeGroupServiceImpl(PrivilegeGroupRepository privilegeGroupRepository) {
        this.privilegeGroupRepository = privilegeGroupRepository;
    }

    @Override
    public List<PrivilegeGroup> getAll() {
        return privilegeGroupRepository.findAll();
    }

    @Override
    public List<PrivilegeGroup> getAllById(List<Long> idList) {
        return privilegeGroupRepository.findAllById(idList);
    }

    @Override
    public PrivilegeGroup save(PrivilegeGroup object) {
        return privilegeGroupRepository.save(object);
    }

    @Override
    public List<PrivilegeGroup> saveAll(List<PrivilegeGroup> objectList) {
        return privilegeGroupRepository.saveAll(objectList);
    }

    @Override
    public PrivilegeGroup read(long id) {
        return privilegeGroupRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(PrivilegeGroup object) {
        privilegeGroupRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        privilegeGroupRepository.deleteById(id);
    }
}