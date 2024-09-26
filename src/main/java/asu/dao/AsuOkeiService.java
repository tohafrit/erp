package asu.dao;

import asu.entity.AsuOkei;
import asu.repository.AsuOkeiRepository;
import org.springframework.stereotype.Service;

@Service
public class AsuOkeiService {

    private final AsuOkeiRepository asuOkeiRepository;

    public AsuOkeiService(AsuOkeiRepository asuOkeiRepository) {
        this.asuOkeiRepository = asuOkeiRepository;
    }

    public AsuOkei read(long id) {
        return asuOkeiRepository.getOne(id);
    }
}