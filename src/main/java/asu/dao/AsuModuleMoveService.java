package asu.dao;

import asu.entity.AsuModuleMove;
import asu.repository.AsuModuleMoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsuModuleMoveService {

    @Autowired
    private AsuModuleMoveRepository asuModuleMoveRepository;

    public AsuModuleMove read(long id) {
        return asuModuleMoveRepository.getOne(id);
    }
}
