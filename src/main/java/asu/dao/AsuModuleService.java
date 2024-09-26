package asu.dao;

import asu.entity.AsuModule;
import asu.repository.AsuModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsuModuleService {

    @Autowired
    AsuModuleRepository asuModuleRepository;

    public AsuModule read(long id) {
        return asuModuleRepository.getOne(id);
    }
}
