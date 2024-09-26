package asu.dao;

import asu.entity.AsuComponent;
import asu.repository.AsuComponentRepository;
import org.springframework.stereotype.Service;

@Service
public class AsuComponentService {

    private final AsuComponentRepository asuComponentRepository;

    public AsuComponentService(AsuComponentRepository asuComponentRepository) {
        this.asuComponentRepository = asuComponentRepository;
    }

    public AsuComponent read(long id) {
        return asuComponentRepository.getOne(id);
    }
}