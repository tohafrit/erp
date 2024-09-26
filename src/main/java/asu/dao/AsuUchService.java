package asu.dao;

import asu.entity.AsuUch;
import asu.repository.AsuUchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsuUchService {

    @Autowired
    private AsuUchRepository asuUchRepository;

    public AsuUch read(long id) {
        return asuUchRepository.getOne(id);
    }

    public List<AsuUch> getAll() {
        return asuUchRepository.findAll();
    }

    public AsuUch getByCode(String code) {
        return asuUchRepository.findTopByCodeOrderByCode(code);
    }

    public boolean existsByCode(String code) {
        return asuUchRepository.existsByCode(code);
    }
}
