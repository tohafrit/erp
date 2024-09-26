package ru.korundm.dao.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.dao.RoleService;
import ru.korundm.entity.Role;
import ru.korundm.repository.RoleRepository;

import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    @Override
    public List<Role> getAllById(List<Long> idList) {
        return roleRepository.findAllById(idList);
    }

    @Override
    public Role save(Role object) {
        return roleRepository.save(object);
    }

    @Override
    public List<Role> saveAll(List<Role> objectList) {
        return roleRepository.saveAll(objectList);
    }

    @Override
    public Role read(long id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Role object) {
        roleRepository.delete(object);
    }

    @Override
    public void deleteById(long id) {
        roleRepository.deleteById(id);
    }
}