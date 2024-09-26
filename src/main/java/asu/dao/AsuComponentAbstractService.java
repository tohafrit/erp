package asu.dao;

import asu.entity.AsuComponentAbstract;
import asu.repository.AsuComponentAbstractRepository;
import org.springframework.stereotype.Service;

@Service
public class AsuComponentAbstractService {

    private final AsuComponentAbstractRepository asuComponentAbstractRepository;

    public AsuComponentAbstractService(AsuComponentAbstractRepository asuComponentAbstractRepository) {
        this.asuComponentAbstractRepository = asuComponentAbstractRepository;
    }

    public AsuComponentAbstract read(long id) {
        return asuComponentAbstractRepository.getOne(id);
    }
}