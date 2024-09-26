package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.PrivilegeService;
import ru.korundm.entity.Privilege;
import ru.korundm.repository.PrivilegeRepository;

import java.util.List;

@Service
@Transactional
public class PrivilegeServiceImpl implements PrivilegeService {

    private final PrivilegeRepository privilegeRepository;

    public PrivilegeServiceImpl(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    @Override
    public List<Privilege> getAll() {
        return privilegeRepository.findAll();
    }

    @Override
    public List<Privilege> getAllById(List<Long> idList) {
        return privilegeRepository.findAllById(idList);
    }

    @Override
    public Privilege save(Privilege object) {
        return privilegeRepository.save(object);
    }

    @Override
    public List<Privilege> saveAll(List<Privilege> objectList) {
        return privilegeRepository.saveAll(objectList);
    }

    @Override
    public Privilege read(long id) {
        return privilegeRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Privilege object) {
        privilegeRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        privilegeRepository.deleteById(id);
    }
}